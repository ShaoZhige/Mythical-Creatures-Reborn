package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.config.MythicalConfig;
import com.shao.mythicalcreatures.entity.ButterflyProjectileEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class FluttershyEntity extends PonyEntity {
    public FluttershyEntity(EntityType<FluttershyEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void refreshConfigAttributes() {
        var h = this.getAttribute(Attributes.MAX_HEALTH);
        if (h != null) h.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:fluttershy", "max_health"));
        var s = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (s != null) s.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:fluttershy", "move_speed"));
        var d = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (d != null) d.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:fluttershy", "attack_damage"));
        var f = this.getAttribute(Attributes.FLYING_SPEED);
        if (f != null) f.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:fluttershy", "fly_speed"));
        this.setHealth(this.getMaxHealth());
    }
    @Override protected boolean canFly() { return true; }
    @Override protected Item getTamingItem() { return Items.APPLE; }
    @Nullable @Override protected net.minecraft.sounds.SoundEvent getAmbientSoundEvent() { return null; }
    @Nullable @Override protected net.minecraft.sounds.SoundEvent getHurtSoundEvent() { return null; }
    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythicalcreatures:fluttershy", "max_health")).add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythicalcreatures:fluttershy", "move_speed"))
                .add(Attributes.FLYING_SPEED, MythicalConfig.DATA.entityAttr("mythicalcreatures:fluttershy", "fly_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythicalcreatures:fluttershy", "attack_damage")).add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }
    @Override protected void defineSynchedData() { super.defineSynchedData(); defineFlyData(); }
    @Override public void performRangedAttack(LivingEntity target, float power) {
        ButterflyProjectileEntity projectile = new ButterflyProjectileEntity(this.level(), this);
        projectile.shoot(target.getX() - this.getX(), target.getY(0.5) - this.getY(0.5),
                target.getZ() - this.getZ(), 0.8F, 1.5F);
        this.playSound(SoundEvents.EGG_THROW, 0.5F, 1.2F);
        this.level().addFreshEntity(projectile);
    }
}
