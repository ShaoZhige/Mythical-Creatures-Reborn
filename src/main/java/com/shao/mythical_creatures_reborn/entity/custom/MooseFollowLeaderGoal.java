package com.shao.mythical_creatures_reborn.entity.custom;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import org.jetbrains.annotations.Nullable;

/**
 * 小麋鹿跟随行为：看到附近的成年麋鹿就跑过去跟着它（类似幼年跟随家长）。
 * 进入战斗（已有攻击目标）时停止跟随；成年麋鹿离开过远则放弃，转回游荡。
 */
public class MooseFollowLeaderGoal extends Goal {

    private final BabyMooseEntity baby;
    private final double speed;
    private final double detect;   // 发现成年麋鹿的感知半径
    private final double minDist;  // 跟到多近算到位
    private final double giveUp;   // 超过此距离放弃跟随
    @Nullable
    private AdultMooseEntity leader;
    private int recalc = 0;

    public MooseFollowLeaderGoal(BabyMooseEntity baby, double speed, double detect, double minDist, double giveUp) {
        this.baby = baby;
        this.speed = speed;
        this.detect = detect;
        this.minDist = minDist;
        this.giveUp = giveUp;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.baby.getTarget() != null) return false; // 战斗中不跟随
        this.leader = findLeader();
        return this.leader != null && this.baby.distanceToSqr(this.leader) > this.minDist * this.minDist;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.baby.getTarget() != null) return false;
        if (this.leader == null || !this.leader.isAlive()) return false;
        double d = this.baby.distanceToSqr(this.leader);
        return d > this.minDist * this.minDist && d < this.giveUp * this.giveUp;
    }

    @Override
    public void start() {
        if (this.leader != null) {
            this.baby.getNavigation().moveTo(this.leader, this.speed);
        }
    }

    @Override
    public void tick() {
        if (this.leader == null) return;
        if (--this.recalc <= 0) {
            this.recalc = 10;
            this.baby.getNavigation().moveTo(this.leader, this.speed);
        }
        this.baby.getLookControl().setLookAt(this.leader, 10.0F, (float) this.baby.getMaxHeadXRot());
    }

    @Override
    public void stop() {
        this.baby.getNavigation().stop();
    }

    @Nullable
    private AdultMooseEntity findLeader() {
        double bestDistSq = this.detect * this.detect;
        AdultMooseEntity best = null;
        for (Entity e : this.baby.level().getEntitiesOfClass(AdultMooseEntity.class,
                this.baby.getBoundingBox().inflate(this.detect))) {
            if (e == this.baby) continue;
            double d = this.baby.distanceToSqr(e);
            if (d < bestDistSq) {
                bestDistSq = d;
                best = (AdultMooseEntity) e;
            }
        }
        return best;
    }
}
