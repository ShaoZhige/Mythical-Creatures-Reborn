package com.shao.mythicalcreatures.entity.custom;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 敌对型小马基类：在 PonyEntity 通用 AI 之外额外加近战攻击与“攻击玩家”目标。
 * 已驯服的个体不会攻击其主人。
 */
public abstract class HostilePonyEntity extends PonyEntity {

    public HostilePonyEntity(EntityType<? extends PonyEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false,
                p -> !(this.isTame() && p == this.getOwner())));
    }

    /**
     * 和平难度下自动移除（模仿原版 Monster 行为）：让“真正的敌对生物”在和平模式消失。
     * 已驯服的个体保留，避免误删玩家的宠物。
     */
    @Override
    public void tick() {
        if (!this.level().isClientSide() && this.level().getDifficulty() == Difficulty.PEACEFUL && !this.isTame()) {
            this.discard();
            return;
        }
        super.tick();
    }
}
