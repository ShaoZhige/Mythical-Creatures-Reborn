package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import com.shao.mythical_creatures_reborn.sound.ModSounds;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class PrinceRutherfordEntity extends HostilePonyEntity {

    // 物理碰撞箱（与 EntityType 注册的 sized(5.5, 7.5) 一致）：仅用于物理碰撞/寻路/投掷物命中。
    private static final float BB_WIDTH = 5.5F;
    private static final float BB_HEIGHT = 7.5F;

    // 逻辑宽度（只喂给原版近战攻击距离公式）：原版 MeleeAttackGoal.getAttackReachSqr =
    // (2×攻击者宽)² + 目标宽。目标宽这一项是线性补偿，对 5.5 宽的 Prince 只补偿 5.5，
    // 导致攻击者攻击半径 ≈ sqrt(4×攻击者宽² + 5.5)——以监守者(宽 0.9)为例仅 2.96 格，
    // 而两个碰撞箱贴脸时中心距已达 0.45 + 2.75 = 3.2 格，攻击者被 Prince 的大碰撞箱挡在
    // 攻击范围外，"贴脸也打不到"。把逻辑宽度抬到 9（覆盖最小近战生物蠹虫宽 0.4 所需的 8.06），
    // 让攻击者贴脸即可挥击命中；不用雪魔那套 400 是因为 Prince 有近战、会被围攻，400 会让
    // 攻击者隔空约 20 格就挥拳，视觉违和。物理碰撞仍走 makeBoundingBox 的常量框，不受影响。
    private static final float LOGIC_WIDTH = 9.0F;

    public PrinceRutherfordEntity(EntityType<PrinceRutherfordEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(LOGIC_WIDTH, BB_HEIGHT);
    }

    @Override
    protected AABB makeBoundingBox() {
        double hw = BB_WIDTH * 0.5;
        return new AABB(this.getX() - hw, this.getY(), this.getZ() - hw,
                        this.getX() + hw, this.getY() + BB_HEIGHT, this.getZ() + hw);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 逻辑宽度撑大后，父类(HostilePonyEntity)的原版 MeleeAttackGoal 会把 Prince 自己的
        // 近战攻击半径算成 sqrt(4×9² + 目标宽) ≈ 18 格，隔空打人。换成按物理半宽计算的
        // 自定义近战：贴脸距离(双方半宽之和)再留 1 格出手余量。
        this.goalSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof MeleeAttackGoal);
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity target) {
                double reach = BB_WIDTH * 0.5 + target.getBbWidth() * 0.5 + 1.0;
                return reach * reach;
            }
        });
    }

    @Override protected void refreshConfigAttributes() {
        applyCoreStats(entityId(), canFly());
    }

    @Override protected boolean canFly() { return false; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() { return ModSounds.PRINCE_RUTHERFORD_AMBIENT.get(); }
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return ModSounds.PRINCE_RUTHERFORD_HURT.get(); }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:prince_rutherford", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:prince_rutherford", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:prince_rutherford", "attack_damage"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {}
}
