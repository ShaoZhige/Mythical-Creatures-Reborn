package com.shao.mythical_creatures_reborn.entity.custom;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import com.shao.mythical_creatures_reborn.util.EntityHateFilter;

/**
 * 敌对型小马基类：在 PonyEntity 通用 AI 之外额外加近战攻击与**全面敌对**的索敌目标。
 * 已驯服的个体不会攻击其主人。
 *
 * <p>所谓"全面敌对"= 攻击一切见到的生物（玩家 / 动物 / 村民 / 其它怪物，**含本模组内的生物**），
 * 与雪魔（{@link WindigoEntity}）完全同款，不分阵营。需要更克制的索敌的子类会先调用本方法、
 * 再 {@code removeIf} 掉 {@link NearestAttackableTargetGoal} 后挂上自己的目标
 * （雪魔冲锋 / 穗龙斯拉横扫 / 末日颅骨排除亡灵 / 麋鹿邻近+族群广播）。</p>
 *
 * 实现原版 {@link Enemy} 标记接口（Monster 自身实现的就是它）：让模组敌对生物被原版
 * 中立/防御机制（铁傀儡、雪傀儡等以 Enemy 为目标的系统）以及其它模组的仇恨/索敌逻辑
 * 正确识别为"敌对"，从而兼容原版敌对机制与其它模组的仇恨。Java 单继承下不能在保留
 * TamableAnimal（驯服/骑乘/飞行共用基类）的同时再 extend Monster，故用 Enemy 接口达成
 * 同等的"被识别为敌对"效果，而非改父类链。
 */
public abstract class HostilePonyEntity extends PonyEntity implements Enemy {

    public HostilePonyEntity(EntityType<? extends PonyEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
        // 目标：全面敌对 —— 通吃一切见到的生物（与雪魔同款写法）。
        // 排除项统一由 EntityHateFilter 处理：原版盔甲架、DuMmmMmmy 试验假人、创造与旁观玩家。
        // 物品展示框继承自 Entity 而非 LivingEntity，本就不会被这条索敌选中，天然安全。
        // 已驯服的个体不攻击其主人。
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                p -> !EntityHateFilter.shouldIgnore(p) && !(this.isTame() && p == this.getOwner())));
    }

    /**
     * 全部敌对生物都导出了 {@code attack} 片段，因此默认开启"近战命中播 attack"。
     * <p>没有该片段的个别子类（如末日颅骨）会显式覆写回 false。</p>
     */
    @Override
    protected boolean hasAttackAnimation() { return true; }

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
