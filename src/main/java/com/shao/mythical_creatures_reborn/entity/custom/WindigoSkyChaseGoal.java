package com.shao.mythical_creatures_reborn.entity.custom;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import com.shao.mythical_creatures_reborn.config.MythicalConfig;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 雪魔飞行追击：常驻悬停 + 保距走位。
 * 框架 tickFlight() 负责无重力与悬停维持（angryFlight 时持续 HOVER 不下落），
 * 本 Goal 每 tick 直接 setDeltaMovement 接管水平靠拢 / 横向走位 / 后撤与高度跟随，
 * 并控制霰弹射击节奏（复用 WindigoEntity.performRangedAttack）。
 * 思路借鉴「保持与目标同层、平滑靠拢、实时朝向」的飞行追击范式，方法为本模组自实现。
 */
public class WindigoSkyChaseGoal extends Goal {

    /* ── 数值来自配置（键 = 本实体注册名；可在编辑器的「生物」分类里改）──
       所有 cfg/cfgInt 都带原硬编码值作 fallback，配置缺失时行为与改动前完全一致。 */
    private String eid() {
        ResourceLocation rl = BuiltInRegistries.ENTITY_TYPE.getKey(this.mob.getType());
        return rl == null ? "" : rl.toString();
    }

    private double cfg(String key, double fallback) {
        return MythicalConfig.DATA.get(eid(), key, fallback);
    }

    private int cfgInt(String key, int fallback) {
        return (int) cfg(key, fallback);
    }
    private final WindigoEntity mob;
    private final double speed;
    private final int attackInterval;
    private final double range;
    private int attackCooldown = 0;
    private int strafeTimer = 0;
    private int strafeDir = 1;


    public WindigoSkyChaseGoal(WindigoEntity mob, double speed, int attackInterval, double range) {
        this.mob = mob;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.range = range;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        // 标记愤怒飞行，框架 tickFlight 维持 HOVER 不降落；进入常驻悬停追击。
        this.mob.flight.angryFlight = true;
        this.mob.setHovering(true);
        this.attackCooldown = 0;
        this.strafeTimer = 0;
        this.strafeDir = this.mob.getRandom().nextBoolean() ? 1 : -1;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) return;

        // 持续维持愤怒悬停（防止框架在边界条件退出悬停导致下坠）
        this.mob.flight.angryFlight = true;
        if (!this.mob.isHovering() && !this.mob.isFlying()) this.mob.setHovering(true);

        double dx = target.getX() - this.mob.getX();
        double dz = target.getZ() - this.mob.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        Vec3 move = Vec3.ZERO;
        if (dist > 1.0E-4D) {
            double nx = dx / dist, nz = dz / dist;
            if (dist > cfg("hover_max_dist", 32.0)) {
                move = new Vec3(nx * speed, 0.0D, nz * speed);        // 过远：靠拢
            } else if (dist < cfg("hover_min_dist", 12.0)) {
                move = new Vec3(-nx * speed, 0.0D, -nz * speed);     // 过近：后撤
            } else {
                if (this.strafeTimer <= 0) {                         // 舒适区间：横向走位
                    this.strafeTimer = 40 + this.mob.getRandom().nextInt(40);
                    if (this.mob.getRandom().nextInt(4) == 0) this.strafeDir *= -1;
                }
                double px = -nz * this.strafeDir;
                double pz = nx * this.strafeDir;
                move = new Vec3(px * speed, 0.0D, pz * speed);
            }
        }

        // 垂直：缓慢跟随目标眼睛高度 + 偏移，悬停贴近但略高于目标
        double targetY = target.getY() + target.getEyeHeight() + cfg("hover_offset", 14.0);
        double dy = targetY - this.mob.getY();
        double vy = Math.max(-0.25D, Math.min(0.25D, dy * 0.06D));

        this.mob.setDeltaMovement(move.x, vy, move.z);

        // 实时朝目标（飞行单位直接设 yaw，不依赖地面 LookControl）
        float yaw = (float) (Math.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        this.mob.setYRot(yaw);
        this.mob.setYHeadRot(yaw);

        // 射击节奏：进入射程且有视线时开火
        if (this.attackCooldown > 0) this.attackCooldown--;
        if (this.attackCooldown <= 0 && dist <= range && this.mob.hasLineOfSight(target)) {
            this.mob.performRangedAttack(target, 1.0F);
            this.attackCooldown = this.attackInterval;
        }

        if (this.strafeTimer > 0) this.strafeTimer--;
    }

    @Override
    public void stop() {
        this.mob.flight.angryFlight = false;
    }
}
