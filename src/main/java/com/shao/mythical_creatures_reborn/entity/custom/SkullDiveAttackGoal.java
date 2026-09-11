package com.shao.mythical_creatures_reborn.entity.custom;

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

    private final SkullOfDoomEntity mob;

    /** 俯冲速度（方块/tick） */
    private static final double DIVE_SPEED = 0.6D;
    /** 拉起爬升速度（方块/tick） */
    private static final double CLIMB_SPEED = 0.32D;
    /** 进入俯冲的最大距离（格） */
    private static final double TRIGGER_RANGE = 20.0D;
    /** 单次俯冲最长时间（tick），超时自动拉起，避免追不上时贴地打转 */
    private static final int MAX_DIVE_TICKS = 50;
    /** 命中 / 超时后的拉起爬升时长（tick） */
    private static final int CLIMB_TICKS = 30;
    /** 两次俯冲之间的冷却（tick） */
    private static final int COOLDOWN_TICKS = 15;
    /** 命中后攻击动画保持时长（tick） */
    private static final int ATTACK_ANIM_TICKS = 10;
    /** 命中判定在双方碰撞箱半宽基础上额外放宽的距离（格） */
    private static final double HIT_INFLATE = 1.0D;

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
        return this.mob.distanceTo(target) <= TRIGGER_RANGE;
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
        this.cooldown = COOLDOWN_TICKS;
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
                double reach = (this.mob.getBbWidth() + target.getBbWidth()) * 0.5D + HIT_INFLATE;
                if (delta.lengthSqr() <= reach * reach) {
                    // 接触：造成一次近战伤害并播放攻击动画
                    if (this.mob.doHurtTarget(target)) {
                        this.mob.setAttackAnimation(true);
                        this.animTicks = ATTACK_ANIM_TICKS;
                    }
                    beginClimb();
                } else if (++this.timer >= MAX_DIVE_TICKS) {
                    beginClimb();
                } else {
                    this.mob.setDeltaMovement(delta.normalize().scale(DIVE_SPEED));
                    this.mob.getLookControl().setLookAt(target, 40.0F, 40.0F);
                }
            }
            case CLIMB -> {
                this.mob.setDeltaMovement(0.0D, CLIMB_SPEED, 0.0D);
                if (++this.timer >= CLIMB_TICKS) this.phase = Phase.DONE;
            }
            default -> { }
        }
    }

    /** 切换到"拉起爬升"阶段：给一点向上速度脱离目标，随后交还悬停状态机 */
    private void beginClimb() {
        this.phase = Phase.CLIMB;
        this.timer = 0;
        this.mob.setDeltaMovement(0.0D, CLIMB_SPEED, 0.0D);
    }
}
