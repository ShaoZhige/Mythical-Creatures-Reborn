package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RainbowCloudEntity extends ThrowableItemProjectile {

    public RainbowCloudEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public RainbowCloudEntity(Level level, LivingEntity shooter) {
        super(ModEntities.RAINBOW_CLOUD.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.RAINBOW_CLOUD_ITEM.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            Vec3 pos = result.getLocation();
            ServerLevel serverLevel = (ServerLevel) this.level();
            serverLevel.explode(this, pos.x, pos.y, pos.z, 0.3F, Level.ExplosionInteraction.NONE);
            // explode() 自带爆炸音效，不再重复播放

            // 白色云朵粒子
            for (int i = 0; i < 12; i++) {
                double dx = (this.random.nextDouble() - 0.5) * 2.0;
                double dy = this.random.nextDouble() * 1.5;
                double dz = (this.random.nextDouble() - 0.5) * 2.0;
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        pos.x + dx, pos.y + dy, pos.z + dz,
                        1, 0, 0, 0, 0.03);
            }
            // 白色漂浮粒子
            for (int i = 0; i < 8; i++) {
                double dx = (this.random.nextDouble() - 0.5) * 2.0;
                double dy = this.random.nextDouble() * 2.0;
                double dz = (this.random.nextDouble() - 0.5) * 2.0;
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        pos.x + dx, pos.y + dy, pos.z + dz,
                        1, 0, 0, 0, 0.03);
            }
        }
    }
}
