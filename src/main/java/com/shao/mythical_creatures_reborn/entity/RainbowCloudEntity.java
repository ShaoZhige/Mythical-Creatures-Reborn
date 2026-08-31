package com.shao.mythical_creatures_reborn.entity;

import com.shao.mythical_creatures_reborn.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import com.shao.mythical_creatures_reborn.entity.ModThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RainbowCloudEntity extends ModThrowableProjectile {

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

            // 直接命中的实体伤害
            if (result.getType() == HitResult.Type.ENTITY) {
                net.minecraft.world.phys.EntityHitResult ehr = (net.minecraft.world.phys.EntityHitResult) result;
                if (ehr.getEntity() instanceof LivingEntity target && target != this.getOwner()) {
                    target.hurt(this.damageSources().thrown(this, this.getOwner()), 7.0F);
                }
            }

            serverLevel.explode(this, pos.x, pos.y, pos.z, 3.0F, Level.ExplosionInteraction.NONE);
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

            // 一次性投掷物：命中后立即移除，避免实体残留堆积
            this.discard();
        }
    }
}
