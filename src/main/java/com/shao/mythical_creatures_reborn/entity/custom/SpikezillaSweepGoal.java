package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.util.EntityHateFilter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 穗龙斯拉的横扫攻击：锁定目标后走到挥击距离内，对身前扇形范围（朝向 ±60°、半径 5 格）
 * 内的所有敌对目标同时造成高额伤害 + 强击退；单次挥击对每个实体只结算一次。
 *
 * 与普通 MeleeAttackGoal 的区别：
 *  · 攻击判定是"扇形多目标横扫"，不是贴身单体；
 *  · 伤害倍率可按体型上调（巨型 boss 的一巴掌很痛）；
 *  · 击退在常规 ATTACK_KNOCKBACK 之外再叠加一个水平冲量，产生"被打飞"的压迫感。
 *
 * 设计：占用 MOVE + LOOK 旗标，优先级(3) 与近战同档（本 Goal 替换了默认 MeleeAttackGoal）。
 * 攻击间隔用冷却控制（约 0.9 秒），避免逐帧连击。已驯服个体不误伤主人。
 * 前摇开始时通过 PonyEntity.setAttackAnimation 播放 attack 动画（同步到客户端），播完自动交还移动动画。
 *
 * Spikezilla sweep attack: after reaching melee range, damages and heavily knocks back every
 * hostile living entity within a forward sector (±60°, radius 5). One hit per entity per swing.
 */
public class SpikezillaSweepGoal extends Goal {

    // ── 可调参数 ──────────────────────────────────────────────────────
    /**
     * 有效攻击半径：覆盖自身碰撞箱半宽 + 近战余量（格）。
     * 穗龙斯拉碰撞箱很宽（12），地面生物被推到约半宽（6）处；若用固定小半径（如 5.5）则永远够不到
     * 脚下的目标 —— 横扫既不触发、攻击动画（setAttackAnimation）也从不置位。半径必须 >= 半宽 + 余量，
     * 让贴身 / 脚下的地面生物也能被打到。随碰撞箱尺寸自动适配。
     */
    private double effectiveReach() {
        return this.mob.getBbWidth() * 0.5D + 2.0D;
    }
    /** 横扫扇形半角（度）：以实体朝向为中心，向前 ±60° 内命中 */
    private static final double SWEEP_HALF_ANGLE = 60.0D;
    /** 伤害倍率：横扫伤害 = 实体攻击力 × 此倍率 */
    private static final double DAMAGE_MULT = 1.6D;
    /** 额外水平击退冲量（叠加在原版击退之上，方块/tick） */
    private static final double EXTRA_KNOCKBACK = 1.4D;
    /** 额外竖直击退（略微把人挑起，让"打飞"更明显） */
    private static final double EXTRA_KNOCKBACK_Y = 0.4D;
    /** 攻击冷却（tick）：约 0.9 秒挥一次 */
    private static final int ATTACK_COOLDOWN = 18;
    /** 挥击前的蓄力/前摇（tick），到点才结算伤害，让动作有节奏 */
    private static final int WINDUP = 6;
    /** 攻击动画时长（tick）：导出动画长 0.8s=16t，覆盖前摇 + 挥击 + 收势 */
    private static final int ATTACK_ANIM_TICKS = 16;

    private final SpikezillaEntity mob;
    private int attackCooldown;
    private int windup;          // >0 表示正在前摇，倒计时到 0 结算本次挥击
    private int animTicks;       // >0 表示攻击动画还在播，倒数到 0 交还移动动画

    public SpikezillaSweepGoal(SpikezillaEntity mob) {
        this.mob = mob;
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
        this.windup = 0;
    }

    @Override
    public void stop() {
        this.windup = 0;
        this.animTicks = 0;
        // 目标丢失/死亡时确保攻击动画不残留
        this.mob.setAttackAnimation(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) return;

        if (this.attackCooldown > 0) this.attackCooldown--;

        // 攻击动画倒计时：播完（或被中断）即交还移动动画
        if (this.animTicks > 0 && --this.animTicks == 0) {
            this.mob.setAttackAnimation(false);
        }

        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double distSqr = this.mob.distanceToSqr(target);

        // 前摇中：身体朝目标、走到位就结算
        if (this.windup > 0) {
            this.mob.getNavigation().stop(); // 前摇时站定发力
            this.windup--;
            if (this.windup == 0) {
                sweep();
                this.attackCooldown = ATTACK_COOLDOWN;
            }
            return;
        }

        double reachSqr = effectiveReach() * effectiveReach();
        if (distSqr <= reachSqr) {
            // 目标已进入挥击范围：停下 → 起前摇（同时开始播 attack 动画）
            this.mob.getNavigation().stop();
            if (this.attackCooldown <= 0) {
                this.windup = WINDUP;
                this.animTicks = ATTACK_ANIM_TICKS;
                this.mob.setAttackAnimation(true);
            }
        } else {
            // 距离不够：朝目标移动（速度用移动速度属性）
            this.mob.getNavigation().moveTo(target, 1.2D);
        }
    }

    /** 结算一次横扫：对身前扇形内所有敌对目标造成伤害 + 击退。 */
    private void sweep() {
        double dmg = this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE) * DAMAGE_MULT;
        Vec3 origin = this.mob.position();
        Vec3 look = this.mob.getLookAngle();
        Vec3 flatLook = new Vec3(look.x, 0.0D, look.z);
        if (flatLook.lengthSqr() < 1.0E-6D) flatLook = new Vec3(0.0D, 0.0D, 1.0D);
        flatLook = flatLook.normalize();
        double cosHalf = Math.cos(Math.toRadians(SWEEP_HALF_ANGLE));

        double reach = effectiveReach();
        for (Entity e : this.mob.level().getEntities(this.mob,
                this.mob.getBoundingBox().inflate(reach))) {
            if (!(e instanceof LivingEntity living)) continue;
            if (!isHostileTo(living)) continue;
            if (living.distanceToSqr(this.mob) > reach * reach) continue;

            // 扇形判定：与朝向的水平夹角在 ±SWEEP_HALF_ANGLE 内
            Vec3 to = new Vec3(living.getX() - origin.x, 0.0D, living.getZ() - origin.z);
            if (to.lengthSqr() > 1.0E-6D) {
                Vec3 toN = to.normalize();
                if (flatLook.dot(toN) < cosHalf) continue;
            }

            living.hurt(this.mob.damageSources().mobAttack(this.mob), (float) dmg);
            applyKnockback(living);
        }
    }

    /** 目标是否可被攻击（排除被仇恨过滤的实体、主人、同类友方）。 */
    private boolean isHostileTo(LivingEntity living) {
        if (!living.isAlive()) return false;
        if (EntityHateFilter.shouldIgnore(living)) return false;
        if (living == this.mob) return false;
        if (this.mob.isTame() && living == this.mob.getOwner()) return false;
        if (living.isAlliedTo(this.mob)) return false;
        return true;
    }

    /** 施加额外水平击退 + 轻微挑起，方向为从穗龙斯拉指向目标。 */
    private void applyKnockback(LivingEntity living) {
        Vec3 dir = new Vec3(living.getX() - this.mob.getX(), 0.0D, living.getZ() - this.mob.getZ());
        if (dir.lengthSqr() < 1.0E-6D) dir = this.mob.getLookAngle();
        dir = dir.normalize();
        Vec3 cur = living.getDeltaMovement();
        living.setDeltaMovement(cur.x + dir.x * EXTRA_KNOCKBACK,
                                cur.y + EXTRA_KNOCKBACK_Y,
                                cur.z + dir.z * EXTRA_KNOCKBACK);
        living.hurtMarked = true; // 强制同步速度到客户端，击退才可见
    }
}
