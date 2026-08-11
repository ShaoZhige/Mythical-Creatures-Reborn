package com.shao.mythicalcreatures.entity;

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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class UnstableItemEntity extends ModThrowableProjectile {

    // 魔法伤害：比紫悦弹射物(13)高一点，也高于小马弹射物平均(约8)
    private static final float MAGIC_DAMAGE = 15.0F;
    private boolean hasHit = false;

    public UnstableItemEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public UnstableItemEntity(Level level, LivingEntity shooter) {
        super(ModEntities.UNSTABLE_ITEM.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.UNSTABLE_ITEM.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (hasHit || this.level().isClientSide) return;
        hasHit = true;

        Vec3 pos = result.getLocation();
        ServerLevel serverLevel = (ServerLevel) this.level();

        // 魔法伤害（不是弹射物伤害）：命中直接给目标 15 点魔法伤害
        if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult ehr = (EntityHitResult) result;
            if (ehr.getEntity() instanceof LivingEntity target && target != this.getOwner()) {
                target.hurt(this.damageSources().indirectMagic(this, this.getOwner()), MAGIC_DAMAGE);
            }
        }

        // 极简命中特效：霰弹弹幕颗粒多，单颗刻意给很少（雪花4 + 雪球2 + 霜雾2）
        double hx = pos.x, hy = pos.y, hz = pos.z;
        if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult ehr = (EntityHitResult) result;
            if (ehr.getEntity() instanceof LivingEntity body) {
                hx = body.getX();
                hy = body.getY(0.5);   // 身体中段高度
                hz = body.getZ();
            }
        }
        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, hx, hy, hz, 4, 0.4, 0.6, 0.4, 0.5);
        serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL, hx, hy, hz, 2, 0.3, 0.5, 0.3, 0.5);
        serverLevel.sendParticles(ParticleTypes.CLOUD, hx, hy, hz, 2, 0.3, 0.4, 0.3, 0.05);

        serverLevel.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.6F, 1.2F);

        // 一次性投掷物：命中后立即移除
        this.discard();
    }

    /** 客户端粒子拖尾 — 极简霜冻尾焰：每 2 tick 在弹尾生成 1 个雪花并向后飘（显示弹道）。
     *  霰弹弹幕颗粒多，单颗特效刻意少，避免粒子堆积卡顿。 */
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && !this.isInWater() && this.tickCount % 2 == 0) {
            Vec3 vel = this.getDeltaMovement();
            this.level().addParticle(ParticleTypes.SNOWFLAKE,
                    this.getX() - vel.x * 0.25, this.getY() - vel.y * 0.25, this.getZ() - vel.z * 0.25,
                    -vel.x * 0.2, -vel.y * 0.2, -vel.z * 0.2);
        }
    }
}
