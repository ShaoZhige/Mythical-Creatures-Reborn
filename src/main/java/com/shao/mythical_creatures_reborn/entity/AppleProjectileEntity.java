package com.shao.mythical_creatures_reborn.entity;

import com.shao.mythical_creatures_reborn.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import com.shao.mythical_creatures_reborn.entity.ModThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class AppleProjectileEntity extends ModThrowableProjectile {

    // 友方小马集合：仅对这 7 个实体免伤并回血（六小马 + 圣光辉辉）。
    // 不能用 instanceof PonyEntity —— 本模组几乎所有生物都继承 PonyEntity（含敌对生物），
    // 那样会把全部生物都误判为友方，导致投掷物对什么都没有伤害。
    private static final Set<String> FRIENDLY_PONIES = Set.of(
            "mythical_creatures_reborn:twilight_sparkle",
            "mythical_creatures_reborn:rainbow_dash",
            "mythical_creatures_reborn:applejack",
            "mythical_creatures_reborn:fluttershy",
            "mythical_creatures_reborn:pinkie_pie",
            "mythical_creatures_reborn:rarity",
            "mythical_creatures_reborn:holy_light_radiance"
    );

    private static boolean isFriendlyPony(Entity e) {
        return FRIENDLY_PONIES.contains(e.getType().builtInRegistryHolder().key().location().toString());
    }

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
        boolean friendly = false;
        if (result.getType() == HitResult.Type.ENTITY) {
            net.minecraft.world.phys.EntityHitResult entityHit = (net.minecraft.world.phys.EntityHitResult) result;
            if (entityHit.getEntity() instanceof LivingEntity target) {
                if (isFriendlyPony(target)) {
                    // 对 M6 + 圣光辉辉：免伤 + 回血 8 点
                    friendly = true;
                    if (!this.level().isClientSide) {
                        target.heal(8.0F);
                    }
                    if (this.level() instanceof ServerLevel sl) {
                        for (int i = 0; i < 10; i++) {
                            sl.sendParticles(ParticleTypes.HEART, target.getRandomX(0.5), target.getY() + target.getBbHeight() * 0.5, target.getRandomZ(0.5),
                                    1, 0, 0.1, 0, 0.1);
                        }
                    }
                    this.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.6F, 1.2F);
                } else if (target != this.getOwner()) {
                    // 对其他生物（含本模组的敌对生物）：正常造成 9 点伤害
                    target.hurt(this.damageSources().thrown(this, this.getOwner()), 9.0F);
                }
            }
        }
        if (!friendly) {
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
