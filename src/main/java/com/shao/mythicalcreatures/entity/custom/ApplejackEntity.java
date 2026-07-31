package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.config.MythicalConfig;
import com.shao.mythicalcreatures.item.ModItems;
import com.shao.mythicalcreatures.sound.ModSounds;
import com.shao.mythicalcreatures.entity.AppleProjectileEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApplejackEntity extends NeutralPonyEntity implements PlayerRideableJumping, PlayerRideable {

    public ApplejackEntity(EntityType<ApplejackEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void refreshConfigAttributes() {
        cacheRideTuning("mythicalcreatures:applejack");
        var h = this.getAttribute(Attributes.MAX_HEALTH);
        if (h != null) h.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:applejack", "max_health"));
        var s = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (s != null) s.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:applejack", "move_speed"));
        var d = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (d != null) d.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:applejack", "attack_damage"));
        this.setHealth(this.getMaxHealth());
    }

    @Override protected boolean canFly() { return false; }
    @Override protected Item getTamingItem() {
        return resolveTamingItem(MythicalConfig.D.AJ_TAMING, ModItems.APPLEJACK_CUTIEMARK.get());
    }
    @Override @Nullable protected SoundEvent getAmbientSoundEvent() { return ModSounds.APPLEJACK_AMBIENT.get(); }
    @Override @Nullable protected SoundEvent getHurtSoundEvent() { return ModSounds.APPLEJACK_HURT.get(); }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH,      MythicalConfig.DATA.entityAttr("mythicalcreatures:applejack", "max_health"))
                .add(Attributes.MOVEMENT_SPEED,  MythicalConfig.DATA.entityAttr("mythicalcreatures:applejack", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE,   MythicalConfig.DATA.entityAttr("mythicalcreatures:applejack", "attack_damage"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }

    /* ════════════════════════════════════════════════════════════════
     * 骑乘 + 跳跃（AbstractHorse 同款逻辑，适配 TamableAnimal 的 API）
     * ════════════════════════════════════════════════════════════════ */

    @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof com.shao.mythicalcreatures.entity.AppleProjectileEntity) return false;
        return super.hurt(source, amount);
    }

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

    /* ── 攻击 ── */
    @Nullable @Override
    public LivingEntity getControllingPassenger() { return super.getControllingPassenger(); }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        AppleProjectileEntity projectile = new AppleProjectileEntity(this.level(), this);
        projectile.shoot(
            target.getX() - this.getX(),
            target.getY(0.5) - this.getY(0.5),
            target.getZ() - this.getZ(), 1.5F, 1.0F);
        this.playSound(SoundEvents.LLAMA_SPIT, 1.0F, 1.0F);
        this.level().addFreshEntity(projectile);
    }
}
