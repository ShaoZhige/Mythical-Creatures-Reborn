package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TwilightStarEntity extends ThrowableItemProjectile {

    private boolean hasHit = false;

    public TwilightStarEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public TwilightStarEntity(Level level, LivingEntity shooter) {
        super(ModEntities.TWILIGHT_STAR.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.TWILIGHT_STAR_ITEM.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (hasHit || this.level().isClientSide) return;
        hasHit = true;

        Vec3 pos = result.getLocation();
        ServerLevel serverLevel = (ServerLevel) this.level();

        // 1 道闪电（LightningBolt 内部有 1-3 次闪烁，所以视觉效果足够）
        LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, serverLevel);
        bolt.setPos(pos.x, pos.y + 0.5, pos.z);
        serverLevel.addFreshEntity(bolt);

        serverLevel.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.6F, 1.0F);

        // 小爆炸
        serverLevel.explode(this, pos.x, pos.y, pos.z, 0.5F, Level.ExplosionInteraction.NONE);

        // 紫色粒子
        for (int i = 0; i < 40; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 3.0;
            double dy = this.random.nextDouble() * 2.5;
            double dz = (this.random.nextDouble() - 0.5) * 3.0;
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    pos.x + dx, pos.y + dy, pos.z + dz,
                    1, dx * 0.05, dy * 0.05, dz * 0.05, 0.05);
        }
        for (int i = 0; i < 25; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 3.0;
            double dy = this.random.nextDouble() * 2.0;
            double dz = (this.random.nextDouble() - 0.5) * 3.0;
            serverLevel.sendParticles(ParticleTypes.WITCH,
                    pos.x + dx, pos.y + dy, pos.z + dz,
                    1, dx * 0.03, dy * 0.03, dz * 0.03, 0.03);
        }
    }

    /** 客户端粒子 — 持续追踪投掷物轨迹的紫色尾迹 */
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && !this.isInWater()) {
            for (int i = 0; i < 3; i++) {
                this.level().addParticle(ParticleTypes.PORTAL,
                        this.getRandomX(0.3), this.getRandomY(), this.getRandomZ(0.3),
                        0, 0, 0);
            }
        }
    }
}
