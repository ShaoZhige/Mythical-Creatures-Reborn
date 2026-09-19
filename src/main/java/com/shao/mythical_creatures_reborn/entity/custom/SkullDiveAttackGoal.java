package com.shao.mythical_creatures_reborn.entity.custom;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import com.shao.mythical_creatures_reborn.config.MythicalConfig;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 末日颅骨的"俯冲近战"攻击 Goal —— 行为对齐原版幻翼 / 恼鬼：
 * 平时由 {@link PonyFlight} 悬停在目标上方（angryFlight 会持续给悬停续命），发现目标进入触发距离后，
 * 本 Goal 接管移动，笔直朝目标俯冲；接触即造成一次近战伤害（走 ATTACK_DAMAGE 属性），
 * 随后拉起爬升，交还悬停状态机，循环往复。
 *
 * 【为什么要 setDiving + 跳过 tickFlight】PonyFlight 的悬停状态机每 tick 都会写 deltaMovement
 * （悬停时把 y 速度清零）。本 Goal 也在写 deltaMovement，二者会互相覆盖。因此俯冲/爬升期间调用
 * {@link SkullOfDoomEntity#setDiving(boolean)} 让实体跳过 tickFlight()，由本 Goal 独占移动；
 * Goal 结束时复位，状态机自然接手（同一 tick 内 tickFlight 会把 noGravity 重新置回 true，不会出现坠落空档）。
 *
 * Dive-melee attack goal for the Skull of Doom — phantom/vex-like: it hovers above the target
 * (driven by PonyFlight's angry hover) and this goal takes over movement to swoop straight at the
 * target; a melee hit is dealt on contact, then it climbs back up and hands control back.
 */
public class SkullDiveAttackGoal extends Goal {

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

    private final SkullOfDoomEntity mob;

    /** 命中后攻击动画保持时长（tick） */
    private static final int ATTACK_ANIM_TICKS = 10;

    private enum Phase { DIVE, CLIMB, DONE }

    private Phase phase = Phase.DONE;
    private int timer;
    private int cooldown;
    private int animTicks;

    public SkullDiveAttackGoal(SkullOfDoomEntity mob) {
        this.mob = mob;
        // 占用移动与朝向：抢占漫步 / 看玩家等低优先级 Goal，避免俯冲时被打断或被拖拽
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) { this.cooldown--; return false; }
        if (!this.mob.isAlive() || this.mob.isOrderedToSit()) return false;
        // 必须已在空中（起飞/悬停）；地面状态交给飞行状态机的"愤怒起飞"先把它带起来
        if (!(this.mob.isFlying() || this.mob.isHovering())) return false;
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive() || !this.mob.canAttack(target)) return false;
        return this.mob.distanceTo(target) <= cfg("dive_trigger_range", 20.0);
    }

    @Override
    public boolean canContinueToUse() {
        return this.phase != Phase.DONE;
    }

    @Override
    public void start() {
        this.phase = Phase.DIVE;
        this.timer = 0;
        this.mob.setDiving(true);        // 让实体跳过蜜蜂式悬停状态机，改由本 Goal 独占移动
        this.mob.setNoGravity(true);
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.phase = Phase.DONE;
        this.timer = 0;
        this.mob.setDiving(false);
        if (this.animTicks > 0) { this.animTicks = 0; this.mob.setAttackAnimation(false); }
        this.cooldown = cfgInt("dive_cooldown", 15);
    }

    @Override
    public void tick() {
        if (this.animTicks > 0 && --this.animTicks == 0) this.mob.setAttackAnimation(false);

        LivingEntity target = this.mob.getTarget();
        switch (this.phase) {
            case DIVE -> {
                if (target == null || !target.isAlive() || !this.mob.canAttack(target)) { beginClimb(); return; }
                // 瞄向目标身体中心（脚底 + 半高），保证撞击点落在身体上而非脚下
                Vec3 aim = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ());
                Vec3 delta = aim.subtract(this.mob.position());
                double reach = (this.mob.getBbWidth() + target.getBbWidth()) * 0.5D + cfg("dive_hit_inflate", 1.0);
                if (delta.lengthSqr() <= reach * reach) {
                    // 接触：造成一次近战伤害并播放攻击动画
                    if (this.mob.doHurtTarget(target)) {
                        this.mob.setAttackAnimation(true);
                        this.animTicks = ATTACK_ANIM_TICKS;
                    }
                    beginClimb();
                } else if (++this.timer >= cfgInt("dive_max_ticks", 50)) {
                    beginClimb();
                } else {
                    this.mob.setDeltaMovement(delta.normalize().scale(cfg("dive_speed", 0.6)));
                    this.mob.getLookControl().setLookAt(target, 40.0F, 40.0F);
                }
            }
            case CLIMB -> {
                this.mob.setDeltaMovement(0.0D, cfg("climb_speed", 0.32), 0.0D);
                if (++this.timer >= cfgInt("climb_ticks", 30)) this.phase = Phase.DONE;
            }
            default -> { }
        }
    }

    /** 切换到"拉起爬升"阶段：给一点向上速度脱离目标，随后交还悬停状态机 */
    private void beginClimb() {
        this.phase = Phase.CLIMB;
        this.timer = 0;
        this.mob.setDeltaMovement(0.0D, cfg("climb_speed", 0.32), 0.0D);
    }
}
