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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class PhoenixFeatherEntity extends ModThrowableProjectile {

    /** 配置注册名（common.toml / 编辑器里用这个键覆盖） */
    private static final String PID = "mythical_creatures_reborn:phoenix_feather";

    /** 取自配置 {@code mythical_creatures_reborn:phoenix_feather|damage}（可在配置编辑器「投掷物」分类里改） */
    private static float damage() { return (float) MythicalConfig.DATA.projectileAttr(PID, "damage"); }

    /** 取自配置 {@code mythical_creatures_reborn:phoenix_feather|fire_seconds}（可在配置编辑器「投掷物」分类里改） */
    private static int fireSeconds() { return (int) MythicalConfig.DATA.projectileAttr(PID, "fire_seconds"); }

    
    

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
            result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), damage());
            result.getEntity().setRemainingFireTicks(fireSeconds() * 20);
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
