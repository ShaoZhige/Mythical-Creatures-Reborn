package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import com.shao.mythical_creatures_reborn.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import com.shao.mythical_creatures_reborn.util.EntityHateFilter;

public class AdultMooseEntity extends HostilePonyEntity {

    public AdultMooseEntity(EntityType<AdultMooseEntity> type, Level level) {
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
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:adult_moose", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:adult_moose", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:adult_moose", "attack_damage"))
                .add(Attributes.ARMOR, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:adult_moose", "armor"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {}

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 移除基类(HostilePonyEntity)挂的「只打玩家」目标，改由麋鹿通用索敌接管
        this.targetSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof NearestAttackableTargetGoal);
        // 过近的非麋鹿生物全部视为威胁，主动攻击
        this.targetSelector.addGoal(5, new MooseProximityTargetGoal(this, 6.0D));
        // 冲锋技能：有仇恨时随机触发，高速撞向目标并破坏沿途锄/斧可采方块、对实体造成高额伤害
        this.goalSelector.addGoal(2, new MooseChargeGoal(this));
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

    /**
     * 自然/刷怪蛋生成时，固定带 2~4 只小麋鹿组成家族单位。
     * 小麋鹿再由自身 AI 跟随成年麋鹿，形成稳定族群。
     */
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                 MobSpawnType reason, SpawnGroupData spawnData,
                                 CompoundTag dataTag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        // 仅在自然生成 / 区块生成时附带小麋鹿组成种群；刷怪蛋、指令、结构等非自然来源只生成单只大麋鹿
        if (!level.isClientSide() && (reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION)) {
            int count = 2 + level.getRandom().nextInt(3); // 2,3,4
            for (int i = 0; i < count; i++) {
                BabyMooseEntity baby = ModEntities.BABY_MOOSE.get().create(level.getLevel());
                if (baby == null) continue;
                double ox = (level.getRandom().nextDouble() - 0.5) * 4.0;
                double oz = (level.getRandom().nextDouble() - 0.5) * 4.0;
                baby.moveTo(this.getX() + ox, this.getY(), this.getZ() + oz,
                        level.getRandom().nextFloat() * 360.0F, 0.0F);
                level.addFreshEntity(baby);
            }
        }
        return result;
    }
}
