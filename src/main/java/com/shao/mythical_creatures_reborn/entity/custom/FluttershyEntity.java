package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import com.shao.mythical_creatures_reborn.entity.ButterflyProjectileEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluttershyEntity extends NeutralPonyEntity {
    public FluttershyEntity(EntityType<FluttershyEntity> type, Level level) {
        super(type, level);
    }

    @Override protected boolean canFly() { return true; }
    @Override protected boolean isRideable() { return true; } // 可骑乘：refreshConfigAttributes 会缓存骑乘调参
    @Override protected Item getTamingItem() {
        return resolveTamingItem(MythicalConfig.D.FS_TAMING, com.shao.mythical_creatures_reborn.item.ModItems.FLUTTERSHY_CUTIEMARK.get());
    }
    @Nullable @Override protected net.minecraft.sounds.SoundEvent getAmbientSound() { return null; }
    @Nullable @Override protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return null; }
    public static AttributeSupplier.Builder createAttributes() {
        return PonyAttributes.flying("mythical_creatures_reborn:fluttershy");
    }
    @Override protected void defineSynchedData() { super.defineSynchedData(); defineFlyData(); }
    @Override public void performRangedAttack(LivingEntity target, float power) {
        ButterflyProjectileEntity projectile = new ButterflyProjectileEntity(this.level(), this);
        projectile.shoot(target.getX() - this.getX(), target.getY(0.5) - this.getY(0.5),
                target.getZ() - this.getZ(), 0.8F, 1.5F);
        this.playSound(SoundEvents.EGG_THROW, 0.5F, 1.2F);
        this.level().addFreshEntity(projectile);
    }

    /* ── 骑乘飞行：逻辑统一在 FlightRideAPI，实体只做委托调用（默认值见 MythicalConfig.D.ENTITY_DEFAULTS） ── */
    // 飞行小马骑手定位以紫悦为标准（见 PonyEntity.FLYING_RIDER_*）
    @Override protected double getRiderBackOffset()    { return FLYING_RIDER_BACK; }
    @Override protected float  getRiderVerticalOffset() { return FLYING_RIDER_Y; }

    @Override protected @NotNull Vec3 getRiddenInput(Player player, @NotNull Vec3 v) {
        return FlightRideAPI.getRiddenInput(this, player, v);
    }

    @Override protected float getRiddenSpeed(@NotNull Player player) {
        return FlightRideAPI.getRiddenSpeed(this);
    }

    @Override protected void tickRidden(@NotNull Player player, @NotNull Vec3 v) {
        super.tickRidden(player, v);
        FlightRideAPI.tickRidden(this, player, v);
    }

    @Override public void travel(@NotNull Vec3 v) {
        if (!FlightRideAPI.flyingRideTravel(this, v)) {
            this.setNoGravity(false);
            super.travel(v);
        }
    }

    @Override public void tick() {
        super.tick();
        if (!FlightRideAPI.tickRiddenFlight(this)) tickFlight();
    }
}
