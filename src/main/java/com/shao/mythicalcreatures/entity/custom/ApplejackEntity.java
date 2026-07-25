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

public class ApplejackEntity extends PonyEntity implements PlayerRideableJumping, PlayerRideable {

    private float playerJumpPendingScale;

    public ApplejackEntity(EntityType<ApplejackEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void refreshConfigAttributes() {
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
        return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.15);
    }

    @Override
    protected @NotNull Vec3 getRiddenInput(Player player, @NotNull Vec3 v) {
        float fwd = player.zza;
        float str = player.xxa * 0.5F;
        if (fwd <= 0) fwd *= 0.25F;
        return new Vec3(str, v.y, fwd);
    }

    @Override
    protected void tickRidden(@NotNull Player player, @NotNull Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        Vec2 rot = new Vec2(player.getXRot() * 0.5F, player.getYRot());
        this.setRot(rot.y, rot.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

        if (this.isControlledByLocalInstance() && this.onGround()) {
            this.setJumping(false);
            if (this.playerJumpPendingScale > 0.0F && !this.jumping) {
                executeRidersJump(this.playerJumpPendingScale, travelVector);
            }
            this.playerJumpPendingScale = 0.0F;
        }
    }

    private void executeRidersJump(float scale, Vec3 travelVector) {
        double jumpY = (double)(0.63F * scale) + (double)this.getJumpBoostPower();
        Vec3 delta = this.getDeltaMovement();
        this.setDeltaMovement(delta.x, jumpY, delta.z);
        this.setJumping(true); // LivingEntity.setJumping(boolean)
        this.hasImpulse = true;
        if (travelVector.z > 0.0D) {
            float f = Mth.sin(this.getYRot() * Mth.DEG_TO_RAD);
            float f1 = Mth.cos(this.getYRot() * Mth.DEG_TO_RAD);
            this.setDeltaMovement(this.getDeltaMovement().add(-0.4F * f * scale, 0.0D, 0.4F * f1 * scale));
        }
    }

    /* ── PlayerRideableJumping：
       onPlayerJump 只跑客户端（发包前的本地回调，不做事）
       handleStartJump 跑服务端（客户端松空格→sendRidingJump→服务端收包调这个） ── */
    @Override public boolean canJump() { return this.isTame(); }

    @Override
    public void onPlayerJump(int jumpPower) {
        if (jumpPower > 10) setJumpScale(jumpPower);
    }

    @Override
    public void handleStartJump(int jumpPower) {
        // 服务端不重复设跳跃——客户端 onPlayerJump→tickRidden 执行的跳跃
        // 通过 ServerboundMoveVehiclePacket 同步到服务端
    }

    private void setJumpScale(int jumpPower) {
        if (jumpPower < 0) jumpPower = 0;
        if (jumpPower >= 90) {
            this.playerJumpPendingScale = 1.0F;
        } else {
            this.playerJumpPendingScale = 0.4F + 0.4F * (float)jumpPower / 90.0F;
        }
    }

    @Override public void handleStopJump() {}

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
