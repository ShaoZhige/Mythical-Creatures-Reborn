package com.shao.mythical_creatures_reborn.entity.custom;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.shao.mythical_creatures_reborn.util.EntityHateFilter;

/**
 * 敌对型小马基类：在 PonyEntity 通用 AI 之外额外加近战攻击与“攻击玩家”目标。
 * 已驯服的个体不会攻击其主人。
 *
 * 实现原版 {@link Enemy} 标记接口（Monster 自身实现的就是它）：让模组敌对生物被原版
 * 中立/防御机制（铁傀儡、雪傀儡等以 Enemy 为目标的系统）以及其它模组的仇恨/索敌逻辑
 * 正确识别为“敌对”，从而兼容原版敌对机制与其它模组的仇恨。Java 单继承下不能在保留
 * TamableAnimal（驯服/骑乘/飞行共用基类）的同时再 extend Monster，故用 Enemy 接口达成
 * 同等的“被识别为敌对”效果，而非改父类链。
 */
public abstract class HostilePonyEntity extends PonyEntity implements Enemy {

    public HostilePonyEntity(EntityType<? extends PonyEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false,
                p -> !EntityHateFilter.shouldIgnore(p) && !(this.isTame() && p == this.getOwner())));
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
