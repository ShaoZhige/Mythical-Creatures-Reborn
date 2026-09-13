package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.sound.ModSounds;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CockatriceEntity extends HostilePonyEntity {

    public CockatriceEntity(EntityType<CockatriceEntity> type, Level level) {
        super(type, level);
    }

    @Override protected boolean canFly() { return false; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() { return ModSounds.COCKATRICE_AMBIENT.get(); }
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return ModSounds.COCKATRICE_HURT.get(); }

    public static AttributeSupplier.Builder createAttributes() {
        return PonyAttributes.of("mythical_creatures_reborn:cockatrice");
    }

    @Override protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {
        // no ranged attack
    }
}
