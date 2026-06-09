package com.shao.mythicalcreatures.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class BleedingEffect extends MobEffect {

    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0xAA0000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 基础伤害：1.0 HP/秒 (amplifier 0)
        float damage = 1.0F + (amplifier * 0.5F);

        // 如果实体在移动，伤害翻倍
        if (entity.getDeltaMovement().horizontalDistance() > 0.01F) {
            damage *= 2.0F;
        }

        entity.hurt(entity.damageSources().generic(), damage);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 每秒触发一次
        int interval = 20;
        return duration % interval == 0;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        super.removeAttributeModifiers(entity, attributes, amplifier);
    }
}
