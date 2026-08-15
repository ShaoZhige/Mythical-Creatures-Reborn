package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.entity.custom.PonyEntity;
import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import com.shao.mythicalcreatures.entity.ModThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AppleProjectileEntity extends ModThrowableProjectile {

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
        boolean healedPony = false;
        if (result.getType() == HitResult.Type.ENTITY) {
            net.minecraft.world.phys.EntityHitResult entityHit = (net.minecraft.world.phys.EntityHitResult) result;
            if (entityHit.getEntity() instanceof PonyEntity pony) {
                if (!this.level().isClientSide) {
                    pony.heal(8.0F);
                }
                if (this.level() instanceof ServerLevel sl) {
                    for (int i = 0; i < 10; i++) {
                        sl.sendParticles(ParticleTypes.HEART, pony.getRandomX(0.5), pony.getY() + pony.getBbHeight() * 0.5, pony.getRandomZ(0.5),
                                1, 0, 0.1, 0, 0.1);
                    }
                }
                this.level().playSound(null, pony.getX(), pony.getY(), pony.getZ(),
                        SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.6F, 1.2F);
                healedPony = true;
            } else if (entityHit.getEntity() instanceof LivingEntity target && target != this.getOwner()) {
                // 命中敌人：造成 9 点伤害（苹果砸怪）
                target.hurt(this.damageSources().thrown(this, this.getOwner()), 9.0F);
            }
        }
        if (!healedPony) {
            super.onHit(result);
        }
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

            // 一次性投掷物：命中后立即移除，避免实体残留堆积
            this.discard();
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
