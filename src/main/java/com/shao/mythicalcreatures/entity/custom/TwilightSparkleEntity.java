package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.config.MythicalConfig;
import com.shao.mythicalcreatures.entity.ModEntities;
import com.shao.mythicalcreatures.entity.TwilightStarEntity;
import com.shao.mythicalcreatures.entity.custom.TwilightMagicEntity;
import com.shao.mythicalcreatures.item.ModItems;
import com.shao.mythicalcreatures.sound.ModSounds;
import com.shao.mythicalcreatures.entity.TwilightStarEntity;
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

public class TwilightSparkleEntity extends NeutralPonyEntity {

    public TwilightSparkleEntity(EntityType<TwilightSparkleEntity> type, Level level) {
        super(type, level);
    }

    private int magicSummonCooldown = 0;

    @Override protected void refreshConfigAttributes() {
        cacheRideTuning("mythicalcreatures:twilight_sparkle");
        var h = this.getAttribute(Attributes.MAX_HEALTH);
        if (h != null) h.setBaseValue((float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "max_health"));
        var s = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (s != null) s.setBaseValue((float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "move_speed"));
        var f = this.getAttribute(Attributes.FLYING_SPEED);
        if (f != null) f.setBaseValue((float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "fly_speed"));
        var d = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (d != null) d.setBaseValue((float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "attack_damage"));
        this.setHealth(this.getMaxHealth());
    }

    @Override protected boolean canFly() { return true; }
    @Override protected Item getTamingItem() {
        return resolveTamingItem(MythicalConfig.D.TS_TAMING, ModItems.TWILIGHT_CUTIEMARK.get());
    }
    @Override @Nullable protected SoundEvent getAmbientSoundEvent() { return ModSounds.TWILIGHT_SPARKLE_AMBIENT.get(); }
    @Override @Nullable protected SoundEvent getHurtSoundEvent() { return ModSounds.TWILIGHT_SPARKLE_HURT.get(); }

    /* ── 飞行参数（慢速平稳，默认值即紫悦的风格） ── */
    @Override protected int    getFlightChance()        { return MythicalConfig.DATA.getInt("mythicalcreatures:twilight_sparkle", "flight_chance", 200); }
    @Override protected int    getFlightCooldownMin()   { return MythicalConfig.DATA.getInt("mythicalcreatures:twilight_sparkle", "fly_cooldown_min", 200); }
    @Override protected int    getFlightCooldownMax()   { return MythicalConfig.DATA.getInt("mythicalcreatures:twilight_sparkle", "fly_cooldown_max", 400); }
    @Override protected int    getFlightDurationMin()   { return MythicalConfig.DATA.getInt("mythicalcreatures:twilight_sparkle", "fly_duration_min", 100); }
    @Override protected int    getFlightDurationMax()   { return MythicalConfig.DATA.getInt("mythicalcreatures:twilight_sparkle", "fly_duration_max", 150); }
    @Override protected int    getAngryFlightChance()   { return 30; }
    @Override protected int    getAngryFlightAscentDuration() { return 40; }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH,  (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "move_speed"))
                .add(Attributes.FLYING_SPEED,   (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "fly_speed"))
                .add(Attributes.ATTACK_DAMAGE,  (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "attack_damage"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }

    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        defineFlyData();
    }

    @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof TwilightStarEntity) return false;
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
        if (this.magicSummonCooldown > 0) this.magicSummonCooldown--;
        if (!FlightRideAPI.tickRiddenFlight(this)) tickFlight();
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        TwilightStarEntity projectile = new TwilightStarEntity(this.level(), this);
        projectile.shoot(target.getX() - this.getX(), target.getY(0.5) - this.getY(0.5),
                target.getZ() - this.getZ(), 1.5F, 1.0F);
        this.playSound(SoundEvents.LLAMA_SPIT, 1.0F, 1.0F);
        this.level().addFreshEntity(projectile);

        // 概率召唤紫悦的魔法团（以紫悦为主人，会自动环绕并攻击敌对生物）
        if (!this.level().isClientSide() && this.magicSummonCooldown <= 0 && this.random.nextFloat() < 0.35F) {
            TwilightMagicEntity magic = new TwilightMagicEntity(ModEntities.TWILIGHT_MAGIC.get(), this.level());
            magic.setPos(this.getX(), this.getY(1.0D), this.getZ());
            magic.setOwner(this);
            this.level().addFreshEntity(magic);
            this.magicSummonCooldown = 120; // 约 6 秒冷却
        }
    }
}
