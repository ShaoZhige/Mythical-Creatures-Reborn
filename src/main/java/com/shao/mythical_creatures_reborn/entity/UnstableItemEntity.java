package com.shao.mythical_creatures_reborn.entity;

import com.shao.mythical_creatures_reborn.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import com.shao.mythical_creatures_reborn.entity.ModThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

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

        // 霜冻中心：命中生物时锚定到生物脚下地面，避免命中点落在身体中段/头顶导致下方无支撑而不生成；
        // 命中方块时仍用命中点（本身就在方块表面，下方天然实心）。
        // Frost center: on entity hits anchor to the entity's feet (ground) so frost always has solid support;
        // on block hits keep the impact point (already on a solid surface).
        Vec3 frostPos = pos;
        if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult ehr = (EntityHitResult) result;
            if (ehr.getEntity() instanceof LivingEntity body) {
                frostPos = Vec3.atCenterOf(body.blockPosition());
            }
        }
        frostScatter(serverLevel, frostPos);

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

    /**
     * 命中后在附近地表覆盖两层霜冻：下层随机铺细雪 / 雪方块 / 冰，上层在其上方一格随机铺雪层。
     * Two-layer frost on hit: lower layer scatters powder snow / snow block / ice; upper layer adds a snow layer
     * on top of some lower blocks, only after lower positions are determined.
     *
     * 做法：遍历命中点周围体积，仅当「目标格正下方是实心方块」时才在该空气格放置（下层），
     * 绝不悬空、绝不替换既有方块；随后对下层方块随机挑一部分，在其正上方一格（仍为空气格）补一层雪层。
     */
    private void frostScatter(ServerLevel level, Vec3 hitPos) {
        BlockPos center = BlockPos.containing(hitPos);
        final int r = 4;                 // 覆盖半径（原 3，直径约 +30%）(coverage radius, was 3)
        final float placeChance = 0.65F; // 下层每格铺设概率，形成斑驳散布
        final float upperChance = 0.5F;  // 上层在已确定的下层方块之上补雪层的概率
        List<BlockPos> lower = new ArrayList<>();
        // 第一层：地表上方随机铺 细雪 / 雪方块 / 冰
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx * dx + dy * dy + dz * dz > r * r + 2) continue; // 球状范围，边缘自然渐淡
                    BlockPos p = center.offset(dx, dy, dz);
                    BlockPos below = p.below();
                    // 下方必须是实心方块（地面/墙面），否则不铺，避免悬空
                    if (!level.getBlockState(below).isSolidRender(level, below)) continue;
                    if (!level.getBlockState(p).isAir()) continue; // 仅覆盖空气格，绝不替换既有方块
                    if (level.random.nextFloat() > placeChance) continue; // 随机散布
                    level.setBlock(p, pickFrostBlock(level), 3);
                    lower.add(p);
                }
            }
        }
        // 第二层：在已确定的下层方块正上方一格，随机生成雪层（上方须为空气格，绝不替换）
        for (BlockPos p : lower) {
            if (level.random.nextFloat() > upperChance) continue; // 随机挑选部分下层方块
            BlockPos above = p.above();
            if (!level.getBlockState(above).isAir()) continue; // 上方非空：跳过，绝不替换
            level.setBlock(above, Blocks.SNOW.defaultBlockState(), 3); // 雪层 (snow layer)
        }
    }

    /** 随机选一种霜冻方块：细雪 / 雪方块 / 冰。 Pick a random frost block: powder snow / snow block / ice. */
    private BlockState pickFrostBlock(ServerLevel level) {
        int r = level.random.nextInt(3);
        if (r == 0) return Blocks.POWDER_SNOW.defaultBlockState();  // 细雪 (powder snow)
        if (r == 1) return Blocks.SNOW_BLOCK.defaultBlockState();   // 雪方块 (snow block)
        return Blocks.ICE.defaultBlockState();                      // 冰 (ice)
    }
}
