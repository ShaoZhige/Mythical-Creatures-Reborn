package com.shao.mythical_creatures_reborn.item;

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
        // 台词行：颜色由语言文件里的 § 码决定（不强制灰，否则会把台词染成灰色）
        tooltip.add(Component.translatable(tooltipKey));
        // 效果行：交给 SpecialTooltip 统一渲染（一行一个效果 + 按类型统一配色）
        String markId = tooltipKey.substring(tooltipKey.lastIndexOf('.') + 1);
        SpecialTooltip.appendMark(markId, tooltip);
    }
}
