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
import com.shao.mythicalcreatures.item.MythicalSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

/**
 * 全物品注册表（移植自 MLP Mythical Creatures 1.7.10）。
 * 含纯物品与带自定义行为的物品（武器/盔甲/可爱标志/套装等）。
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
    public static final RegistryObject<Item> BONE_BRICK_BLOCK = ITEMS.register("bone_brick_block",
            () -> new BlockItem(ModBlocks.BONE_BRICK.get(), new Item.Properties()));
    public static final RegistryObject<Item> BONE_ORE = ITEMS.register("bone_ore",
            () -> new BlockItem(ModBlocks.BONE_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> BONE_WALL = ITEMS.register("bone_wall",
            () -> new BlockItem(ModBlocks.BONE_WALL.get(), new Item.Properties()));
    public static final RegistryObject<Item> GLOWING_BONE_BLOCK = ITEMS.register("glowing_bone_block",
            () -> new BlockItem(ModBlocks.GLOWING_BONE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_ORE = ITEMS.register("crystal_ore",
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
    public static final RegistryObject<Item> AURORA_BLOCK = ITEMS.register("aurora_block",
            () -> new BlockItem(ModBlocks.AURORA.get(), new Item.Properties()));

    // ==================== 一、特殊物品 & 材料 (30) ====================

    public static final RegistryObject<Item> BONE_WAND = ITEMS.register("bone_wand",
            () -> new ManeSixItem(new Item.Properties()));
    public static final RegistryObject<Item> TWILICANE = ITEMS.register("twilicane",
            () -> new TwilicaneItem(new Item.Properties().stacksTo(1)));
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
    public static final RegistryObject<Item> PRECIOUS_GEM = ITEMS.register("precious_gem",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<ThrowableFunItem> BUTTERFLIES = ITEMS.register("butterflies",
            () -> new ThrowableFunItem(new Item.Properties(),
                    (level, shooter) -> new com.shao.mythicalcreatures.entity.ButterflyProjectileEntity(level, shooter)));
    public static final RegistryObject<AppleProjectileItem> APPLES_ITEM = ITEMS.register("apples",
            () -> new AppleProjectileItem(new Item.Properties()));
    public static final RegistryObject<ThrowableFunItem> BALLOONS = ITEMS.register("balloons",
            () -> new ThrowableFunItem(new Item.Properties(),
                    (level, shooter) -> new com.shao.mythicalcreatures.entity.BalloonProjectileEntity(level, shooter)));
    public static final RegistryObject<Item> MAVIS_ORBS = ITEMS.register("mavis_orbs",
            () -> new Item(new Item.Properties()));

    // 可爱标志
    public static final RegistryObject<CutieMarkItem> APPLEJACK_CUTIEMARK = ITEMS.register("applejack_cutiemark",
            () -> new CutieMarkItem(new Item.Properties().stacksTo(1), "tooltip.mythicalcreatures.cutiemark.applejack"));
    public static final RegistryObject<CutieMarkItem> PINKIE_PIE_CUTIEMARK = ITEMS.register("pinkie_pie_cutiemark",
            () -> new CutieMarkItem(new Item.Properties().stacksTo(1), "tooltip.mythicalcreatures.cutiemark.pinkie_pie"));
    public static final RegistryObject<CutieMarkItem> FLUTTERSHY_CUTIEMARK = ITEMS.register("fluttershy_cutiemark",
            () -> new CutieMarkItem(new Item.Properties().stacksTo(1), "tooltip.mythicalcreatures.cutiemark.fluttershy"));
    public static final RegistryObject<CutieMarkItem> RARITY_CUTIEMARK = ITEMS.register("rarity_cutiemark",
            () -> new CutieMarkItem(new Item.Properties().stacksTo(1), "tooltip.mythicalcreatures.cutiemark.rarity"));
    public static final RegistryObject<CutieMarkItem> RAINBOW_DASH_CUTIEMARK = ITEMS.register("rainbow_dash_cutiemark",
            () -> new CutieMarkItem(new Item.Properties().stacksTo(1), "tooltip.mythicalcreatures.cutiemark.rainbow_dash"));
    public static final RegistryObject<CutieMarkItem> TWILIGHT_CUTIEMARK = ITEMS.register("twilight_cutiemark",
            () -> new CutieMarkItem(new Item.Properties().stacksTo(1), "tooltip.mythicalcreatures.cutiemark.twilight"));
    public static final RegistryObject<CutieMarkItem> HOLY_LIGHT_RADIANCE_CUTIEMARK = ITEMS.register("holy_light_radiance_cutiemark",
            () -> new CutieMarkItem(new Item.Properties().stacksTo(1), "tooltip.mythicalcreatures.cutiemark.holy_light_radiance"));

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
            () -> new UnstableItemItem(new Item.Properties()));
    public static final RegistryObject<Item> CENTIPEDE_ANTENNA = ITEMS.register("centipede_antenna",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_GEM = ITEMS.register("crystal_gem",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> YAK_HORN = ITEMS.register("yak_horn",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RHINO_HORN = ITEMS.register("rhino_horn",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<TwilightMagicEggItem> TWILICORN_MAGIC_EGG = ITEMS.register("twilicorn_magic_egg",
            () -> new TwilightMagicEggItem(new Item.Properties()));
    public static final RegistryObject<Item> MAGIC_SPAWN_EGG = ITEMS.register("magic_spawn_egg",
            () -> new Item(new Item.Properties()));

    // ==================== 二、武器 (13) ====================

    public static final RegistryObject<BowserSword> BOWSERS_SWORD = ITEMS.register("bowsers_sword",
            () -> new BowserSword(ModTiers.REINFORCED, 3, -2.4F, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final RegistryObject<SwordItem> TWILIGHT_SWORD = ITEMS.register("twilight_sword",
            () -> new TwilightSword(ModTiers.TWILIGHT, 3, -2.4F, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final RegistryObject<SwordItem> EXPLOSIVE_SWORD = ITEMS.register("explosive_sword",
            () -> new SwordItem(ModTiers.EXPLOSIVE, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> APPLE_SWORD = ITEMS.register("apple_sword",
            () -> new SwordItem(ModTiers.APPLE, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> RAINBOW_DASH_SWORD = ITEMS.register("rainbow_dash_sword",
            () -> new RainbowDashSword(ModTiers.DASH, 3, -2.4F, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final RegistryObject<BearClawSword> BEAR_CLAW_SWORD = ITEMS.register("bear_claw_sword",
            () -> new BearClawSword(ModTiers.BEAR, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> CRAG_HAMMER = ITEMS.register("crag_hammer",
            () -> new SwordItem(ModTiers.CRAG, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> APPLEJACK_SWORD = ITEMS.register("applejack_sword",
            () -> new SwordItem(ModTiers.APPLEJACK, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<SwordItem> CRYSTAL_SWORD = ITEMS.register("crystal_sword",
            () -> new SwordItem(ModTiers.CRYSTAL, 3, -2.4F, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final RegistryObject<SwordItem> ALICORN_SWORD = ITEMS.register("alicorn_sword",
            () -> new SwordItem(ModTiers.ALICORN, 10, -2.4F, new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final RegistryObject<SwordItem> DAGGER = ITEMS.register("dagger",
            () -> new SwordItem(ModTiers.RANDOM2, 3, -1.0F, new Item.Properties()));
    public static final RegistryObject<UrsaClawsItem> URSA_CLAWS = ITEMS.register("ursa_claws",
            // 攻击伤害 = 空手基准1.0 + (87 + URSA Tier加成12) = 100；攻速修正 -3.65F：原版基础4.0+(-3.65)=0.35 次/秒（比原 -3.3F 的 0.7 对半砍更慢）
            () -> new UrsaClawsItem(ModTiers.URSA, 87, -3.65F,
                    new Item.Properties().fireResistant().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
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
            () -> new ModArmorItem(ModArmorMaterials.BOWSERS, ArmorItem.Type.HELMET, new Item.Properties(), "bowsers"));
    public static final RegistryObject<ArmorItem> BOWSERS_CHESTPLATE = ITEMS.register("bowsers_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.BOWSERS, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "bowsers"));
    public static final RegistryObject<ArmorItem> BOWSERS_LEGGINGS = ITEMS.register("bowsers_leggings",
            () -> new ModArmorItem(ModArmorMaterials.BOWSERS, ArmorItem.Type.LEGGINGS, new Item.Properties(), "bowsers"));
    public static final RegistryObject<ArmorItem> BOWSERS_BOOTS = ITEMS.register("bowsers_boots",
            () -> new ModArmorItem(ModArmorMaterials.BOWSERS, ArmorItem.Type.BOOTS, new Item.Properties(), "bowsers"));

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
            () -> new ModArmorItem(ModArmorMaterials.DARK_CRYSTAL, ArmorItem.Type.HELMET, new Item.Properties(), "dark_crystal"));
    public static final RegistryObject<ArmorItem> DARK_CRYSTAL_CHESTPLATE = ITEMS.register("dark_crystal_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.DARK_CRYSTAL, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "dark_crystal"));
    public static final RegistryObject<ArmorItem> DARK_CRYSTAL_LEGGINGS = ITEMS.register("dark_crystal_leggings",
            () -> new ModArmorItem(ModArmorMaterials.DARK_CRYSTAL, ArmorItem.Type.LEGGINGS, new Item.Properties(), "dark_crystal"));
    public static final RegistryObject<ArmorItem> DARK_CRYSTAL_BOOTS = ITEMS.register("dark_crystal_boots",
            () -> new ModArmorItem(ModArmorMaterials.DARK_CRYSTAL, ArmorItem.Type.BOOTS, new Item.Properties(), "dark_crystal"));

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
    public static final RegistryObject<ThrowableFunItem> CUPCAKE = ITEMS.register("cupcake",
            () -> new ThrowableFunItem(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(8).saturationMod(0.4F).build()),
                    (level, shooter) -> new com.shao.mythicalcreatures.entity.CupcakeProjectileEntity(level, shooter)));
    public static final RegistryObject<Item> RAINBOW = ITEMS.register("rainbow",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(1).saturationMod(0.02F).build())));
    public static final RegistryObject<Item> HARDENED_APPLE = ITEMS.register("hardened_apple",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(12).saturationMod(1.5F).build())));

    // ==================== 云宝工具 ====================
    public static final RegistryObject<PickaxeItem> RAINBOW_DASH_PICKAXE = ITEMS.register("rainbow_dash_pickaxe",
            () -> new PickaxeItem(ModTiers.DASH, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<AxeItem> RAINBOW_DASH_AXE = ITEMS.register("rainbow_dash_axe",
            () -> new AxeItem(ModTiers.DASH, 6, -3.1F, new Item.Properties()));
    public static final RegistryObject<ShovelItem> RAINBOW_DASH_SHOVEL = ITEMS.register("rainbow_dash_shovel",
            () -> new ShovelItem(ModTiers.DASH, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<HoeItem> RAINBOW_DASH_HOE = ITEMS.register("rainbow_dash_hoe",
            () -> new HoeItem(ModTiers.DASH, -3, 0.0F, new Item.Properties()));

    // ==================== 柔柔套 ====================
    public static final RegistryObject<SwordItem> FLUTTERSHY_SWORD = ITEMS.register("fluttershy_sword",
            () -> new SwordItem(ModTiers.FLUTTERSHY, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<PickaxeItem> FLUTTERSHY_PICKAXE = ITEMS.register("fluttershy_pickaxe",
            () -> new PickaxeItem(ModTiers.FLUTTERSHY, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<AxeItem> FLUTTERSHY_AXE = ITEMS.register("fluttershy_axe",
            () -> new AxeItem(ModTiers.FLUTTERSHY, 6, -3.1F, new Item.Properties()));
    public static final RegistryObject<ShovelItem> FLUTTERSHY_SHOVEL = ITEMS.register("fluttershy_shovel",
            () -> new ShovelItem(ModTiers.FLUTTERSHY, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<HoeItem> FLUTTERSHY_HOE = ITEMS.register("fluttershy_hoe",
            () -> new HoeItem(ModTiers.FLUTTERSHY, -3, 0.0F, new Item.Properties()));
    public static final RegistryObject<ArmorItem> FLUTTERSHY_HELMET = ITEMS.register("fluttershy_helmet",
            () -> new ModArmorItem(ModArmorMaterials.FLUTTERSHY, ArmorItem.Type.HELMET, new Item.Properties(), "fluttershy"));
    public static final RegistryObject<ArmorItem> FLUTTERSHY_CHESTPLATE = ITEMS.register("fluttershy_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.FLUTTERSHY, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "fluttershy"));
    public static final RegistryObject<ArmorItem> FLUTTERSHY_LEGGINGS = ITEMS.register("fluttershy_leggings",
            () -> new ModArmorItem(ModArmorMaterials.FLUTTERSHY, ArmorItem.Type.LEGGINGS, new Item.Properties(), "fluttershy"));
    public static final RegistryObject<ArmorItem> FLUTTERSHY_BOOTS = ITEMS.register("fluttershy_boots",
            () -> new ModArmorItem(ModArmorMaterials.FLUTTERSHY, ArmorItem.Type.BOOTS, new Item.Properties(), "fluttershy"));

    // ==================== 碧琪套 ====================
    public static final RegistryObject<SwordItem> PINKIE_PIE_SWORD = ITEMS.register("pinkie_pie_sword",
            () -> new SwordItem(ModTiers.PINKIE_PIE, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<PickaxeItem> PINKIE_PIE_PICKAXE = ITEMS.register("pinkie_pie_pickaxe",
            () -> new PickaxeItem(ModTiers.PINKIE_PIE, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<AxeItem> PINKIE_PIE_AXE = ITEMS.register("pinkie_pie_axe",
            () -> new AxeItem(ModTiers.PINKIE_PIE, 6, -3.1F, new Item.Properties()));
    public static final RegistryObject<ShovelItem> PINKIE_PIE_SHOVEL = ITEMS.register("pinkie_pie_shovel",
            () -> new ShovelItem(ModTiers.PINKIE_PIE, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<HoeItem> PINKIE_PIE_HOE = ITEMS.register("pinkie_pie_hoe",
            () -> new HoeItem(ModTiers.PINKIE_PIE, -3, 0.0F, new Item.Properties()));
    public static final RegistryObject<ArmorItem> PINKIE_PIE_HELMET = ITEMS.register("pinkie_pie_helmet",
            () -> new ArmorItem(ModArmorMaterials.PINKIE_PIE, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<ArmorItem> PINKIE_PIE_CHESTPLATE = ITEMS.register("pinkie_pie_chestplate",
            () -> new ArmorItem(ModArmorMaterials.PINKIE_PIE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<ArmorItem> PINKIE_PIE_LEGGINGS = ITEMS.register("pinkie_pie_leggings",
            () -> new ArmorItem(ModArmorMaterials.PINKIE_PIE, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<ArmorItem> PINKIE_PIE_BOOTS = ITEMS.register("pinkie_pie_boots",
            () -> new ArmorItem(ModArmorMaterials.PINKIE_PIE, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ==================== 珍奇套 ====================
    public static final RegistryObject<SwordItem> RARITY_SWORD = ITEMS.register("rarity_sword",
            () -> new SwordItem(ModTiers.RARITY, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<PickaxeItem> RARITY_PICKAXE = ITEMS.register("rarity_pickaxe",
            () -> new PickaxeItem(ModTiers.RARITY, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<AxeItem> RARITY_AXE = ITEMS.register("rarity_axe",
            () -> new AxeItem(ModTiers.RARITY, 6, -3.1F, new Item.Properties()));
    public static final RegistryObject<ShovelItem> RARITY_SHOVEL = ITEMS.register("rarity_shovel",
            () -> new ShovelItem(ModTiers.RARITY, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<HoeItem> RARITY_HOE = ITEMS.register("rarity_hoe",
            () -> new HoeItem(ModTiers.RARITY, -3, 0.0F, new Item.Properties()));
    public static final RegistryObject<ArmorItem> RARITY_HELMET = ITEMS.register("rarity_helmet",
            () -> new ArmorItem(ModArmorMaterials.RARITY_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<ArmorItem> RARITY_CHESTPLATE = ITEMS.register("rarity_chestplate",
            () -> new ArmorItem(ModArmorMaterials.RARITY_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<ArmorItem> RARITY_LEGGINGS = ITEMS.register("rarity_leggings",
            () -> new ArmorItem(ModArmorMaterials.RARITY_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<ArmorItem> RARITY_BOOTS = ITEMS.register("rarity_boots",
            () -> new ArmorItem(ModArmorMaterials.RARITY_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ==================== 圣光套 ====================
    public static final RegistryObject<SwordItem> HOLY_LIGHT_RADIANCE_SWORD = ITEMS.register("holy_light_radiance_sword",
            () -> new SwordItem(ModTiers.HOLY_LIGHT, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<PickaxeItem> HOLY_LIGHT_RADIANCE_PICKAXE = ITEMS.register("holy_light_radiance_pickaxe",
            () -> new PickaxeItem(ModTiers.HOLY_LIGHT, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<AxeItem> HOLY_LIGHT_RADIANCE_AXE = ITEMS.register("holy_light_radiance_axe",
            () -> new AxeItem(ModTiers.HOLY_LIGHT, 6, -3.1F, new Item.Properties()));
    public static final RegistryObject<ShovelItem> HOLY_LIGHT_RADIANCE_SHOVEL = ITEMS.register("holy_light_radiance_shovel",
            () -> new ShovelItem(ModTiers.HOLY_LIGHT, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<HoeItem> HOLY_LIGHT_RADIANCE_HOE = ITEMS.register("holy_light_radiance_hoe",
            () -> new HoeItem(ModTiers.HOLY_LIGHT, -3, 0.0F, new Item.Properties()));
    public static final RegistryObject<ArmorItem> HOLY_LIGHT_RADIANCE_HELMET = ITEMS.register("holy_light_radiance_helmet",
            () -> new ArmorItem(ModArmorMaterials.HOLY_LIGHT, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<ArmorItem> HOLY_LIGHT_RADIANCE_CHESTPLATE = ITEMS.register("holy_light_radiance_chestplate",
            () -> new ArmorItem(ModArmorMaterials.HOLY_LIGHT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<ArmorItem> HOLY_LIGHT_RADIANCE_LEGGINGS = ITEMS.register("holy_light_radiance_leggings",
            () -> new ArmorItem(ModArmorMaterials.HOLY_LIGHT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<ArmorItem> HOLY_LIGHT_RADIANCE_BOOTS = ITEMS.register("holy_light_radiance_boots",
            () -> new ArmorItem(ModArmorMaterials.HOLY_LIGHT, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ==================== 六、刷怪蛋 (37) ====================

    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_ROBOT_SOMBRA = ITEMS.register("spawn_egg_robot_sombra", () -> new MythicalSpawnEggItem(ModEntities.ROBOT_SOMBRA, 0x808080, 0xFF0000, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_TWILIGHT_SPARKLE = ITEMS.register("spawn_egg_twilight_sparkle",
            () -> new ForgeSpawnEggItem(ModEntities.TWILIGHT_SPARKLE, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_CRAGADILE = ITEMS.register("spawn_egg_cragadile", () -> new MythicalSpawnEggItem(ModEntities.CRAGADILE, 0x2E8B57, 0x006400, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_PHOENIX = ITEMS.register("spawn_egg_phoenix",
            () -> new MythicalSpawnEggItem(ModEntities.PHOENIX, 0xFF6633, 0xFFAA00, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_CHIEF_THUNDERHOOVES = ITEMS.register("spawn_egg_chief_thunderhooves", () -> new ForgeSpawnEggItem(ModEntities.CHIEF_THUNDERHOOVES, 0x8B5A2B, 0x3E1F0D, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_BLACK_WIDOW_SPIDER = ITEMS.register("spawn_egg_black_widow_spider", () -> new MythicalSpawnEggItem(ModEntities.BLACK_WIDOW_SPIDER, 0x111111, 0xFF0000, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_LEVIATHAN = ITEMS.register("spawn_egg_leviathan", () -> new MythicalSpawnEggItem(ModEntities.LEVIATHAN, 0x2A4D5E, 0x1A2E3A, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_KINGBOWSER_9000 = ITEMS.register("spawn_egg_kingbowser_9000",
            () -> new MythicalSpawnEggItem(ModEntities.KINGBOWSER_9000, 0x44AA44, 0x228822, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_CENTIPEDE = ITEMS.register("spawn_egg_centipede", () -> new MythicalSpawnEggItem(ModEntities.CENTIPEDE, 0x8B4513, 0xDAA520, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_HYDRA = ITEMS.register("spawn_egg_hydra", () -> new MythicalSpawnEggItem(ModEntities.HYDRA, 0x2E8B57, 0x006400, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_WINDIGO = ITEMS.register("spawn_egg_windigo", () -> new MythicalSpawnEggItem(ModEntities.WINDIGO, 0xE0FFFF, 0xFFFFFF, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_BABY_MOOSE = ITEMS.register("spawn_egg_baby_moose", () -> new MythicalSpawnEggItem(ModEntities.BABY_MOOSE, 0x8B5A2B, 0x5C3A1E, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_BUFFALO = ITEMS.register("spawn_egg_buffalo", () -> new MythicalSpawnEggItem(ModEntities.BUFFALO, 0x8B5A2B, 0x5C3A1E, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_ADULT_MOOSE = ITEMS.register("spawn_egg_adult_moose", () -> new MythicalSpawnEggItem(ModEntities.ADULT_MOOSE, 0x6B4226, 0x4A2E1A, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_BEAR = ITEMS.register("spawn_egg_bear",
            () -> new MythicalSpawnEggItem(ModEntities.BEAR, 0x8B4513, 0x3E1F0D, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_TOUGH_GUY = ITEMS.register("spawn_egg_tough_guy", () -> new MythicalSpawnEggItem(ModEntities.TOUGH_GUY, 0xB22222, 0x8B0000, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_MAVIS = ITEMS.register("spawn_egg_mavis", () -> new MythicalSpawnEggItem(ModEntities.MAVIS, 0x800080, 0x4B0082, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_URSA_MAJOR = ITEMS.register("spawn_egg_ursa_major",
            () -> new MythicalSpawnEggItem(ModEntities.URSA_MAJOR, 0x222244, 0x4444AA, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_RAINBOW_DASH = ITEMS.register("spawn_egg_rainbow_dash",
            () -> new MythicalSpawnEggItem(ModEntities.RAINBOW_DASH, 0x00CCFF, 0xFF6600, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_MANTICORE = ITEMS.register("spawn_egg_manticore", () -> new MythicalSpawnEggItem(ModEntities.MANTICORE, 0xCD5C5C, 0x8B0000, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_RAINBOW_CENTIPEDE = ITEMS.register("spawn_egg_rainbow_centipede", () -> new MythicalSpawnEggItem(ModEntities.RAINBOW_CENTIPEDE, 0xFF69B4, 0x9400D3, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_APPLEJACK = ITEMS.register("spawn_egg_applejack",
            () -> new MythicalSpawnEggItem(ModEntities.APPLEJACK, 0xFF9900, 0xFFFF00, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_ARCTIC_SCORPION = ITEMS.register("spawn_egg_arctic_scorpion", () -> new MythicalSpawnEggItem(ModEntities.ARCTIC_SCORPION, 0xADD8E6, 0x4682B4, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_TIMBER_WOLF = ITEMS.register("spawn_egg_timber_wolf", () -> new MythicalSpawnEggItem(ModEntities.TIMBER_WOLF, 0x8B7355, 0x5C4033, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_PARASPRITE = ITEMS.register("spawn_egg_parasprite",
            () -> new MythicalSpawnEggItem(ModEntities.PARASPRITE, 0xFF88FF, 0xCC44CC, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_COCKATRICE = ITEMS.register("spawn_egg_cockatrice",
            () -> new MythicalSpawnEggItem(ModEntities.COCKATRICE, 0xCC6633, 0x663311, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_CRABZILLA = ITEMS.register("spawn_egg_crabzilla", () -> new MythicalSpawnEggItem(ModEntities.CRABZILLA, 0xDC143C, 0x8B0000, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_IRON_WILL = ITEMS.register("spawn_egg_iron_will", () -> new MythicalSpawnEggItem(ModEntities.IRON_WILL, 0xA9A9A9, 0x696969, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_SKULL_OF_DOOM = ITEMS.register("spawn_egg_skull_of_doom", () -> new MythicalSpawnEggItem(ModEntities.SKULL_OF_DOOM, 0xEEEEEE, 0x888888, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_PRINCE_RUTHERFORD = ITEMS.register("spawn_egg_prince_rutherford", () -> new MythicalSpawnEggItem(ModEntities.PRINCE_RUTHERFORD, 0x8B5A2B, 0x3E1F0D, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_SPIKEZILLA = ITEMS.register("spawn_egg_spikezilla", () -> new MythicalSpawnEggItem(ModEntities.SPIKEZILLA, 0x556B2F, 0x6B8E23, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_RHINOCEROS = ITEMS.register("spawn_egg_rhinoceros", () -> new MythicalSpawnEggItem(ModEntities.RHINOCEROS, 0x808080, 0x696969, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_GARBLE = ITEMS.register("spawn_egg_garble",
            () -> new ForgeSpawnEggItem(ModEntities.GARBLE, 0xFF2200, 0xFFAA00, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_FLUTTERSHY = ITEMS.register("spawn_egg_fluttershy",
            () -> new ForgeSpawnEggItem(ModEntities.FLUTTERSHY, 0xFF88CC, 0xFFCCDD, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_HOLY_LIGHT_RADIANCE = ITEMS.register("spawn_egg_holy_light_radiance",
            () -> new ForgeSpawnEggItem(ModEntities.HOLY_LIGHT_RADIANCE, 0xCCCCFF, 0x4488FF, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_PINKIE_PIE = ITEMS.register("spawn_egg_pinkie_pie",
            () -> new ForgeSpawnEggItem(ModEntities.PINKIE_PIE, 0xFF69B4, 0xFFAACC, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> SPAWN_EGG_RARITY = ITEMS.register("spawn_egg_rarity",
            () -> new ForgeSpawnEggItem(ModEntities.RARITY, 0x9966FF, 0xCCAAFF, new Item.Properties()));
}
