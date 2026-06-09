package com.shao.mythicalcreatures.item;

import com.shao.mythicalcreatures.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class BearClawSword extends SwordItem {

    public BearClawSword(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 施加 5 秒流血（amplifier 0 = 每秒 0.5 心，移动时 1 心）
        target.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 100, 0), attacker);
        return super.hurtEnemy(stack, target, attacker);
    }
}
