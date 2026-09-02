package com.shao.mythical_creatures_reborn.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 统一两套按键提示，避免每个套装/特殊物品各写一份"按住 Shift 显示 xxx"：
 * - SET_HINT：套装，翻译键固定为「按住 Shift 显示套装效果」
 * - SPECIAL_HINT：特殊装备/武器，翻译键固定为「按住 Shift 显示详情」
 * 各自的实际效果文本仍放在各自的 .detail 键里（套装 set.<id>.detail，特殊 special.<key>.detail）。
 */
public final class SpecialTooltip {

    public static final String SET_HINT_KEY = "tooltip.mythical_creatures_reborn.set.hint";
    public static final String SPECIAL_HINT_KEY = "tooltip.mythical_creatures_reborn.special.hint";

    private SpecialTooltip() {}

    /**
     * 套装 tooltip：Shift 按下显示 set.<setId>.detail，否则显示统一的套装提示。
     */
    public static void appendSet(ItemStack stack, String setId, List<Component> tooltip) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.mythical_creatures_reborn.set." + setId + ".detail")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable(SET_HINT_KEY).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    /**
     * 特殊装备/武器 tooltip：Shift 按下显示 special.<detailKey>.detail，否则显示统一的详情提示。
     */
    public static void appendSpecial(String detailKey, ItemStack stack, List<Component> tooltip) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.mythical_creatures_reborn.special." + detailKey + ".detail")
                    .withStyle(ChatFormatting.BLUE));
        } else {
            tooltip.add(Component.translatable(SPECIAL_HINT_KEY).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
