package com.shao.mythicalcreatures.effect;

import com.shao.mythicalcreatures.config.MythicalConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedingEffect extends MobEffect {

    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0xAA0000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        float base = (float) MythicalConfig.DATA.get("global_params", "bleeding_base", 1.0);
        float amp  = (float) MythicalConfig.DATA.get("global_params", "bleeding_amp", 0.5);
        float damage = base + (amplifier * amp);

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
}
