package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.config.MythicalConfig;
import com.shao.mythicalcreatures.item.ModItems;
import com.shao.mythicalcreatures.sound.ModSounds;
import com.shao.mythicalcreatures.entity.RainbowCloudEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RainbowDashEntity extends NeutralPonyEntity {

    public RainbowDashEntity(EntityType<RainbowDashEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void refreshConfigAttributes() {
        cacheRideTuning(entityId());
        applyCoreStats(entityId(), canFly());
    }

    @Override protected boolean canFly() { return true; }
    @Override protected Item getTamingItem() {
        return resolveTamingItem(MythicalConfig.D.RD_TAMING, ModItems.RAINBOW_DASH_CUTIEMARK.get());
    }
    @Override @Nullable protected SoundEvent getAmbientSoundEvent() { return ModSounds.RAINBOW_DASH_AMBIENT.get(); }
    @Override @Nullable protected SoundEvent getHurtSoundEvent() { return ModSounds.RAINBOW_DASH_HURT.get(); }

    /* ── 飞行参数（快于紫悦） ── */
    @Override protected double getFlightAscentSpeed()   { return 0.15D; }
    @Override protected double getFlightDescendSpeed()  { return -0.08D; }
    @Override protected double getFlightMaxHeight()     { return 4.0D; }
    @Override protected int    getFlightHoverDuration() { return 100; }
    @Override protected int    getFlightChance()        { return MythicalConfig.DATA.getInt("mythicalcreatures:rainbow_dash", "flight_chance", 200); }
    @Override protected int    getFlightCooldownMin()   { return MythicalConfig.DATA.getInt("mythicalcreatures:rainbow_dash", "fly_cooldown_min", 100); }
    @Override protected int    getFlightCooldownMax()   { return MythicalConfig.DATA.getInt("mythicalcreatures:rainbow_dash", "fly_cooldown_max", 300); }
    @Override protected int    getFlightDurationMin()   { return MythicalConfig.DATA.getInt("mythicalcreatures:rainbow_dash", "fly_duration_min", 150); }
    @Override protected int    getFlightDurationMax()   { return MythicalConfig.DATA.getInt("mythicalcreatures:rainbow_dash", "fly_duration_max", 250); }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:rainbow_dash", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:rainbow_dash", "move_speed"))
                .add(Attributes.FLYING_SPEED, (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:rainbow_dash", "fly_speed"))
                .add(Attributes.ATTACK_DAMAGE, (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:rainbow_dash", "attack_damage"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }

    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        defineFlyData();
    }

    @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof com.shao.mythicalcreatures.entity.RainbowCloudEntity) return false;
        return super.hurt(source, amount);
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

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        RainbowCloudEntity projectile = new RainbowCloudEntity(this.level(), this);
        projectile.shoot(target.getX() - this.getX(), target.getY(0.5) - this.getY(0.5),
                target.getZ() - this.getZ(), 1.5F, 1.0F);
        this.playSound(SoundEvents.LLAMA_SPIT, 1.0F, 1.0F);
        this.level().addFreshEntity(projectile);
    }
}
