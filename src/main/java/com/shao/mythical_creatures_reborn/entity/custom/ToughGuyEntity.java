package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.sound.ModSounds;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ToughGuyEntity extends NeutralPonyEntity {

    public ToughGuyEntity(EntityType<ToughGuyEntity> type, Level level) {
        super(type, level);
    }

    @Override protected boolean canFly() { return false; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    /**
     * 攻击 AI（2026-09-19 新增）。
     *
     * <p>硬汉此前直接继承 {@link PonyEntity}，**只有被动反击**，bbmodel 里做好的 {@code attack}
     * 动画在游戏里永远不会播。现在改为继承 {@link NeutralPonyEntity}（中立生物基类），
     * 于是同时具备：</p>
     * <ul>
     *   <li>**中立**：不会无缘无故攻击玩家/动物；</li>
     *   <li>**主动攻击敌对生物**：{@link NeutralPonyEntity} 已挂上「打模组内一切
     *       {@link HostilePonyEntity}」+「打原版 {@code Enemy}（僵尸/骷髅/苦力怕…）」两条目标，
     *       与其它 7 只小马完全一致 —— 这就是需求里的「包括本模组内的敌对生物」；</li>
     *   <li>本类只额外补一个近战 Goal（中立基类本身不带近战手段）。</li>
     * </ul>
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
    }

    /** 有专属 attack 动画片段（toughguy.animation.json 的 attack）。 */
    @Override protected boolean hasAttackAnimation() { return true; }

    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() { return ModSounds.TOUGH_GUY_AMBIENT.get(); }
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return ModSounds.TOUGH_GUY_HURT.get(); }
    @Override protected net.minecraft.sounds.SoundEvent getDeathSound() { return ModSounds.TOUGH_GUY_DEATH.get(); }

    public static AttributeSupplier.Builder createAttributes() {
        return PonyAttributes.of("mythical_creatures_reborn:tough_guy");
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {}
}
