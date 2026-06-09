package com.shao.mythicalcreatures.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.block.ModBlocks;
import com.shao.mythicalcreatures.entity.ModEntities;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

/**
 * All items ported from MLP Mythical Creatures 1.7.10.
 * No special behaviors - pure items only.
 */
@SuppressWarnings("unused")
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MythicalCreaturesMod.MODID);

    // ==================== 零、方块 (10) ====================

    public static final RegistryObject<Item> APPLE_BLOCK = ITEMS.register("apple_block",
            () -> new BlockItem(ModBlocks.APPLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ARCTIC_BLOCK = ITEMS.register("arctic_block",
            () -> new BlockItem(ModBlocks.ARCTIC.get(), new Item.Properties()));
    public static final RegistryObject<Item> BEAR_FUR_BLOCK = ITEMS.register("bear_fur_block",
            () -> new BlockItem(ModBlocks.BEAR_FUR.get(), new Item.Properties()));
    public static final RegistryObject<Item> BONE_BLOCK = ITEMS.register("bone_block",
            () -> new BlockItem(ModBlocks.BONE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_BLOCK = ITEMS.register("crystal_block",
            () -> new BlockItem(ModBlocks.CRYSTAL.get(), new Item.Properties()));
    public static final RegistryObject<Item> DARK_CRYSTAL_BLOCK = ITEMS.register("dark_crystal_block",
            () -> new BlockItem(ModBlocks.DARK_CRYSTAL.get(), new Item.Properties()));
    public static final RegistryObject<Item> HARD_APPLE_BLOCK = ITEMS.register("hard_apple_block",
            () -> new BlockItem(ModBlocks.HARD_APPLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> PHOENIX_BLOCK = ITEMS.register("phoenix_block",
            () -> new BlockItem(ModBlocks.PHOENIX.get(), new Item.Properties()));
    public static final RegistryObject<Item> ROBOT_BLOCK = ITEMS.register("robot_block",
            () -> new BlockItem(ModBlocks.ROBOT.get(), new Item.Properties()));
    public static final RegistryObject<Item> TWILICORN_BLOCK = ITEMS.register("twilicorn_block",
            () -> new BlockItem(ModBlocks.TWILICORN.get(), new Item.Properties()));

    // ==================== 一、特殊物品 & 材料 (30) ====================

    public static final RegistryObject<Item> BONE_WAND = ITEMS.register("bone_wand",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TWILICANE = ITEMS.register("twilicane",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_BONE = ITEMS.register("reinforced_bone",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SCORCHING_BONE = ITEMS.register("scorching_bone",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<MeteorFireballItem> METEOR_FIREBALL = ITEMS.register("meteor_fireball",
            () -> new MeteorFireballItem(new Item.Properties()));
    public static final RegistryObject<BowserEyeItem> EYE_OF_BOWSER = ITEMS.register("eye_of_bowser",
            () -> new BowserEyeItem(new Item.Properties()));
    public static final RegistryObject<Item> LUNA_ECLIPSE = ITEMS.register("luna_eclipse",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BOWSER_ROD = ITEMS.register("bowser_rod",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<PhoenixFeatherBow> PHOENIX_BOW = ITEMS.register("phoenix_bow",
            () -> new PhoenixFeatherBow(new Item.Properties().durability(6500)));
    public static final RegistryObject<Item> TWILIGHT_SPARKLE_BOW = ITEMS.register("twilight_sparkle_bow",
            () -> new TwilightSparkleBow(new Item.Properties().durability(12500)));
    public static final RegistryObject<Item> MANE_SIX = ITEMS.register("mane_six",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<TwilightStarItem> TWILIGHT_STAR_ITEM = ITEMS.register("twilight_star",
            () -> new TwilightStarItem(new Item.Properties()));
    public static final RegistryObject<RainbowCloudItem> RAINBOW_CLOUD_ITEM = ITEMS.register("rainbow_cloud",
            () -> new RainbowCloudItem(new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_GEMS = ITEMS.register("diamond_gems",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BUTTERFLIES = ITEMS.register("butterflies",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<AppleProjectileItem> APPLES_ITEM = ITEMS.register("apples",
            () -> new AppleProjectileItem(new Item.Properties()));
    public static final RegistryObject<Item> BALLOONS = ITEMS.register("balloons",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MAVIS_ORBS = ITEMS.register("mavis_orbs",
            () -> new Item(new Item.Properties()));

    // 可爱标志
    public static final RegistryObject<Item> APPLEJACK_CUTIEMARK = ITEMS.register("applejack_cutiemark",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PINKIE_PIE_CUTIEMARK = ITEMS.register("pinkie_pie_cutiemark",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FLUTTERSHY_CUTIEMARK = ITEMS.register("fluttershy_cutiemark",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RARITY_CUTIEMARK = ITEMS.register("rarity_cutiemark",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAINBOW_DASH_CUTIEMARK = ITEMS.register("rainbow_dash_cutiemark",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TWILIGHT_CUTIEMARK = ITEMS.register("twilight_cutiemark",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BEAR_FUR = ITEMS.register("bear_fur",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BEAR_CLAW = ITEMS.register("bear_claw",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<PhoenixFeatherItem> PHOENIX_FEATHER = ITEMS.register("phoenix_feather",
            () -> new PhoenixFeatherItem(new Item.Properties()));
    public static final RegistryObject<Item> CRAGADILE_SCALE = ITEMS.register("cragadile_scale",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DARK_CRYSTAL = ITEMS.register("dark_crystal",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ARCTIC_STINGER = ITEMS.register("arctic_stinger",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UNSTABLE_ITEM = ITEMS.register("unstable_item",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CENTIPEDE_ANTENNA = ITEMS.register("centipede_antenna",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_GEM = ITEMS.register("crystal_gem",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> YAK_HORN = ITEMS.register("yak_horn",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RHINO_HORN = ITEMS.register("rhino_horn",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TWILICORN_MAGIC_EGG = ITEMS.register("twilicorn_magic_egg",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MAGIC_SPAWN_EGG = ITEMS.register("magic_spawn_egg",
            () -> new Item(new Item.Properties()));

    // ==================== 二、武器 (13) ====================

    public static final RegistryObject<BowserSword> BOWSERS_SWORD = ITEMS.register("bowsers_sword",
            () -> new BowserSword(ModTiers.REINFORCED, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> TWILIGHT_SWORD = ITEMS.register("twilight_sword",
            () -> new TwilightSword(ModTiers.TWILIGHT, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> EXPLOSIVE_SWORD = ITEMS.register("explosive_sword",
            () -> new SwordItem(ModTiers.WOW, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> APPLE_SWORD = ITEMS.register("apple_sword",
            () -> new SwordItem(ModTiers.APPLE, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> RAINBOW_DASH_SWORD = ITEMS.register("rainbow_dash_sword",
            () -> new RainbowDashSword(ModTiers.DASH, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<BearClawSword> BEAR_CLAW_SWORD = ITEMS.register("bear_claw_sword",
            () -> new BearClawSword(ModTiers.BEAR, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> CRAG_HAMMER = ITEMS.register("crag_hammer",
            () -> new SwordItem(ModTiers.CRAG, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> APPLEJACK_SWORD = ITEMS.register("applejack_sword",
            () -> new SwordItem(ModTiers.APPLEJACK, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> CRYSTAL_SWORD = ITEMS.register("crystal_sword",
            () -> new SwordItem(ModTiers.CRYSTAL, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> ALICORN_SWORD = ITEMS.register("alicorn_sword",
            () -> new SwordItem(ModTiers.ALICORN, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> DAGGER = ITEMS.register("dagger",
            () -> new SwordItem(ModTiers.RANDOM2, 2, -2.0F, new Item.Properties()));
    public static final RegistryObject<SwordItem> URSA_CLAWS = ITEMS.register("ursa_claws",
            () -> new SwordItem(ModTiers.URSA, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> DIRT_SWORD = ITEMS.register("dirt_sword",
            () -> new SwordItem(ModTiers.DIRT, 3, -2.4F, new Item.Properties()));

    // ==================== 三、工具 (21) ====================

    // Bowser's Tools
    public static final RegistryObject<PickaxeItem> BOWSERS_PICKAXE = ITEMS.register("bowsers_pickaxe",
            () -> new PickaxeItem(ModTiers.REINFORCED, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<AxeItem> BOWSERS_AXE = ITEMS.register("bowsers_axe",
            () -> new AxeItem(ModTiers.REINFORCED, 5.0F, -3.0F, new Item.Properties()));
    public static final RegistryObject<HoeItem> BOWSERS_HOE = ITEMS.register("bowsers_hoe",
            () -> new HoeItem(ModTiers.REINFORCED, -4, 0.0F, new Item.Properties()));
    public static final RegistryObject<ShovelItem> BOWSERS_SHOVEL = ITEMS.register("bowsers_shovel",
            () -> new ShovelItem(ModTiers.REINFORCED, 1.5F, -3.0F, new Item.Properties()));

    // Twilight Tools
    public static final RegistryObject<PickaxeItem> TWILIGHT_PICKAXE = ITEMS.register("twilight_pickaxe",
            () -> new PickaxeItem(ModTiers.TWILIGHT, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<AxeItem> TWILIGHT_AXE = ITEMS.register("twilight_axe",
            () -> new AxeItem(ModTiers.TWILIGHT, 5.0F, -3.0F, new Item.Properties()));
    public static final RegistryObject<HoeItem> TWILIGHT_HOE = ITEMS.register("twilight_hoe",
            () -> new HoeItem(ModTiers.TWILIGHT, -4, 0.0F, new Item.Properties()));
    public static final RegistryObject<ShovelItem> TWILIGHT_SHOVEL = ITEMS.register("twilight_shovel",
            () -> new ShovelItem(ModTiers.TWILIGHT, 1.5F, -3.0F, new Item.Properties()));

    // Apple Tools
    public static final RegistryObject<PickaxeItem> APPLE_PICKAXE = ITEMS.register("apple_pickaxe",
            () -> new PickaxeItem(ModTiers.APPLE, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<AxeItem> APPLE_AXE = ITEMS.register("apple_axe",
            () -> new AxeItem(ModTiers.APPLE, 5.0F, -3.0F, new Item.Properties()));
    public static final RegistryObject<HoeItem> APPLE_HOE = ITEMS.register("apple_hoe",
            () -> new HoeItem(ModTiers.APPLE, -3, 0.0F, new Item.Properties()));
    public static final RegistryObject<ShovelItem> APPLE_SHOVEL = ITEMS.register("apple_shovel",
            () -> new ShovelItem(ModTiers.APPLE, 1.5F, -3.0F, new Item.Properties()));

    // Bear Claw Tools
    public static final RegistryObject<PickaxeItem> BEAR_CLAW_PICKAXE = ITEMS.register("bear_claw_pickaxe",
            () -> new PickaxeItem(ModTiers.BEAR, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<AxeItem> BEAR_CLAW_AXE = ITEMS.register("bear_claw_axe",
            () -> new AxeItem(ModTiers.BEAR, 5.0F, -3.0F, new Item.Properties()));
    public static final RegistryObject<HoeItem> BEAR_CLAW_HOE = ITEMS.register("bear_claw_hoe",
            () -> new HoeItem(ModTiers.BEAR, -2, 0.0F, new Item.Properties()));
    public static final RegistryObject<ShovelItem> BEAR_CLAW_SHOVEL = ITEMS.register("bear_claw_shovel",
            () -> new ShovelItem(ModTiers.BEAR, 1.5F, -3.0F, new Item.Properties()));

    // AppleJack Tools
    public static final RegistryObject<PickaxeItem> APPLEJACK_PICKAXE = ITEMS.register("applejack_pickaxe",
            () -> new PickaxeItem(ModTiers.APPLEJACK, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<AxeItem> APPLEJACK_AXE = ITEMS.register("applejack_axe",
            () -> new AxeItem(ModTiers.APPLEJACK, 5.0F, -3.0F, new Item.Properties()));
    public static final RegistryObject<HoeItem> APPLEJACK_HOE = ITEMS.register("applejack_hoe",
            () -> new HoeItem(ModTiers.APPLEJACK, -4, 0.0F, new Item.Properties()));
    public static final RegistryObject<ShovelItem> APPLEJACK_SHOVEL = ITEMS.register("applejack_shovel",
            () -> new ShovelItem(ModTiers.APPLEJACK, 1.5F, -3.0F, new Item.Properties()));

    // Special: Digger
    public static final RegistryObject<ExcavatorItem> DIGGER = ITEMS.register("digger",
            () -> new ExcavatorItem(new Item.Properties()));

    // ==================== 四、盔甲 - 7套28件 ====================

    // Bowser's Armor (Reinforced)
    public static final RegistryObject<ArmorItem> BOWSERS_HELMET = ITEMS.register("bowsers_helmet",
            () -> new ModArmorItem(ModArmorMaterials.REINFORCED, ArmorItem.Type.HELMET, new Item.Properties(), "bowsers"));
    public static final RegistryObject<ArmorItem> BOWSERS_CHESTPLATE = ITEMS.register("bowsers_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.REINFORCED, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "bowsers"));
    public static final RegistryObject<ArmorItem> BOWSERS_LEGGINGS = ITEMS.register("bowsers_leggings",
            () -> new ModArmorItem(ModArmorMaterials.REINFORCED, ArmorItem.Type.LEGGINGS, new Item.Properties(), "bowsers"));
    public static final RegistryObject<ArmorItem> BOWSERS_BOOTS = ITEMS.register("bowsers_boots",
            () -> new ModArmorItem(ModArmorMaterials.REINFORCED, ArmorItem.Type.BOOTS, new Item.Properties(), "bowsers"));

    // Twilight Armor
    public static final RegistryObject<ArmorItem> TWILIGHT_HELMET = ITEMS.register("twilight_helmet",
            () -> new ModArmorItem(ModArmorMaterials.TWILIGHT, ArmorItem.Type.HELMET, new Item.Properties(), "twilight"));
    public static final RegistryObject<ArmorItem> TWILIGHT_CHESTPLATE = ITEMS.register("twilight_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.TWILIGHT, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "twilight"));
    public static final RegistryObject<ArmorItem> TWILIGHT_LEGGINGS = ITEMS.register("twilight_leggings",
            () -> new ModArmorItem(ModArmorMaterials.TWILIGHT, ArmorItem.Type.LEGGINGS, new Item.Properties(), "twilight"));
    public static final RegistryObject<ArmorItem> TWILIGHT_BOOTS = ITEMS.register("twilight_boots",
            () -> new ModArmorItem(ModArmorMaterials.TWILIGHT, ArmorItem.Type.BOOTS, new Item.Properties(), "twilight"));

    // Dark Crystal Armor
    public static final RegistryObject<ArmorItem> DARK_CRYSTAL_HELMET = ITEMS.register("dark_crystal_helmet",
            () -> new ModArmorItem(ModArmorMaterials.DARK, ArmorItem.Type.HELMET, new Item.Properties(), "dark_crystal"));
    public static final RegistryObject<ArmorItem> DARK_CRYSTAL_CHESTPLATE = ITEMS.register("dark_crystal_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.DARK, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "dark_crystal"));
    public static final RegistryObject<ArmorItem> DARK_CRYSTAL_LEGGINGS = ITEMS.register("dark_crystal_leggings",
            () -> new ModArmorItem(ModArmorMaterials.DARK, ArmorItem.Type.LEGGINGS, new Item.Properties(), "dark_crystal"));
    public static final RegistryObject<ArmorItem> DARK_CRYSTAL_BOOTS = ITEMS.register("dark_crystal_boots",
            () -> new ModArmorItem(ModArmorMaterials.DARK, ArmorItem.Type.BOOTS, new Item.Properties(), "dark_crystal"));

    // Apple Armor
    public static final RegistryObject<ArmorItem> APPLE_HELMET = ITEMS.register("apple_helmet",
            () -> new ModArmorItem(ModArmorMaterials.APPLE_ARMOR, ArmorItem.Type.HELMET, new Item.Properties(), "apple"));
    public static final RegistryObject<ArmorItem> APPLE_CHESTPLATE = ITEMS.register("apple_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.APPLE_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "apple"));
    public static final RegistryObject<ArmorItem> APPLE_LEGGINGS = ITEMS.register("apple_leggings",
            () -> new ModArmorItem(ModArmorMaterials.APPLE_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties(), "apple"));
    public static final RegistryObject<ArmorItem> APPLE_BOOTS = ITEMS.register("apple_boots",
            () -> new ModArmorItem(ModArmorMaterials.APPLE_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties(), "apple"));

    // Bear Fur Armor
    public static final RegistryObject<ArmorItem> BEAR_FUR_HELMET = ITEMS.register("bear_fur_helmet",
            () -> new ModArmorItem(ModArmorMaterials.BEAR_ARMOR, ArmorItem.Type.HELMET, new Item.Properties(), "bear_fur"));
    public static final RegistryObject<ArmorItem> BEAR_FUR_CHESTPLATE = ITEMS.register("bear_fur_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.BEAR_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "bear_fur"));
    public static final RegistryObject<ArmorItem> BEAR_FUR_LEGGINGS = ITEMS.register("bear_fur_leggings",
            () -> new ModArmorItem(ModArmorMaterials.BEAR_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties(), "bear_fur"));
    public static final RegistryObject<ArmorItem> BEAR_FUR_BOOTS = ITEMS.register("bear_fur_boots",
            () -> new ModArmorItem(ModArmorMaterials.BEAR_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties(), "bear_fur"));

    // AppleJack Armor — 每件 +0.05 击退抗性
    public static final RegistryObject<ArmorItem> APPLEJACK_HELMET = ITEMS.register("applejack_helmet",
            () -> new ModArmorItem(ModArmorMaterials.APPLEJACK_ARMOR, ArmorItem.Type.HELMET, new Item.Properties(),
                    kbMod(0.05, UUID.fromString("a01b01c1-d111-e111-f111-000000000111")), "applejack"));
    public static final RegistryObject<ArmorItem> APPLEJACK_CHESTPLATE = ITEMS.register("applejack_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.APPLEJACK_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties(),
                    kbMod(0.05, UUID.fromString("a02b02c2-d222-e222-f222-000000000222")), "applejack"));
    public static final RegistryObject<ArmorItem> APPLEJACK_LEGGINGS = ITEMS.register("applejack_leggings",
            () -> new ModArmorItem(ModArmorMaterials.APPLEJACK_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties(),
                    kbMod(0.05, UUID.fromString("a03b03c3-d333-e333-f333-000000000333")), "applejack"));
    public static final RegistryObject<ArmorItem> APPLEJACK_BOOTS = ITEMS.register("applejack_boots",
            () -> new ModArmorItem(ModArmorMaterials.APPLEJACK_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties(),
                    kbMod(0.05, UUID.fromString("a04b04c4-d444-e444-f444-000000000444")), "applejack"));

    // Rainbow Dash Armor — 每件 +0.15 移动速度（乘算）
    public static final RegistryObject<ArmorItem> RAINBOW_DASH_HELMET = ITEMS.register("rainbow_dash_helmet",
            () -> new ModArmorItem(ModArmorMaterials.DASH, ArmorItem.Type.HELMET, new Item.Properties(),
                    spdMod(0.15, UUID.fromString("b01c01d1-e111-f111-a111-000000000555")), "rainbow_dash"));
    public static final RegistryObject<ArmorItem> RAINBOW_DASH_CHESTPLATE = ITEMS.register("rainbow_dash_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.DASH, ArmorItem.Type.CHESTPLATE, new Item.Properties(),
                    spdMod(0.15, UUID.fromString("b02c02d2-e222-f222-a222-000000000666")), "rainbow_dash"));
    public static final RegistryObject<ArmorItem> RAINBOW_DASH_LEGGINGS = ITEMS.register("rainbow_dash_leggings",
            () -> new ModArmorItem(ModArmorMaterials.DASH, ArmorItem.Type.LEGGINGS, new Item.Properties(),
                    spdMod(0.15, UUID.fromString("b03c03d3-e333-f333-a333-000000000777")), "rainbow_dash"));
    public static final RegistryObject<ArmorItem> RAINBOW_DASH_BOOTS = ITEMS.register("rainbow_dash_boots",
            () -> new ModArmorItem(ModArmorMaterials.DASH, ArmorItem.Type.BOOTS, new Item.Properties(),
                    spdMod(0.15, UUID.fromString("b04c04d4-e444-f444-a444-000000000888")), "rainbow_dash"));

    // ---- 属性辅助方法 ----
    private static Multimap<net.minecraft.world.entity.ai.attributes.Attribute, AttributeModifier> kbMod(double v, UUID id) {
        Multimap<net.minecraft.world.entity.ai.attributes.Attribute, AttributeModifier> m = HashMultimap.create();
        m.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id, "kb_bonus", v, AttributeModifier.Operation.ADDITION));
        return m;
    }

    private static Multimap<net.minecraft.world.entity.ai.attributes.Attribute, AttributeModifier> spdMod(double v, UUID id) {
        Multimap<net.minecraft.world.entity.ai.attributes.Attribute, AttributeModifier> m = HashMultimap.create();
        m.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(id, "spd_bonus", v, AttributeModifier.Operation.MULTIPLY_BASE));
        return m;
    }

    // ==================== 五、食物 (4) ====================

    public static final RegistryObject<Item> MUFFIN = ITEMS.register("muffin",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(4).saturationMod(0.2F).build())));
    public static final RegistryObject<Item> CUPCAKE = ITEMS.register("cupcake",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(8).saturationMod(0.4F).build())));
    public static final RegistryObject<Item> RAINBOW = ITEMS.register("rainbow",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(1).saturationMod(0.02F).build())));
    public static final RegistryObject<Item> HARDENED_APPLE = ITEMS.register("hardened_apple",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(12).saturationMod(1.5F).build())));

    // ==================== 六、刷怪蛋 (37) ====================

    public static final RegistryObject<Item> SPAWN_EGG_ROBOT_SOMBRA = ITEMS.register("spawn_egg_robot_sombra",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_TWILIGHT_SPARKLE = ITEMS.register("spawn_egg_twilight_sparkle",
            () -> new ForgeSpawnEggItem(ModEntities.TWILIGHT_SPARKLE, 0x7b2d8e, 0xd4a520, new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_CRAGADILE = ITEMS.register("spawn_egg_cragadile",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_PHOENIX = ITEMS.register("spawn_egg_phoenix",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_CHIEF_THUNDERHOOVES = ITEMS.register("spawn_egg_chief_thunderhooves",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_BLACK_WIDOW_SPIDER = ITEMS.register("spawn_egg_black_widow_spider",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_LEVIATHAN = ITEMS.register("spawn_egg_leviathan",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_KINGBOWSER = ITEMS.register("spawn_egg_kingbowser",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_CENTIPEDE = ITEMS.register("spawn_egg_centipede",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_HYDRA = ITEMS.register("spawn_egg_hydra",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_WINDIGO = ITEMS.register("spawn_egg_windigo",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_BABY_MOOSE = ITEMS.register("spawn_egg_baby_moose",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_BUFFALO = ITEMS.register("spawn_egg_buffalo",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_LEECH = ITEMS.register("spawn_egg_leech",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_GARBLE = ITEMS.register("spawn_egg_garble",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_ADULT_MOOSE = ITEMS.register("spawn_egg_adult_moose",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_BEAR = ITEMS.register("spawn_egg_bear",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_TOUGH_GUY = ITEMS.register("spawn_egg_tough_guy",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_MAVIS = ITEMS.register("spawn_egg_mavis",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_URSA_MAJOR = ITEMS.register("spawn_egg_ursa_major",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_RAINBOW_DASH = ITEMS.register("spawn_egg_rainbow_dash",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_MANTICORE = ITEMS.register("spawn_egg_manticore",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_RAINBOW_CENTIPEDE = ITEMS.register("spawn_egg_rainbow_centipede",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_APPLEJACK = ITEMS.register("spawn_egg_applejack",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_ARCTIC_SCORPION = ITEMS.register("spawn_egg_arctic_scorpion",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_TIMBER_WOLF = ITEMS.register("spawn_egg_timber_wolf",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_PARASPRITE = ITEMS.register("spawn_egg_parasprite",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_COCKATRICE = ITEMS.register("spawn_egg_cockatrice",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_CRABZILLA = ITEMS.register("spawn_egg_crabzilla",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_IRON_WILL = ITEMS.register("spawn_egg_iron_will",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_LIGHTNING_CLOUD = ITEMS.register("spawn_egg_lightning_cloud",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_TREVOR = ITEMS.register("spawn_egg_trevor",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_SKULL_OF_DOOM = ITEMS.register("spawn_egg_skull_of_doom",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_SKULL = ITEMS.register("spawn_egg_skull",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_PRINCE_RUTHERFORD = ITEMS.register("spawn_egg_prince_rutherford",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_SPIKEZILLA = ITEMS.register("spawn_egg_spikezilla",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_EGG_RHINOCEROS = ITEMS.register("spawn_egg_rhinoceros",
            () -> new Item(new Item.Properties()));
}
