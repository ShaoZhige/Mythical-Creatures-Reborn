package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class PhoenixFeatherEntity extends ThrowableItemProjectile {

    private static final float DAMAGE = 6.0F;
    private static final int FIRE_SECONDS = 5;

    public PhoenixFeatherEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public PhoenixFeatherEntity(Level level, LivingEntity shooter) {
        super(ModEntities.PHOENIX_FEATHER.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.PHOENIX_FEATHER.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), DAMAGE);
            result.getEntity().setRemainingFireTicks(FIRE_SECONDS * 20);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    /** 火焰尾迹 */
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            for (int i = 0; i < 3; i++) {
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getRandomX(0.2), this.getRandomY(), this.getRandomZ(0.2),
                        0, 0.02, 0);
            }
        }
    }
}
