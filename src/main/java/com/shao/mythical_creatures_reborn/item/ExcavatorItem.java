package com.shao.mythical_creatures_reborn.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 挖掘者 — 钻石镐等级 + 铁镐速度 + 3×3×3 范围挖掘。
 * 范围挖掘逻辑由 ExcavatorHandler (BlockEvent.BreakEvent) 实现。
 */
public class ExcavatorItem extends PickaxeItem {

    public ExcavatorItem(Properties properties) {
        super(ModTiers.EXCAVATOR, 1, -2.8F, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        SpecialTooltip.appendSpecial("digger", stack, tooltip);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
