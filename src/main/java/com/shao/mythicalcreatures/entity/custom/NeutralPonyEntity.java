package com.shao.mythicalcreatures.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;

/**
 * 友好小马基类（中立）：在 PonyEntity 通用行为之上，额外让小马主动攻击模组里的敌对生物（防御行为）。
 * 仍继承 PonyEntity，因此保留驯服 / 骑乘 / 飞行能力。
 */
public abstract class NeutralPonyEntity extends PonyEntity {

    public NeutralPonyEntity(EntityType<? extends PonyEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 主动索敌模组里的敌对生物（HostilePonyEntity 是所有敌对模组的共同父类）
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, HostilePonyEntity.class, false));
    }
}
