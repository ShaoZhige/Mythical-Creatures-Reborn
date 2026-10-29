package com.shao.mythical_creatures_reborn.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 友谊就是魔法 — 一击必杀。
 * 用 setHealth(0) 绕过护甲/抗性直接秒杀目标。
 */
public class ManeSixItem extends Item {

    public ManeSixItem(Properties props) {
        super(props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide) {
            target.setHealth(0);
            target.invulnerableTime = 0;
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        SpecialTooltip.appendSpecial("mane_six", stack, tooltip);
    }
}
