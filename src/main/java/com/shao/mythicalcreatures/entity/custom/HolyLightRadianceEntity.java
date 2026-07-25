package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.config.MythicalConfig;
import com.shao.mythicalcreatures.entity.AppleProjectileEntity;
import com.shao.mythicalcreatures.entity.BalloonProjectileEntity;
import com.shao.mythicalcreatures.entity.ButterflyProjectileEntity;
import com.shao.mythicalcreatures.entity.CupcakeProjectileEntity;
import com.shao.mythicalcreatures.entity.RainbowCloudEntity;
import com.shao.mythicalcreatures.entity.TwilightStarEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class HolyLightRadianceEntity extends PonyEntity {
    public HolyLightRadianceEntity(EntityType<HolyLightRadianceEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void refreshConfigAttributes() {
        var h = this.getAttribute(Attributes.MAX_HEALTH);
        if (h != null) h.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:holy_light_radiance", "max_health"));
        var s = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (s != null) s.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:holy_light_radiance", "move_speed"));
        var d = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (d != null) d.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:holy_light_radiance", "attack_damage"));
        this.setHealth(this.getMaxHealth());
    }
    @Override protected boolean canFly() { return false; }
    @Override protected Item getTamingItem() { return Items.APPLE; }
    @Nullable @Override protected net.minecraft.sounds.SoundEvent getAmbientSoundEvent() { return null; }
    @Nullable @Override protected net.minecraft.sounds.SoundEvent getHurtSoundEvent() { return null; }
    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythicalcreatures:holy_light_radiance", "max_health")).add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythicalcreatures:holy_light_radiance", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythicalcreatures:holy_light_radiance", "attack_damage")).add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }
    @Override protected void defineSynchedData() { super.defineSynchedData();  }
    @Override public void performRangedAttack(LivingEntity target, float power) {
        ThrowableItemProjectile projectile;
        int roll = this.random.nextInt(6);
        switch (roll) {
            case 0 -> projectile = new BalloonProjectileEntity(this.level(), this);
            case 1 -> projectile = new ButterflyProjectileEntity(this.level(), this);
            case 2 -> projectile = new CupcakeProjectileEntity(this.level(), this);
            case 3 -> projectile = new AppleProjectileEntity(this.level(), this);
            case 4 -> projectile = new TwilightStarEntity(this.level(), this);
            default -> projectile = new RainbowCloudEntity(this.level(), this);
        }
        projectile.shoot(target.getX() - this.getX(), target.getY(0.5) - this.getY(0.5),
                target.getZ() - this.getZ(), 1.2F, 1.0F);
        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.5F);
        this.level().addFreshEntity(projectile);
    }
}
