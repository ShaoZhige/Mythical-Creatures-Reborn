package com.shao.mythical_creatures_reborn.entity;

import com.shao.mythical_creatures_reborn.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import com.shao.mythical_creatures_reborn.entity.ModThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TwilightStarEntity extends ModThrowableProjectile {

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

        // 直接命中的实体伤害。
        // 直伤 13 = 基础 8 + 假雷不再结算的 5 点范围闪电伤害（LightningBolt.thunderHit），
        // 保证总输出与真雷时代持平。
        if (result.getType() == HitResult.Type.ENTITY) {
            net.minecraft.world.phys.EntityHitResult ehr = (net.minecraft.world.phys.EntityHitResult) result;
            if (ehr.getEntity() instanceof LivingEntity target && target != this.getOwner()) {
                target.hurt(this.damageSources().thrown(this, this.getOwner()), 13.0F);
            }
        }

        // 1 道闪电（LightningBolt 内部有 1-3 次闪烁，所以视觉效果足够）
        LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, serverLevel);
        // —— 为什么用"假雷"（setVisualOnly）——
        // 1) 闪电的 thunderHit 会对命中实体 setRemainingFireTicks(8)（点 8 秒火），
        //    野生紫悦会被自己的雷劈得着火，体验很差，必须去掉这道雷的火。
        // 2) 若用 Mixin 去全局点火会污染原版行为（玩家/村民/苦力怕/引雷三叉戟等的闪电都不再着火），
        //    只应在紫悦子弹使用这道雷时生效。
        // 3) 因此只对这道雷本身设 setVisualOnly(true)：跳过"召雷击中实体（点火+5点伤害+变身）"和
        //    "落点 3x3 生地面火"两个分支，但仍正常渲染闪电与雷声——既去掉火、又不触碰任何原版逻辑。
        bolt.setVisualOnly(true);
        bolt.setPos(pos.x, pos.y + 0.5, pos.z);
        serverLevel.addFreshEntity(bolt);

        serverLevel.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.6F, 1.0F);

        // 小爆炸
        serverLevel.explode(this, pos.x, pos.y, pos.z, 0.5F, Level.ExplosionInteraction.NONE);

        // ① 击中特效：粒子从被击中实体身体迸出（而非在原命中点的空中漂浮）。
        //    命中实体时以实体身体中心为原点；未命中实体（打在方块/地面）则退化为命中点。
        double hx = pos.x, hy = pos.y, hz = pos.z;
        if (result.getType() == HitResult.Type.ENTITY) {
            net.minecraft.world.phys.EntityHitResult ehr = (net.minecraft.world.phys.EntityHitResult) result;
            if (ehr.getEntity() instanceof LivingEntity body) {
                hx = body.getX();
                hy = body.getY(0.5);   // 身体中段高度
                hz = body.getZ();
            }
        }
        // 紫色魔法粒子以身体为中心向四周迸发（WITCH=女巫紫，ENCHANT/END_ROD 增加一点闪光）。
        // 走 sendParticles 才会在服务端广播到客户端；增大扩散与初速让"迸出"更明显。
        serverLevel.sendParticles(ParticleTypes.WITCH, hx, hy, hz, 60, 0.45, 0.7, 0.45, 0.6);
        serverLevel.sendParticles(ParticleTypes.ENCHANT, hx, hy, hz, 18, 0.3, 0.5, 0.3, 0.5);
        serverLevel.sendParticles(ParticleTypes.END_ROD, hx, hy, hz, 12, 0.25, 0.4, 0.25, 0.7);

        // 一次性投掷物：命中后立即移除，避免实体残留堆积
        this.discard();
    }

    /** 客户端粒子 — 紫色传送门 + 附魔符文拖尾 */
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && !this.isInWater()) {
            for (int i = 0; i < 3; i++) {
                this.level().addParticle(ParticleTypes.PORTAL,
                        this.getRandomX(0.3), this.getRandomY(), this.getRandomZ(0.3),
                        0, 0, 0);
            }
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(ParticleTypes.ENCHANT,
                        this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5),
                        0, 0.05, 0);
            }
        }
    }
}
