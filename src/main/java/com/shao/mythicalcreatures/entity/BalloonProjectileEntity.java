package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class BalloonProjectileEntity extends ThrowableItemProjectile {

    public BalloonProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public BalloonProjectileEntity(Level level, LivingEntity shooter) {
        super(ModEntities.BALLOON_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BALLOONS.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            ((net.minecraft.server.level.ServerLevel) this.level()).sendParticles(
                    ParticleTypes.HEART, this.getX(), this.getY(), this.getZ(),
                    8, 0.3, 0.3, 0.3, 0.05);
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.HEART,
                    this.getX(), this.getY(), this.getZ(),
                    0, 0.02, 0);
        }
    }
}
