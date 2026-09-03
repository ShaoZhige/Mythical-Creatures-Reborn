package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import com.shao.mythical_creatures_reborn.entity.AppleProjectileEntity;
import com.shao.mythical_creatures_reborn.entity.BalloonProjectileEntity;
import com.shao.mythical_creatures_reborn.entity.ButterflyProjectileEntity;
import com.shao.mythical_creatures_reborn.entity.CupcakeProjectileEntity;
import com.shao.mythical_creatures_reborn.entity.PreciousGemEntity;
import com.shao.mythical_creatures_reborn.entity.RainbowCloudEntity;
import com.shao.mythical_creatures_reborn.entity.TwilightStarEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HolyLightRadianceEntity extends NeutralPonyEntity implements PlayerRideableJumping, PlayerRideable {
    public HolyLightRadianceEntity(EntityType<HolyLightRadianceEntity> type, Level level) {
        super(type, level);
    }

    /* ════════════════════════════════════════════════════════════════
     * 地面骑乘 + 跳跃（与苹果嘉儿同一套标准，AbstractHorse 同款逻辑）
     * ════════════════════════════════════════════════════════════════ */

    @Override protected float getRiddenSpeed(@NotNull Player player) {
        return GroundRideAPI.getRiddenSpeed(this);
    }

    @Override
    protected @NotNull Vec3 getRiddenInput(Player player, @NotNull Vec3 v) {
        return GroundRideAPI.getRiddenInput(this, player, v);
    }

    @Override
    protected void tickRidden(@NotNull Player player, @NotNull Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        GroundRideAPI.tickRidden(this, player, travelVector);
    }

    @Override public boolean canJump() { return GroundRideAPI.canJump(this); }

    @Override
    public void onPlayerJump(int jumpPower) {
        GroundRideAPI.onPlayerJump(this, jumpPower);
    }

    @Override
    public void handleStartJump(int jumpPower) {
        GroundRideAPI.handleStartJump(this, jumpPower);
    }

    @Override public void handleStopJump() { GroundRideAPI.handleStopJump(this); }

    @Override protected void refreshConfigAttributes() {
        cacheRideTuning(entityId());
        applyCoreStats(entityId(), canFly());
    }
    @Override protected boolean canFly() { return false; }
    @Override protected Item getTamingItem() {
        return resolveTamingItem(MythicalConfig.D.HL_TAMING, com.shao.mythical_creatures_reborn.item.ModItems.HOLY_LIGHT_RADIANCE_CUTIEMARK.get());
    }
    @Nullable @Override protected net.minecraft.sounds.SoundEvent getAmbientSound() { return null; }
    @Nullable @Override protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return null; }
    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:holy_light_radiance", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:holy_light_radiance", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:holy_light_radiance", "attack_damage"))
                .add(Attributes.ARMOR, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:holy_light_radiance", "armor"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }
    @Override public void performRangedAttack(LivingEntity target, float power) {
        ThrowableItemProjectile projectile;
        int roll = this.random.nextInt(7);
        switch (roll) {
            case 0 -> projectile = new BalloonProjectileEntity(this.level(), this);
            case 1 -> projectile = new ButterflyProjectileEntity(this.level(), this);
            case 2 -> projectile = new CupcakeProjectileEntity(this.level(), this);
            case 3 -> projectile = new AppleProjectileEntity(this.level(), this);
            case 4 -> projectile = new TwilightStarEntity(this.level(), this);
            case 5 -> projectile = new PreciousGemEntity(this.level(), this);
            default -> projectile = new RainbowCloudEntity(this.level(), this);
        }
        projectile.shoot(target.getX() - this.getX(), target.getY(0.5) - this.getY(0.5),
                target.getZ() - this.getZ(), 1.2F, 1.0F);
        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.5F);
        this.level().addFreshEntity(projectile);
    }
}
