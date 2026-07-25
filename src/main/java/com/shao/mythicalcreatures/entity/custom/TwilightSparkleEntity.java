package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.config.MythicalConfig;
import com.shao.mythicalcreatures.entity.TwilightStarEntity;
import com.shao.mythicalcreatures.item.ModItems;
import com.shao.mythicalcreatures.sound.ModSounds;
import com.shao.mythicalcreatures.util.KeyStateHelper;
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

public class TwilightSparkleEntity extends PonyEntity {

    public TwilightSparkleEntity(EntityType<TwilightSparkleEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void refreshConfigAttributes() {
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

    /* ── 骑乘飞行 ── */
    @Override protected @NotNull Vec3 getRiddenInput(Player player, @NotNull Vec3 v) {
        float fwd = player.zza;
        float str = (float)(player.xxa * (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "ridden_speed_factor"));
        if (fwd <= 0) fwd *= 0.25F;
        return new Vec3(str, v.y, fwd);
    }

    @Override protected float getRiddenSpeed(@NotNull Player player) {
        return (float) this.getAttributeValue(Attributes.FLYING_SPEED);
    }

    @Override protected void tickRidden(@NotNull Player player, @NotNull Vec3 v) {
        super.tickRidden(player, v);
        this.setYRot(player.getYRot()); this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        if (this.isFlying() || this.isHovering()) {
            float vert = KeyStateHelper.isJumpKeyDown(player)
                    ? (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "vertical_up")
                    : KeyStateHelper.isMountDescendDown(player)
                    ? (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "vertical_down")
                    : (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "vertical_hover");
            this.setDeltaMovement(this.getDeltaMovement().add(0, vert, 0));
        }
    }

    @Override public void travel(@NotNull Vec3 v) {
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player) {
            this.setNoGravity(true); this.fallDistance = 0;
            if (this.isFlying() || this.isHovering()) {
                float s = (float)(getRiddenSpeed((Player)this.getControllingPassenger())
                        * (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "horizontal_factor"));
                this.moveRelative(s, new Vec3(v.x, 0, v.z));
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "inertia_decay")));
                if (this.getY() > this.level().getMaxBuildHeight() + 4)
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.5, 0));
            } else { super.travel(v); }
        } else { this.setNoGravity(false); super.travel(v); }
    }

    @Override public void tick() {
        super.tick();
        if (this.isVehicle()) {
            this.setFlying(true);
            this.wingFlapTicks = (float)((this.wingFlapTicks + MythicalConfig.DATA.get("global_params", "wing_flap_speed", 0.4)) % 360.0);
            return;
        }
        tickFlight();
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        TwilightStarEntity projectile = new TwilightStarEntity(this.level(), this);
        projectile.shoot(target.getX() - this.getX(), target.getY(0.5) - this.getY(0.5),
                target.getZ() - this.getZ(), 1.5F, 1.0F);
        this.playSound(SoundEvents.LLAMA_SPIT, 1.0F, 1.0F);
        this.level().addFreshEntity(projectile);
    }
}
