package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.item.RainbowDashSword;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class RainbowBeamEntity extends Projectile {

    private static final float BEAM_LENGTH = 14.0F;
    private static final float BEAM_SIZE = 0.6F;
    private static final float DAMAGE = 8.0F;
    private static final int DAMAGE_INTERVAL = 4;

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(RainbowBeamEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public int tickCount = 0;

    public RainbowBeamEntity(EntityType<? extends RainbowBeamEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public RainbowBeamEntity(Level level, LivingEntity shooter) {
        super(ModEntities.RAINBOW_BEAM.get(), level);
        this.noPhysics = true;
        this.entityData.set(OWNER_UUID, Optional.of(shooter.getUUID()));
        syncToShooter(shooter);
    }

    private void syncToShooter(LivingEntity shooter) {
        Vec3 look = shooter.getLookAngle();
        this.setPos(shooter.position().add(0, 1.2, 0).add(look.scale(2.0F)));
        this.setYRot(shooter.getYRot());
        this.setXRot(shooter.getXRot());
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_UUID, Optional.empty());
    }

    public static float getBeamLength() {
        return BEAM_LENGTH;
    }

    public float getSpinAngle() {
        return tickCount * 30.0F;
    }

    @Override
    public void tick() {
        tickCount++;

        if (this.level().isClientSide) {
            // 客户端直接跟随本地玩家视角，避免网络延迟导致的跟手迟钝
            getOwnerUUID().ifPresent(uuid -> {
                Player localPlayer = Minecraft.getInstance().player;
                if (localPlayer != null && localPlayer.getUUID().equals(uuid)) {
                    this.setYRot(localPlayer.getYRot());
                    this.setXRot(localPlayer.getXRot());
                    this.yRotO = this.getYRot();
                    this.xRotO = this.getXRot();
                }
            });
            // super.tick() 放在旋转更新之后，避免 Projectile 覆盖我们的旋转
            super.tick();
            return;
        }

        LivingEntity owner = getOwner();
        if (owner == null || !owner.isAlive()) {
            this.discard();
            return;
        }

        // 检查是否还在施法
        if (owner instanceof Player player) {
            if (!(player.getMainHandItem().getItem() instanceof RainbowDashSword) || !player.isUsingItem()) {
                this.discard();
                return;
            }
        } else {
            this.discard();
            return;
        }

        // 跟随玩家
        syncToShooter(owner);

        // 伤害
        if (tickCount % DAMAGE_INTERVAL == 0) {
            Vec3 origin = this.position();
            Vec3 dir = Vec3.directionFromRotation(this.getXRot(), this.getYRot());
            Vec3 end = origin.add(dir.scale(BEAM_LENGTH));

            AABB beamBox = new AABB(origin, end).inflate(BEAM_SIZE);
            for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, beamBox,
                    e -> e != owner && e.isAlive())) {
                if (target.getBoundingBox().clip(origin, end).isPresent()) {
                    target.hurt(this.damageSources().magic(), DAMAGE);
                    target.invulnerableTime = 0;
                }
            }
        }
        // Projectile.tick() 放最后，避免覆盖我们设置的旋转/位置
        super.tick();
    }

    public Optional<UUID> getOwnerUUID() {
        return this.entityData.get(OWNER_UUID);
    }

    @Nullable
    public LivingEntity getOwner() {
        return getOwnerUUID()
                .map(uuid -> this.level().getPlayerByUUID(uuid))
                .orElse(null);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        tickCount = tag.getInt("TickCount");
        if (tag.hasUUID("Owner")) {
            this.entityData.set(OWNER_UUID, Optional.of(tag.getUUID("Owner")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("TickCount", tickCount);
        getOwnerUUID().ifPresent(uuid -> tag.putUUID("Owner", uuid));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
