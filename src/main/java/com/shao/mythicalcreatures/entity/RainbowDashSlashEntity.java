package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RainbowDashSlashEntity extends ThrowableItemProjectile {

    private static final double RANGE = 18.0;   // 最大射程
    private static final int AREA_X = 17;        // X 范围
    private static final int AREA_Z = 17;        // Z 范围
    private static final int AREA_Y_UP = 10;     // Y 正方向
    private static final int AREA_Y_DOWN = 10;   // Y 负方向
    private static final float DAMAGE = 10.0F;

    public RainbowDashSlashEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = true; // 无碰撞体积，穿透方块
    }

    public RainbowDashSlashEntity(Level level, LivingEntity shooter) {
        super(ModEntities.RAINBOW_DASH_SLASH.get(), shooter, level);
        this.noPhysics = true;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.RAINBOW_DASH_SWORD.get();
    }

    @Override
    public void tick() {
        super.tick();
        // 超出 18 格自动消失
        if (this.tickCount > 20 * 3 || this.distanceToSqr(this.getOwner()) > RANGE * RANGE) {
            this.discard();
            return;
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.level().isClientSide) return;

        Vec3 center = result.getLocation();
        DamageSource magic = this.damageSources().magic();

        // 命中目标
        if (result.getEntity() instanceof LivingEntity primary) {
            primary.hurt(magic, DAMAGE);
            primary.invulnerableTime = 0; // 重置无敌帧，允许后续伤害
        }

        // 范围伤害：17x17x20 长方体
        AABB area = new AABB(
                center.x - AREA_X / 2.0, center.y - AREA_Y_DOWN, center.z - AREA_Z / 2.0,
                center.x + AREA_X / 2.0, center.y + AREA_Y_UP,  center.z + AREA_Z / 2.0
        );

        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != this.getOwner() && e != result.getEntity() && e.isAlive());

        for (LivingEntity target : targets) {
            target.hurt(magic, DAMAGE);
            target.invulnerableTime = 0;
        }

        this.discard();
    }

    /** 客户端不渲染 */
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return false;
    }
}
