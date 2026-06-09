package com.shao.mythicalcreatures.item;

import net.minecraft.world.item.PickaxeItem;

/**
 * 挖掘者 — 钻石镐等级 + 铁镐速度 + 3×3×3 范围挖掘。
 * 范围挖掘逻辑由 ExcavatorHandler (BlockEvent.BreakEvent) 实现。
 */
public class ExcavatorItem extends PickaxeItem {

    public ExcavatorItem(Properties properties) {
        super(ModTiers.EXCAVATOR, 1, -2.8F, properties);
    }
}
