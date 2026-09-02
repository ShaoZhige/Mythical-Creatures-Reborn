package com.shao.mythical_creatures_reborn.entity.custom;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import org.jetbrains.annotations.Nullable;

/**
 * 麋鹿主动索敌：任何进入「挑衅半径」内的非麋鹿生物都会被锁定为目标（族群有攻击性）。
 * 已锁定有效猎物时不再重新挑选，交给近战目标持续追击；猎物死亡或为空时再就近刷新。
 * 永远不会把麋鹿本身设为目标，保证族群之间、同族群内部互不攻击。
 */
public class MooseProximityTargetGoal extends TargetGoal {

    /** 挑衅半径：生物进入此距离即被主动攻击 */
    private final double radius;
    @Nullable
    private LivingEntity cachedTarget;

    public MooseProximityTargetGoal(Mob mob, double radius) {
        super(mob, true, false);
        this.radius = radius;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity cur = this.mob.getTarget();
        if (cur != null && cur.isAlive() && !MooseHerd.isMoose(cur)) {
            return false; // 已有有效猎物，继续追，不抢目标
        }
        LivingEntity nearest = findNearest();
        if (nearest != null) {
            this.cachedTarget = nearest;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        if (this.cachedTarget != null) {
            this.mob.setTarget(this.cachedTarget);
        }
    }

    @Override
    public boolean canContinueToUse() {
        // 一次性设定目标后让出，由近战目标接管；此处不再持续占用
        return false;
    }

    @Nullable
    private LivingEntity findNearest() {
        AABB box = this.mob.getBoundingBox().inflate(this.radius);
        LivingEntity best = null;
        double bestDistSq = this.radius * this.radius;
        for (LivingEntity e : this.mob.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (e == this.mob) continue;
            if (MooseHerd.isMoose(e)) continue;                       // 不攻击同类
            if (e instanceof Player p && p.isCreative()) continue;    // 不主动攻击创造模式玩家
            if (this.mob instanceof TamableAnimal ta && ta.isTame() && e == ta.getOwner()) continue; // 不攻击主人
            double d = this.mob.distanceToSqr(e);
            if (d < bestDistSq) {
                bestDistSq = d;
                best = e;
            }
        }
        return best;
    }
}
