package com.shao.mythicalcreatures.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 紫悦施法时的"魔法爆发"视觉实体：一个短暂存在（约 0.8 秒）的纯特效实体，
 * 自身不移动 / 不碰撞 / 无重力，由 MagicBurstRenderer 画出扩散的冲击波环 + 发光圆盘。
 * 服务端 spawn 后会同步到所有客户端，因此多人下也能看到。
 */
public class MagicBurstEntity extends Entity {

    public static final int LIFE = 16; // tick 数（约 0.8s @20tps）

    private int age = 0;

    public MagicBurstEntity(EntityType<? extends MagicBurstEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public MagicBurstEntity(Level level, double x, double y, double z) {
        this(ModEntities.MAGIC_BURST.get(), level);
        this.setPos(x, y, z);
    }

    public int getAge() { return this.age; }

    @Override
    protected void defineSynchedData() { }

    @Override
    public void tick() {
        this.age++;
        if (this.age > LIFE) {
            this.discard();
        }
    }

    @Override
    public boolean isNoGravity() { return true; }

    @Override
    public boolean canBeCollidedWith() { return false; }

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) { }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) { }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
