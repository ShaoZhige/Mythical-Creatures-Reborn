package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import com.shao.mythical_creatures_reborn.util.EntityHateFilter;

public class BabyMooseEntity extends HostilePonyEntity {

    public BabyMooseEntity(EntityType<BabyMooseEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void refreshConfigAttributes() {
        applyCoreStats(entityId(), canFly());
    }

    @Override protected boolean canFly() { return false; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() { return null; }
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return null; }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:baby_moose", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:baby_moose", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:baby_moose", "attack_damage"))
                .add(Attributes.ARMOR, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:baby_moose", "armor"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {}

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 移除基类(HostilePonyEntity)挂的「只打玩家」目标，改由麋鹿通用索敌接管
        this.targetSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof NearestAttackableTargetGoal);
        // 小麋鹿跟随最近的成年麋鹿：看到就跟着跑（战斗时自动让位给攻击）
        this.goalSelector.addGoal(4, new MooseFollowLeaderGoal(this, 1.0D, 16.0D, 3.0D, 24.0D));
        // 过近的非麋鹿生物全部视为威胁，主动攻击
        this.targetSelector.addGoal(5, new MooseProximityTargetGoal(this, 6.0D));
    }

    /**
     * 被非麋鹿攻击时，自身锁定攻击者并向族群广播仇恨（猪灵式联动）；
     * 攻击者为麋鹿时不广播，保证族群之间互不攻击。
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean tookDamage = super.hurt(source, amount);
        if (tookDamage && !this.level().isClientSide()) {
            Entity attacker = source.getEntity();
            // 创造模式玩家、被排除实体（假人 / 盔甲架 / 展示框）不触发仇恨：不锁定、也不向族群广播
            if (attacker instanceof LivingEntity living && !MooseHerd.isMoose(attacker)
                    && !(attacker instanceof Player player && player.isCreative())
                    && !EntityHateFilter.shouldIgnore(living)) {
                this.setTarget(living);
                MooseHerd.alert(living, this.level(), this.getX(), this.getY(), this.getZ());
            }
        }
        return tookDamage;
    }
}
