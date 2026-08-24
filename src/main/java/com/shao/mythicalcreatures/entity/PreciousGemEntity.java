package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.effect.ModEffects;
import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 珍贵宝石（珍奇专属）投掷物：可被玩家扔出。
 * 命中目标造成很低的直接伤害，并施加「流血 + 修补」各 3 秒；
 * 期间再次命中会刷新（重置）这两个 buff 的持续时间。
 * 特效走「珠光宝气」路线：附魔闪光 + 白色发光 + 暴击星星 + 紫水晶音效。
 */
public class PreciousGemEntity extends ModThrowableProjectile {

    /** 直接伤害刻意压低：主要输出靠命中后的流血 buff。 */
    private static final float IMPACT_DAMAGE = 4.0F;

    /** 流血 / 修补 buff 持续时间（tick）：3 秒。 */
    private static final int EFFECT_DURATION = 60; // 20 tick/s × 3s

    private boolean hasHit = false;

    public PreciousGemEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public PreciousGemEntity(Level level, LivingEntity shooter) {
        super(ModEntities.PRECIOUS_GEM_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.PRECIOUS_GEM.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (hasHit || this.level().isClientSide) return;
        hasHit = true;

        ServerLevel serverLevel = (ServerLevel) this.level();

        if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult ehr = (EntityHitResult) result;
            if (ehr.getEntity() instanceof LivingEntity target && target != this.getOwner()) {
                // 低直接伤害（投掷物伤害源）
                target.hurt(this.damageSources().thrown(this, this.getOwner()), IMPACT_DAMAGE);
                // 流血 + 修补各 3 秒。原版 addEffect 对同等级效果会刷新到更长持续时间，
                // 因此 3 秒内再次命中即等于「重置计数」（重新回到满 3 秒）。
                target.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), EFFECT_DURATION, 0));
                target.addEffect(new MobEffectInstance(ModEffects.REPAIR.get(), EFFECT_DURATION, 0));
            }
        }

        // 珠光宝气命中特效：附魔闪光 + 暴击星星 + 白色发光粒子
        double hx = result.getLocation().x, hy = result.getLocation().y, hz = result.getLocation().z;
        if (result.getType() == HitResult.Type.ENTITY
                && ((EntityHitResult) result).getEntity() instanceof LivingEntity body) {
            hx = body.getX();
            hy = body.getY(0.5);   // 身体中段高度
            hz = body.getZ();
        }
        serverLevel.sendParticles(ParticleTypes.ENCHANT, hx, hy, hz, 20, 0.5, 0.5, 0.5, 0.8);
        serverLevel.sendParticles(ParticleTypes.CRIT, hx, hy, hz, 12, 0.4, 0.4, 0.4, 0.6);
        serverLevel.sendParticles(ParticleTypes.END_ROD, hx, hy, hz, 8, 0.3, 0.3, 0.3, 0.05);

        serverLevel.playSound(null, hx, hy, hz,
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS,
                0.8F, 1.0F + this.random.nextFloat() * 0.4F);

        this.discard();
    }

    /** 客户端拖尾：附魔闪光 + 白色发光，向后飘，营造「宝石划过留下珠光」的轨迹。 */
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && !this.isInWater() && this.tickCount % 2 == 0) {
            Vec3 vel = this.getDeltaMovement();
            this.level().addParticle(ParticleTypes.ENCHANT,
                    this.getX() - vel.x * 0.25, this.getY() - vel.y * 0.25, this.getZ() - vel.z * 0.25,
                    -vel.x * 0.2, -vel.y * 0.2, -vel.z * 0.2);
            if (this.tickCount % 4 == 0) {
                this.level().addParticle(ParticleTypes.END_ROD,
                        this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
        }
    }
}
