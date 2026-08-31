package com.shao.mythical_creatures_reborn.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Custom armor materials ported from MLP Mythical Creatures 1.7.10.
 */
public class ModArmorMaterials {

    public static final ArmorMaterial BOWSERS = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 2275;
                case CHESTPLATE -> 3310;
                case LEGGINGS -> 3100;
                case BOOTS -> 2690;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 3;
                case CHESTPLATE -> 8;
                case LEGGINGS -> 6;
                case BOOTS -> 3;
            };
        }
        @Override public int getEnchantmentValue() { return 18; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_IRON; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "mythical_creatures_reborn:bowsers"; }
        @Override public float getToughness() { return 2.0F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public static final ArmorMaterial TWILIGHT = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 1775;
                case CHESTPLATE -> 2580;
                case LEGGINGS -> 2420;
                case BOOTS -> 2100;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 4;
                case CHESTPLATE -> 9;
                case LEGGINGS -> 7;
                case BOOTS -> 4;
            };
        }
        @Override public int getEnchantmentValue() { return 25; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_DIAMOND; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "mythical_creatures_reborn:twilight"; }
        @Override public float getToughness() { return 3.0F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public static final ArmorMaterial DASH = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 363;
                case CHESTPLATE -> 528;
                case LEGGINGS -> 495;
                case BOOTS -> 429;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 2;
                case CHESTPLATE -> 7;
                case LEGGINGS -> 5;
                case BOOTS -> 2;
            };
        }
        @Override public int getEnchantmentValue() { return 25; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_DIAMOND; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "mythical_creatures_reborn:dash"; }
        @Override public float getToughness() { return 0.0F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public static final ArmorMaterial DARK_CRYSTAL = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 6245;
                case CHESTPLATE -> 9085;
                case LEGGINGS -> 8510;
                case BOOTS -> 7390;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 3;
                case CHESTPLATE -> 8;
                case LEGGINGS -> 6;
                case BOOTS -> 3;
            };
        }
        @Override public int getEnchantmentValue() { return 10; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_NETHERITE; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "mythical_creatures_reborn:dark_crystal"; }
        @Override public float getToughness() { return 3.0F; }
        @Override public float getKnockbackResistance() { return 0.1F; }
    };

    public static final ArmorMaterial APPLE_ARMOR = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 110;
                case CHESTPLATE -> 160;
                case LEGGINGS -> 150;
                case BOOTS -> 130;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 2;
                case CHESTPLATE -> 4;
                case LEGGINGS -> 3;
                case BOOTS -> 2;
            };
        }
        @Override public int getEnchantmentValue() { return 10; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_CHAIN; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "mythical_creatures_reborn:apple"; }
        @Override public float getToughness() { return 0.0F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public static final ArmorMaterial BEAR_ARMOR = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 110;
                case CHESTPLATE -> 160;
                case LEGGINGS -> 150;
                case BOOTS -> 130;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 2;
                case CHESTPLATE -> 5;
                case LEGGINGS -> 4;
                case BOOTS -> 2;
            };
        }
        @Override public int getEnchantmentValue() { return 12; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_LEATHER; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "mythical_creatures_reborn:bear"; }
        @Override public float getToughness() { return 0.0F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public static final ArmorMaterial APPLEJACK_ARMOR = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 2250;
                case CHESTPLATE -> 3275;
                case LEGGINGS -> 3070;
                case BOOTS -> 2665;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 4;
                case CHESTPLATE -> 9;
                case LEGGINGS -> 7;
                case BOOTS -> 4;
            };
        }
        @Override public int getEnchantmentValue() { return 8; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_DIAMOND; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "mythical_creatures_reborn:applejack"; }
        @Override public float getToughness() { return 2.0F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public static final ArmorMaterial FLUTTERSHY = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 330; case CHESTPLATE -> 480;
                case LEGGINGS -> 450; case BOOTS -> 390;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 3; case CHESTPLATE -> 7;
                case LEGGINGS -> 5; case BOOTS -> 3;
            };
        }
        @Override public int getEnchantmentValue() { return 22; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_DIAMOND; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "mythical_creatures_reborn:fluttershy"; }
        @Override public float getToughness() { return 1.5F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public static final ArmorMaterial PINKIE_PIE = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 300; case CHESTPLATE -> 440;
                case LEGGINGS -> 410; case BOOTS -> 350;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 3; case CHESTPLATE -> 6;
                case LEGGINGS -> 5; case BOOTS -> 3;
            };
        }
        @Override public int getEnchantmentValue() { return 20; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_DIAMOND; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "mythical_creatures_reborn:pinkie_pie"; }
        @Override public float getToughness() { return 1.5F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public static final ArmorMaterial RARITY_ARMOR = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 360; case CHESTPLATE -> 520;
                case LEGGINGS -> 490; case BOOTS -> 420;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 3; case CHESTPLATE -> 8;
                case LEGGINGS -> 6; case BOOTS -> 3;
            };
        }
        @Override public int getEnchantmentValue() { return 25; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_DIAMOND; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "mythical_creatures_reborn:rarity"; }
        @Override public float getToughness() { return 2.0F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public static final ArmorMaterial HOLY_LIGHT = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 380; case CHESTPLATE -> 550;
                case LEGGINGS -> 520; case BOOTS -> 450;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 4; case CHESTPLATE -> 8;
                case LEGGINGS -> 6; case BOOTS -> 4;
            };
        }
        @Override public int getEnchantmentValue() { return 25; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_DIAMOND; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "mythical_creatures_reborn:holy_light_radiance"; }
        @Override public float getToughness() { return 2.5F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };
}
