package com.shao.mythicalcreatures.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * 模组投掷物公共基类：在原生 ThrowableItemProjectile 之上「加粗命中判定」。
 *
 * 原版命中检测内联在 ThrowableProjectile.tick() 里：用「移动线段 vs 实体 AABB.inflate(0.3) 的细线判定」，
 * 小子弹高速飞行极易擦肩而过，玩家体感“很难打中”。这里重写 tick()：
 *   - 直接搬到 ThrowableProjectile.tick 的核心移动（setPos 位置 + 重力 + 摩擦 + updateRotation），
 *     因为原版 ThrowableProjectile.tick 的 setPos 移动是该类唯一的移动来源（Entity.baseTick() 不移动）；
 *   - 把碰撞换成自定义「移动线段 vs 实体 AABB + HIT_RADIUS 粗化框」命中，并把每 tick 候选扫描盒相应外扩，
 *     使投掷物命中容易很多；
 *   - 显式排除持有者(owner)，避免出生点就在持有者体内导致“出生即自伤消失”。
 *
 * 不重写 getBoundingBox()：避免把出生点 / 移动 / 方块碰撞一并放大，导致"一出生就撞地消失"。
 */
public abstract class ModThrowableProjectile extends ThrowableItemProjectile {

    /** 命中粗化半径（方块）。原版等效约 0.3，这里放大到 1.0，命中容易很多。 */
    private static final double HIT_RADIUS = 1.0D;

    public ModThrowableProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public ModThrowableProjectile(EntityType<? extends ThrowableItemProjectile> type, LivingEntity owner, Level level) {
        super(type, owner, level);
    }

    @Override
    public void tick() {
        // 本类（ThrowableProjectile 的直接子类）必须自带移动 ——
        // 原版 ThrowableProjectile.tick() 通过 this.setPos(start+deltaMovement) 移动
        // （并处理重力、摩擦），而 Entity.baseTick() 本身不移动。因此不调 super.tick()，
        // 直接实现移动 + 重力 + 摩擦，并换成加粗命中判定。
        // baseTick() 不做移动，可安全调用 —— 它提供下界门/末地门传送状态机
        // (handleNetherPortal)、流体浸没状态、掉出世界检查等基础逻辑。
        this.baseTick();

        Vec3 start = this.position();
        Vec3 end = start.add(this.getDeltaMovement());

        // 1) 方块优先：传送门/末地门放行并触发传送，其余方块正常落地/撞墙
        HitResult blockHit = this.level().clip(new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos bpos = ((BlockHitResult) blockHit).getBlockPos();
            BlockState bstate = this.level().getBlockState(bpos);
            boolean portalHandled = false;
            if (bstate.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(bpos); // 触发下界传送（后续 tick 由 handleNetherPortal 完成）
                portalHandled = true;
            } else if (bstate.is(Blocks.END_GATEWAY)) {
                BlockEntity be = this.level().getBlockEntity(bpos);
                if (be instanceof TheEndGatewayBlockEntity egw && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    TheEndGatewayBlockEntity.teleportEntity(this.level(), bpos, bstate, this, egw);
                }
                portalHandled = true;
            }
            if (!portalHandled) {
                this.onHit(blockHit);
                return;
            }
        }

        // 2) 实体粗化命中：移动线段 vs 实体 AABB.inflate(HIT_RADIUS)，并排除持有者
        double bestDist = Double.MAX_VALUE;
        Entity bestEnt = null;
        Vec3 bestPos = null;
        AABB probe = this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D + HIT_RADIUS);
        for (Entity e : this.level().getEntities(this, probe, this::canHitEntity)) {
            if (e == this.getOwner()) continue; // 排除持有者，避免出生即自伤
            AABB box = e.getBoundingBox().inflate(HIT_RADIUS);
            Optional<Vec3> hit = box.clip(start, end);
            if (hit.isPresent()) {
                double d = start.distanceToSqr(hit.get());
                if (d < bestDist) {
                    bestDist = d;
                    bestEnt = e;
                    bestPos = hit.get();
                }
            }
        }
        if (bestEnt != null) {
            this.onHit(new EntityHitResult(bestEnt, bestPos));
            if (this.isRemoved()) return; // 命中后被 discard 则停止后续移动
        }

        // 3) 移动到新位置 + 默认旋转（核心移动逻辑来自 ThrowableProjectile.tick）
        this.setPos(end.x, end.y, end.z);
        this.updateRotation();
        float friction = this.isInWater() ? 0.8F : 0.99F;
        Vec3 vel = this.getDeltaMovement().scale(friction);
        if (!this.isNoGravity()) {
            vel = new Vec3(vel.x, vel.y - this.getGravity(), vel.z);
        }
        this.setDeltaMovement(vel);
    }
}
