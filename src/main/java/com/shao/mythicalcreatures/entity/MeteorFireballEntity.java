package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MeteorFireballEntity extends ThrowableItemProjectile {

    private static final float DAMAGE = 6.0F;
    private boolean hasHit = false;

    public MeteorFireballEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public MeteorFireballEntity(Level level, LivingEntity shooter) {
        super(ModEntities.METEOR_FIREBALL.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.METEOR_FIREBALL.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide && !hasHit) {
            hasHit = true;
            result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), DAMAGE);
            result.getEntity().setRemainingFireTicks(60);
            explode(result.getLocation());
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide && !hasHit) {
            hasHit = true;
            explode(result.getLocation());
        }
    }

    private void explode(Vec3 pos) {
        ServerLevel serverLevel = (ServerLevel) this.level();
        // 大爆炸
        serverLevel.explode(this, pos.x, pos.y, pos.z, 3.0F, Level.ExplosionInteraction.MOB);
        // 黑色粒子
        for (int i = 0; i < 60; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 5.0;
            double dy = this.random.nextDouble() * 4.0;
            double dz = (this.random.nextDouble() - 0.5) * 5.0;
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    pos.x + dx, pos.y + dy, pos.z + dz,
                    1, dx * 0.15, dy * 0.15, dz * 0.15, 0.05);
        }
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.FLAME,
                    this.getRandomX(0.2), this.getRandomY(), this.getRandomZ(0.2), 0, 0, 0);
            this.level().addParticle(ParticleTypes.SMOKE,
                    this.getRandomX(0.3), this.getRandomY(), this.getRandomZ(0.3), 0, 0.01, 0);
        }
    }
}
