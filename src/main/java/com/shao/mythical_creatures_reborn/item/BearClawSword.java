package com.shao.mythical_creatures_reborn.item;

import com.shao.mythical_creatures_reborn.effect.ModEffects;
import com.shao.mythical_creatures_reborn.util.EffectGrants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BearClawSword extends SwordItem {

    /** 流血持续时长（tick）：5 秒。 */
    private static final int BLEED_TICKS = 100;

    public BearClawSword(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 施加 5 秒流血（amplifier 0 = 每秒 0.5 心，移动时 1 心）
        EffectGrants.timed(target, ModEffects.BLEEDING.get(), BLEED_TICKS, 0, attacker);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        SpecialTooltip.appendSpecial("bear_claw_sword", stack, tooltip);
    }
}
