package com.shao.mythicalcreatures.item;

import java.util.function.Supplier;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;

/**
 * 自定义贴图刷怪蛋：覆写 getColor 返回纯白(0xFFFFFF)，使原版 SpawnEggItem 的
 * 单色背景染色在乘法混合下等于"不染色"，从而保留 item/generated 自绘 layer0 的彩色贴图原色，
 * 避免被压暗或偏色。
 *
 * Custom spawn eggs with a hand-drawn layer0 texture: override getColor() to return
 * pure white (0xFFFFFF). The vanilla SpawnEggItem applies its background tint as a
 * multiplicative blend, so returning white makes that blend a no-op, keeping the egg's
 * true colors instead of being darkened or color-shifted.
 */
public class MythicalSpawnEggItem extends ForgeSpawnEggItem {

    public MythicalSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties properties) {
        super(type, backgroundColor, highlightColor, properties);
    }

    @Override
    public int getColor(int tintIndex) {
        return 0xFFFFFF; // 纯白 = 不改变贴图原色（乘法染色下等于不染色）
    }
}
