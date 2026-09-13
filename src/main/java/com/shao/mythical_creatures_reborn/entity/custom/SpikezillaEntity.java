package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.sound.ModSounds;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import com.shao.mythical_creatures_reborn.util.EntityHateFilter;
import org.jetbrains.annotations.Nullable;

public class SpikezillaEntity extends HostilePonyEntity {

    public SpikezillaEntity(EntityType<SpikezillaEntity> type, Level level) {
        super(type, level);
    }

    // 穗龙斯拉为地面近战巨龙：不飞行（无 fly 动画，避免飞行时找不到动画）。
    @Override protected boolean canFly() { return false; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() { return ModSounds.SPIKEZILLA_AMBIENT.get(); }
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return ModSounds.SPIKEZILLA_HURT.get(); }
    @Override protected net.minecraft.sounds.SoundEvent getDeathSound() { return ModSounds.SPIKEZILLA_DEATH.get(); }

    /**
     * 攻击 AI：
     *  · 移动层：用自定义的"扇形横扫"替换基类默认的贴身单体近战（MeleeAttackGoal）。
     *    横扫能在身前 ±60°、半径 5.5 格内对多个目标同时造成高额伤害 + 强击退。
     *  · 目标层：与雪魔（WindigoEntity）完全对齐 —— 攻击**一切见到的生物**，
     *    玩家 / 动物 / 村民 / 其它怪物全打，不分敌我阵营。
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 移除 HostilePonyEntity 挂上的普通近战，换成范围横扫
        this.goalSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof MeleeAttackGoal);
        this.goalSelector.addGoal(3, new SpikezillaSweepGoal(this));

        // 目标：攻击一切见到的生物（与雪魔同款写法）。
        // 排除项统一由 EntityHateFilter 处理 —— DuMmmMmmy 试验假人、原版盔甲架；
        // 物品展示框继承自 Entity 而非 LivingEntity，本就不会被这条索敌选中，天然安全。
        // 已驯服的个体不攻击其主人。移除基类"只打玩家"的目标，改由本条通吃。
        this.targetSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof NearestAttackableTargetGoal);
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                p -> !EntityHateFilter.shouldIgnore(p) && !(this.isTame() && p == this.getOwner())));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PonyAttributes.of("mythical_creatures_reborn:spikezilla");
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {}
}
