package com.shao.mythical_creatures_reborn.entity;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import com.shao.mythical_creatures_reborn.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** 梅菲斯之球：单纯有伤害的投掷物，命中实体造成伤害后消失。 */
public class MavisOrbEntity extends ModThrowableProjectile {

    /** 配置注册名（common.toml / 编辑器里用这个键覆盖） */
    private static final String PID = "mythical_creatures_reborn:mavis_orb_projectile";

    /** 取自配置 {@code mythical_creatures_reborn:mavis_orb_projectile|damage}（可在配置编辑器「投掷物」分类里改） */
    private static float damage() { return (float) MythicalConfig.DATA.projectileAttr(PID, "damage"); }

    public MavisOrbEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public MavisOrbEntity(Level level, LivingEntity shooter) {
        super(ModEntities.MAVIS_ORB_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.MAVIS_ORBS.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level().isClientSide) return;
        if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult ehr = (EntityHitResult) result;
            if (ehr.getEntity() instanceof LivingEntity target && target != this.getOwner()) {
                target.hurt(this.damageSources().thrown(this, this.getOwner()), damage());
            }
        }
        this.discard();
    }
}
