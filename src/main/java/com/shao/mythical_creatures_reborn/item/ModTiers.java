package com.shao.mythical_creatures_reborn.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;

/**
 * 工具等级。| Tool tiers.
 * 默认值与 mythiccreatures-common.toml [equipment] 段完全一致。| Defaults match config [equipment] entries.
 * 如需调整属性请修改配置文件，重启游戏后生效。| To adjust stats, edit the config file and restart.
 */
public class ModTiers {

    private static Tier make(int lv, net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> tag,
                              int uses, float spd, float atk, int ench) {
        return new ForgeTier(lv, uses, spd, atk, ench, tag, () -> Ingredient.EMPTY);
    }

    public static final Tier REINFORCED  = make(5, Tags.Blocks.NEEDS_NETHERITE_TOOL, 3000,14,6,22);
    public static final Tier TWILIGHT    = make(4, Tags.Blocks.NEEDS_NETHERITE_TOOL, 2000,16,7,25);
    public static final Tier EXPLOSIVE   = make(4, Tags.Blocks.NEEDS_NETHERITE_TOOL, 1500,12,5,16);
    public static final Tier APPLE       = make(1, BlockTags.NEEDS_STONE_TOOL,       100, 4,1,5);
    public static final Tier DASH        = make(3, Tags.Blocks.NEEDS_NETHERITE_TOOL, 800, 9,4,18);
    public static final Tier BEAR        = make(2, BlockTags.NEEDS_IRON_TOOL,        200, 5,2,8);
    public static final Tier CRAG        = make(4, Tags.Blocks.NEEDS_NETHERITE_TOOL, 1300,12,5,8);
    public static final Tier APPLEJACK   = make(4, Tags.Blocks.NEEDS_NETHERITE_TOOL, 2800,15,8,10);
    public static final Tier CRYSTAL     = make(4, Tags.Blocks.NEEDS_NETHERITE_TOOL, 3200,13,6,25);
    public static final Tier ALICORN     = make(5, Tags.Blocks.NEEDS_NETHERITE_TOOL, 4000,18,10,25);
    public static final Tier URSA        = make(5, Tags.Blocks.NEEDS_NETHERITE_TOOL, 4500,20,12,2);
    public static final Tier RANDOM2     = make(0, BlockTags.NEEDS_STONE_TOOL,       80,  5,1,10);
    public static final Tier DIRT        = make(0, BlockTags.NEEDS_STONE_TOOL,       39,  2,0,15);
    public static final Tier EXCAVATOR   = make(3, BlockTags.NEEDS_DIAMOND_TOOL,     2300,6,0,10);
    public static final Tier FLUTTERSHY  = make(3, BlockTags.NEEDS_DIAMOND_TOOL,     750, 8,3.5F,22);
    public static final Tier PINKIE_PIE  = make(3, BlockTags.NEEDS_DIAMOND_TOOL,     700,10,3,20);
    public static final Tier RARITY      = make(3, BlockTags.NEEDS_DIAMOND_TOOL,     850,7.5F,4,25);
    public static final Tier HOLY_LIGHT  = make(3, BlockTags.NEEDS_DIAMOND_TOOL,     900,8.5F,4.5F,25);
}
