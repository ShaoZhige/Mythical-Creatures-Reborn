package com.shao.mythical_creatures_reborn.entity;
import com.shao.mythical_creatures_reborn.config.MythicalConfig;

import com.shao.mythical_creatures_reborn.item.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import com.shao.mythical_creatures_reborn.entity.ModThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RainbowDashSlashEntity extends ModThrowableProjectile {

    /** 配置注册名（common.toml / 编辑器里用这个键覆盖） */
    private static final String PID = "mythical_creatures_reborn:rainbow_dash_slash";

    /** 取自配置 {@code mythical_creatures_reborn:rainbow_dash_slash|range}（可在配置编辑器「投掷物」分类里改） */
    private static double range() { return MythicalConfig.DATA.projectileAttr(PID, "range"); }

    /** 取自配置 {@code mythical_creatures_reborn:rainbow_dash_slash|area_x}（可在配置编辑器「投掷物」分类里改） */
    private static double areaX() { return MythicalConfig.DATA.projectileAttr(PID, "area_x"); }

    /** 取自配置 {@code mythical_creatures_reborn:rainbow_dash_slash|area_z}（可在配置编辑器「投掷物」分类里改） */
    private static double areaZ() { return MythicalConfig.DATA.projectileAttr(PID, "area_z"); }

    /** 取自配置 {@code mythical_creatures_reborn:rainbow_dash_slash|area_y_up}（可在配置编辑器「投掷物」分类里改） */
    private static double areaYUp() { return MythicalConfig.DATA.projectileAttr(PID, "area_y_up"); }

    /** 取自配置 {@code mythical_creatures_reborn:rainbow_dash_slash|area_y_down}（可在配置编辑器「投掷物」分类里改） */
    private static double areaYDown() { return MythicalConfig.DATA.projectileAttr(PID, "area_y_down"); }

    /** 取自配置 {@code mythical_creatures_reborn:rainbow_dash_slash|damage}（可在配置编辑器「投掷物」分类里改） */
    private static float damage() { return (float) MythicalConfig.DATA.projectileAttr(PID, "damage"); }

    
    
    
    
    
    

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
        // 超出 18 格自动消失（owner 可能已死亡/离场/从存档加载后为 null，需判空防 NPE）
        net.minecraft.world.entity.Entity owner = this.getOwner();
        if (this.tickCount > 20 * 3 || (owner != null && this.distanceToSqr(owner) > range() * range())) {
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
            primary.hurt(magic, damage());
            primary.invulnerableTime = 0; // 重置无敌帧，允许后续伤害
        }

        // 范围伤害：17x17x20 长方体
        AABB area = new AABB(
                center.x - areaX() / 2.0, center.y - areaYDown(), center.z - areaZ() / 2.0,
                center.x + areaX() / 2.0, center.y + areaYUp(),  center.z + areaZ() / 2.0
        );

        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != this.getOwner() && e != result.getEntity() && e.isAlive());

        for (LivingEntity target : targets) {
            target.hurt(magic, damage());
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
