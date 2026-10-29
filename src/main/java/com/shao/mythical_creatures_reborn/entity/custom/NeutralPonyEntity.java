package com.shao.mythical_creatures_reborn.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;
import com.shao.mythical_creatures_reborn.util.EntityHateFilter;

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
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, HostilePonyEntity.class, false,
                EntityHateFilter.targetable()));
        // 同时主动攻击原版敌对生物（实现 Enemy 接口：僵尸/骷髅/苦力怕等），
        // 使小马的中立/防御仇恨与原版中立生物（如狼、北极熊）一致：被动但会反击/驱赶威胁。
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                e -> e instanceof net.minecraft.world.entity.monster.Enemy && !EntityHateFilter.shouldIgnore(e)));
    }
}
