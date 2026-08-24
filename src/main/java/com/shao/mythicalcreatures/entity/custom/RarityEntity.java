package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.config.MythicalConfig;
import com.shao.mythicalcreatures.entity.PreciousGemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

public class RarityEntity extends NeutralPonyEntity implements PlayerRideableJumping, PlayerRideable {
    public RarityEntity(EntityType<RarityEntity> type, Level level) {
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
        return resolveTamingItem(MythicalConfig.D.RY_TAMING, com.shao.mythicalcreatures.item.ModItems.RARITY_CUTIEMARK.get());
    }
    @Nullable @Override protected net.minecraft.sounds.SoundEvent getAmbientSound() { return null; }
    @Nullable @Override protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return null; }
    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythicalcreatures:rarity", "max_health")).add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythicalcreatures:rarity", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythicalcreatures:rarity", "attack_damage")).add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }
    @Override protected void defineSynchedData() { super.defineSynchedData();  }
    @Override public void performRangedAttack(LivingEntity target, float power) {
        PreciousGemEntity projectile = new PreciousGemEntity(this.level(), this);
        projectile.shoot(target.getX() - this.getX(), target.getY(0.5) - this.getY(0.5),
                target.getZ() - this.getZ(), 1.0F, 1.0F);
        this.playSound(SoundEvents.EGG_THROW, 0.6F, 1.0F);
        this.level().addFreshEntity(projectile);
    }
}
