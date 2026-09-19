package com.shao.mythical_creatures_reborn.entity;
import com.shao.mythical_creatures_reborn.config.MythicalConfig;

import com.shao.mythical_creatures_reborn.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import com.shao.mythical_creatures_reborn.entity.ModThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ButterflyProjectileEntity extends ModThrowableProjectile {

    /** 配置注册名（common.toml / 编辑器里用这个键覆盖） */
    private static final String PID = "mythical_creatures_reborn:butterfly_projectile";

    /** 取自配置 {@code mythical_creatures_reborn:butterfly_projectile|damage}（可在配置编辑器「投掷物」分类里改） */
    private static float damage() { return (float) MythicalConfig.DATA.projectileAttr(PID, "damage"); }

    public ButterflyProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public ButterflyProjectileEntity(Level level, LivingEntity shooter) {
        super(ModEntities.BUTTERFLY_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BUTTERFLIES.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (result.getType() == HitResult.Type.ENTITY) {
                net.minecraft.world.phys.EntityHitResult ehr = (net.minecraft.world.phys.EntityHitResult) result;
                if (ehr.getEntity() instanceof LivingEntity target && target != this.getOwner()) {
                    target.hurt(this.damageSources().thrown(this, this.getOwner()), damage());
                }
            }
            ((net.minecraft.server.level.ServerLevel) this.level()).sendParticles(
                    ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY(), this.getZ(),
                    10, 0.5, 0.5, 0.5, 0.05);
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(ParticleTypes.HAPPY_VILLAGER,
                        this.getRandomX(0.3), this.getRandomY(), this.getRandomZ(0.3),
                        0, 0.05, 0);
            }
        }
    }
}
