package com.shao.mythicalcreatures.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CutieMarkItem extends Item {
    private final String tooltipKey;

    public CutieMarkItem(Properties properties, String tooltipKey) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        // 常规可见：名字 + 台词
        tooltip.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY));
        // Shift 查看：具体效果
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(tooltipKey + ".detail").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.mythicalcreatures.cutiemark.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
