package com.shao.mythicalcreatures.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;

/**
 * Custom tool tiers ported from MLP Mythical Creatures 1.7.10.
 * Values are scaled to fit Minecraft 1.20.1 balance ranges.
 */
public class ModTiers {

    // Level 4+ custom tiers (above netherite)
    public static final Tier REINFORCED = new ForgeTier(4, 3000, 14.0F, 6.0F, 22,
            Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.EMPTY);

    public static final Tier TWILIGHT = new ForgeTier(4, 2000, 16.0F, 7.0F, 25,
            Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.EMPTY);

    public static final Tier WOW = new ForgeTier(4, 1500, 12.0F, 5.0F, 16,
            Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.EMPTY);

    public static final Tier APPLE = new ForgeTier(3, 500, 8.0F, 3.0F, 12,
            Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.EMPTY);

    public static final Tier DASH = new ForgeTier(3, 800, 9.0F, 4.0F, 18,
            Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.EMPTY);

    public static final Tier BEAR = new ForgeTier(2, 200, 5.0F, 2.0F, 8,
            BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.EMPTY);

    public static final Tier CRAG = new ForgeTier(4, 1300, 12.0F, 5.0F, 8,
            Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.EMPTY);

    public static final Tier APPLEJACK = new ForgeTier(4, 2800, 15.0F, 8.0F, 10,
            Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.EMPTY);

    public static final Tier CRYSTAL = new ForgeTier(4, 3200, 13.0F, 6.0F, 25,
            Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.EMPTY);

    public static final Tier ALICORN = new ForgeTier(5, 4000, 18.0F, 10.0F, 25,
            Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.EMPTY);

    public static final Tier URSA = new ForgeTier(5, 4500, 20.0F, 12.0F, 2,
            Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.EMPTY);

    public static final Tier RANDOM2 = new ForgeTier(2, 180, 5.0F, 2.0F, 10,
            BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.EMPTY);

    public static final Tier RANDOM3 = new ForgeTier(3, 250, 6.0F, 3.0F, 12,
            BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.EMPTY);

    public static final Tier RANDOM4 = new ForgeTier(3, 300, 7.0F, 4.0F, 12,
            BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.EMPTY);

    /** 泥土剑 — 等价木剑 */
    public static final Tier DIRT = new ForgeTier(0, 59, 2.0F, 0.0F, 15,
            BlockTags.NEEDS_STONE_TOOL, () -> Ingredient.EMPTY);

    /** 挖掘者 — 钻石挖掘等级 + 铁镐基础速度 */
    public static final Tier EXCAVATOR = new ForgeTier(3, 1561, 6.0F, 0.0F, 10,
            BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.EMPTY);
}
