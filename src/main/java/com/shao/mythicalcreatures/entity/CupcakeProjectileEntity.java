package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import com.shao.mythicalcreatures.entity.ModThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class CupcakeProjectileEntity extends ModThrowableProjectile {

    public CupcakeProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public CupcakeProjectileEntity(Level level, LivingEntity shooter) {
        super(ModEntities.CUPCAKE_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.CUPCAKE.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (result.getType() == HitResult.Type.ENTITY) {
                net.minecraft.world.phys.EntityHitResult ehr = (net.minecraft.world.phys.EntityHitResult) result;
                if (ehr.getEntity() instanceof LivingEntity target && target != this.getOwner()) {
                    target.hurt(this.damageSources().thrown(this, this.getOwner()), 8.0F);
                }
            }
            ((net.minecraft.server.level.ServerLevel) this.level()).sendParticles(
                    ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY(), this.getZ(),
                    12, 0.4, 0.4, 0.4, 0.03);
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER,
                    this.getX(), this.getY(), this.getZ(),
                    0, 0.03, 0);
        }
    }
}
