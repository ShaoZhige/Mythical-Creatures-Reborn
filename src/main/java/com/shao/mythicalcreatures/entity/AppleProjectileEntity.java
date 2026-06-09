package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AppleProjectileEntity extends ThrowableItemProjectile {

    public AppleProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public AppleProjectileEntity(Level level, LivingEntity shooter) {
        super(ModEntities.APPLE_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.APPLES_ITEM.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            Vec3 pos = result.getLocation();
            this.level().explode(this, pos.x, pos.y, pos.z, 0.3F, Level.ExplosionInteraction.NONE);

            // 爱心粒子
            for (int i = 0; i < 25; i++) {
                double dx = (this.random.nextDouble() - 0.5) * 2.5;
                double dy = this.random.nextDouble() * 2.5;
                double dz = (this.random.nextDouble() - 0.5) * 2.5;
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART,
                        pos.x + dx, pos.y + dy, pos.z + dz,
                        1, dx * 0.02, dy * 0.1, dz * 0.02, 0.05);
            }
            // 绿色生长粒子
            for (int i = 0; i < 30; i++) {
                double dx = (this.random.nextDouble() - 0.5) * 3.0;
                double dy = this.random.nextDouble() * 2.0;
                double dz = (this.random.nextDouble() - 0.5) * 3.0;
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        pos.x + dx, pos.y + dy, pos.z + dz,
                        1, dx * 0.02, dy * 0.05, dz * 0.02, 0.05);
            }
            // 额外绿色堆肥粒子
            for (int i = 0; i < 15; i++) {
                double dx = (this.random.nextDouble() - 0.5) * 2.0;
                double dy = this.random.nextDouble() * 1.5;
                double dz = (this.random.nextDouble() - 0.5) * 2.0;
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.COMPOSTER,
                        pos.x + dx, pos.y + dy, pos.z + dz,
                        1, dx * 0.01, 0.1, dz * 0.01, 0.05);
            }
            this.level().playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.4F, 2.0F);
        }
    }

    /** 爱心尾迹 */
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.tickCount % 3 == 0) {
            this.level().addParticle(ParticleTypes.HEART,
                    this.getRandomX(0.2), this.getRandomY() + 0.2, this.getRandomZ(0.2),
                    0, 0, 0);
        }
    }
}
