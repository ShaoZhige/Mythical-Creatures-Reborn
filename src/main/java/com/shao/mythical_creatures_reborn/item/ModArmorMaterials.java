package com.shao.mythical_creatures_reborn.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Custom armor materials ported from MLP Mythical Creatures 1.7.10.
 * <p>
 * 11 套材质的差异只有 7 个标量 + 两组四元数组，因此统一由 {@link #material} 构造，
 * 不再各写一份匿名类（原来 11 × 25 行）。
 * <p>
 * 耐久 / 护甲值数组下标与 {@link ArmorItem.Type} 的枚举顺序一一对应：
 * {@code [0]=HELMET, [1]=CHESTPLATE, [2]=LEGGINGS, [3]=BOOTS}（静态块会校验该顺序）。
 */
public class ModArmorMaterials {

    public static final ArmorMaterial BOWSERS =
            material("mythical_creatures_reborn:bowsers", SoundEvents.ARMOR_EQUIP_IRON, 18, 2.0F, 0.0F,
                    new int[]{2275, 3310, 3100, 2690}, new int[]{3, 8, 6, 3});

    public static final ArmorMaterial TWILIGHT =
            material("mythical_creatures_reborn:twilight", SoundEvents.ARMOR_EQUIP_DIAMOND, 25, 3.0F, 0.0F,
                    new int[]{1775, 2580, 2420, 2100}, new int[]{4, 9, 7, 4});

    public static final ArmorMaterial DASH =
            material("mythical_creatures_reborn:dash", SoundEvents.ARMOR_EQUIP_DIAMOND, 25, 0.0F, 0.0F,
                    new int[]{363, 528, 495, 429}, new int[]{2, 7, 5, 2});

    public static final ArmorMaterial DARK_CRYSTAL =
            material("mythical_creatures_reborn:dark_crystal", SoundEvents.ARMOR_EQUIP_NETHERITE, 10, 3.0F, 0.1F,
                    new int[]{6245, 9085, 8510, 7390}, new int[]{3, 8, 6, 3});

    public static final ArmorMaterial APPLE_ARMOR =
            material("mythical_creatures_reborn:apple", SoundEvents.ARMOR_EQUIP_CHAIN, 10, 0.0F, 0.0F,
                    new int[]{110, 160, 150, 130}, new int[]{2, 4, 3, 2});

    public static final ArmorMaterial BEAR_ARMOR =
            material("mythical_creatures_reborn:bear", SoundEvents.ARMOR_EQUIP_LEATHER, 12, 0.0F, 0.0F,
                    new int[]{110, 160, 150, 130}, new int[]{2, 5, 4, 2});

    public static final ArmorMaterial APPLEJACK_ARMOR =
            material("mythical_creatures_reborn:applejack", SoundEvents.ARMOR_EQUIP_DIAMOND, 8, 2.0F, 0.0F,
                    new int[]{2250, 3275, 3070, 2665}, new int[]{4, 9, 7, 4});

    public static final ArmorMaterial FLUTTERSHY =
            material("mythical_creatures_reborn:fluttershy", SoundEvents.ARMOR_EQUIP_DIAMOND, 22, 1.5F, 0.0F,
                    new int[]{330, 480, 450, 390}, new int[]{3, 7, 5, 3});

    public static final ArmorMaterial PINKIE_PIE =
            material("mythical_creatures_reborn:pinkie_pie", SoundEvents.ARMOR_EQUIP_DIAMOND, 20, 1.5F, 0.0F,
                    new int[]{300, 440, 410, 350}, new int[]{3, 6, 5, 3});

    public static final ArmorMaterial RARITY_ARMOR =
            material("mythical_creatures_reborn:rarity", SoundEvents.ARMOR_EQUIP_DIAMOND, 25, 2.0F, 0.0F,
                    new int[]{360, 520, 490, 420}, new int[]{3, 8, 6, 3});

    public static final ArmorMaterial HOLY_LIGHT =
            material("mythical_creatures_reborn:holy_light_radiance", SoundEvents.ARMOR_EQUIP_DIAMOND, 25, 2.5F, 0.0F,
                    new int[]{380, 550, 520, 450}, new int[]{4, 8, 6, 4});

    static {
        ArmorItem.Type[] t = ArmorItem.Type.values();
        if (t.length != 4 || t[0] != ArmorItem.Type.HELMET || t[1] != ArmorItem.Type.CHESTPLATE
                || t[2] != ArmorItem.Type.LEGGINGS || t[3] != ArmorItem.Type.BOOTS) {
            throw new IllegalStateException(
                    "ArmorItem.Type 的枚举顺序已变化，ModArmorMaterials 里 durability/defense 数组的下标需同步调整");
        }
    }

    /**
     * 构造一套护甲材质。
     *
     * @param name       材质名（同时决定盔甲贴图路径，如 {@code mythical_creatures_reborn:bowsers_layer_1.png}）
     * @param equipSound 穿戴音效
     * @param enchant    附魔能力
     * @param toughness  盔甲韧性
     * @param knockback  击退抗性
     * @param durability 耐久，下标 [HELMET, CHESTPLATE, LEGGINGS, BOOTS]
     * @param defense    护甲值，下标同上
     */
    private static ArmorMaterial material(String name, SoundEvent equipSound, int enchant,
                                          float toughness, float knockback,
                                          int[] durability, int[] defense) {
        return new ArmorMaterial() {
            @Override public int getDurabilityForType(ArmorItem.Type type) { return durability[type.ordinal()]; }
            @Override public int getDefenseForType(ArmorItem.Type type) { return defense[type.ordinal()]; }
            @Override public int getEnchantmentValue() { return enchant; }
            @Override public SoundEvent getEquipSound() { return equipSound; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
            @Override public String getName() { return name; }
            @Override public float getToughness() { return toughness; }
            @Override public float getKnockbackResistance() { return knockback; }
        };
    }
}
