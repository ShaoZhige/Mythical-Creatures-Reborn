package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.sound.ModSounds;

import com.shao.mythicalcreatures.config.MythicalConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GarbleEntity extends HostilePonyEntity {

    public GarbleEntity(EntityType<GarbleEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void refreshConfigAttributes() {
        applyCoreStats(entityId(), canFly());
    }

    @Override protected boolean canFly() { return true; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSoundEvent() { return ModSounds.GARBLE_AMBIENT.get(); }
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getHurtSoundEvent() { return ModSounds.GARBLE_HURT.get(); }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythicalcreatures:garble", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythicalcreatures:garble", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythicalcreatures:garble", "attack_damage"))
                .add(Attributes.FLYING_SPEED, (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:garble", "fly_speed"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }

    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        defineFlyData();
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {}

    @Override public void tick() {
        super.tick();
        tickFlight();
    }
}
