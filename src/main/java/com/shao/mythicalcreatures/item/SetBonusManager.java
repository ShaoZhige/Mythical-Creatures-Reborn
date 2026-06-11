package com.shao.mythicalcreatures.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

/**
 * 套装效果管理器 — 穿齐四件套自动获得属性加成，脱掉移除。
 *
 * 使用方式：
 * {@code SetBonusManager.registerSet("applejack", "苹果嘉儿套装", ModItems.APPLEJACK_HELMET, ModItems.APPLEJACK_CHESTPLATE,
 *      ModItems.APPLEJACK_LEGGINGS, ModItems.APPLEJACK_BOOTS, SetBonusManager.bonuses()
 *          .maxHealth(30.0).knockbackResistance(0.1).build());}
 */
public class SetBonusManager {

    /** 单个属性加成 */
    public static class AttributeBonus {
        final UUID uuid;
        final Attribute attribute;
        final double amount;
        final AttributeModifier.Operation operation;
        final String name;

        AttributeBonus(String setName, Attribute attribute, double amount, AttributeModifier.Operation op) {
            this.attribute = attribute;
            this.amount = amount;
            this.operation = op;
            this.name = setName + "_bonus";
            this.uuid = UUID.nameUUIDFromBytes((setName + "_" + attribute.getDescriptionId()).getBytes());
        }
    }

    /** 套装特殊能力回调 — 每tick检查 */
    @FunctionalInterface
    public interface SetAbility {
        /** @param player 玩家
         *  @param wearing 当前是否穿戴全套 */
        void onTick(Player player, boolean wearing);
    }

    /** 一套装备的定义 */
    public static class SetDef {
        final String id;
        final String displayName;
        final Item helmet;
        final Item chestplate;
        final Item leggings;
        final Item boots;
        final List<AttributeBonus> bonuses;
        final SetAbility ability;

        SetDef(String id, String displayName, Item h, Item c, Item l, Item b,
               List<AttributeBonus> bonuses, SetAbility ability) {
            this.id = id;
            this.displayName = displayName;
            this.helmet = h;
            this.chestplate = c;
            this.leggings = l;
            this.boots = b;
            this.bonuses = bonuses;
            this.ability = ability;
        }
    }

    // ---- Builder for bonuses ----

    public static BonusBuilder bonuses() {
        return new BonusBuilder();
    }

    public static class BonusBuilder {
        private String id;
        private final List<AttributeBonus> list = new ArrayList<>();

        public BonusBuilder() {}

        /** 必须调用！传入套装唯一ID */
        public BonusBuilder id(String setId) {
            this.id = setId;
            return this;
        }

        /** +N 最大生命值 (1点 = 半心, 玩家默认20点=10心) */
        public BonusBuilder maxHealth(double amount) {
            list.add(new AttributeBonus(id, net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH,
                    amount, AttributeModifier.Operation.ADDITION));
            return this;
        }

        /** +N 击退抗性 (0~1, 1=完全免疫) */
        public BonusBuilder knockbackResistance(double amount) {
            list.add(new AttributeBonus(id, net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE,
                    amount, AttributeModifier.Operation.ADDITION));
            return this;
        }

        /** +N 盔甲韧性 */
        public BonusBuilder armorToughness(double amount) {
            list.add(new AttributeBonus(id, net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS,
                    amount, AttributeModifier.Operation.ADDITION));
            return this;
        }

        /** +N 护甲值 */
        public BonusBuilder armor(double amount) {
            list.add(new AttributeBonus(id, net.minecraft.world.entity.ai.attributes.Attributes.ARMOR,
                    amount, AttributeModifier.Operation.ADDITION));
            return this;
        }

        /** +N 移动速度 (默认0.1, 0.2=快一倍) */
        public BonusBuilder movementSpeed(double amount) {
            list.add(new AttributeBonus(id, net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED,
                    amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
            return this;
        }

        /** +N 攻击力 */
        public BonusBuilder attackDamage(double amount) {
            list.add(new AttributeBonus(id, net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                    amount, AttributeModifier.Operation.ADDITION));
            return this;
        }

        public List<AttributeBonus> build() {
            return new ArrayList<>(list);
        }
    }

    // ---- 注册 & 查询 ----

    private static final List<SetDef> SETS = new ArrayList<>();

    /**
     * 在所有注册完成后调用，统一注册所有套装效果。
     * 被 {@code MythicalCreaturesMod.commonSetup} 调用。
     */
    public static void registerAllSets() {
        // === 苹果嘉儿套装 — 全套伤害吸收 I ===
        registerSet("applejack", "苹果嘉儿套装", ModItems.APPLEJACK_HELMET, ModItems.APPLEJACK_CHESTPLATE,
                ModItems.APPLEJACK_LEGGINGS, ModItems.APPLEJACK_BOOTS,
                bonuses().id("applejack").build(),
                (player, wearing) -> {
                    if (wearing) {
                        if (!player.hasEffect(net.minecraft.world.effect.MobEffects.ABSORPTION)) {
                            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                    net.minecraft.world.effect.MobEffects.ABSORPTION,
                                    -1, 0, false, false, true));
                        }
                    } else {
                        player.removeEffect(net.minecraft.world.effect.MobEffects.ABSORPTION);
                    }
                });

        // === 紫悦套装 — 全套 +20 最大生命 ===
        registerSet("twilight", "紫悦套装", ModItems.TWILIGHT_HELMET, ModItems.TWILIGHT_CHESTPLATE,
                ModItems.TWILIGHT_LEGGINGS, ModItems.TWILIGHT_BOOTS,
                bonuses().id("twilight").maxHealth(20.0).build(), null);

        // === 库巴套装 — 全套 +15 最大生命 + 防火 ===
        registerSet("bowsers", "库巴套装", ModItems.BOWSERS_HELMET, ModItems.BOWSERS_CHESTPLATE,
                ModItems.BOWSERS_LEGGINGS, ModItems.BOWSERS_BOOTS,
                bonuses().id("bowsers").maxHealth(15.0).build(),
                (player, wearing) -> {
                    if (wearing) {
                        if (!player.hasEffect(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE)) {
                            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                    net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE,
                                    -1, 0, false, false, true));
                        }
                    } else {
                        player.removeEffect(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE);
                    }
                });

        // === 熊皮套装 — 全套 +4 攻击伤害 ===
        registerSet("bear_fur", "熊皮套装", ModItems.BEAR_FUR_HELMET, ModItems.BEAR_FUR_CHESTPLATE,
                ModItems.BEAR_FUR_LEGGINGS, ModItems.BEAR_FUR_BOOTS,
                bonuses().id("bear_fur").attackDamage(4.0).build(), null);

        // === 云宝套装 — 飞行 + 免疫摔落（摔落在 ModEvents 处理） ===
        registerSet("rainbow_dash", "云宝套装", ModItems.RAINBOW_DASH_HELMET, ModItems.RAINBOW_DASH_CHESTPLATE,
                ModItems.RAINBOW_DASH_LEGGINGS, ModItems.RAINBOW_DASH_BOOTS,
                bonuses().id("rainbow_dash").build(),
                (player, wearing) -> {
                    if (wearing) {
                        player.getAbilities().mayfly = true;
                    } else {
                        if (!player.isCreative() && !player.isSpectator()) {
                            player.getAbilities().mayfly = false;
                            player.getAbilities().flying = false;
                        }
                    }
                    player.onUpdateAbilities();
                });
    }

    /**
     * 注册一个套装效果。
     * @param id  唯一ID，如 "applejack"
     * @param displayName 显示名，如 "苹果嘉儿套装"
     * @param helmet 头盔的 RegistryObject
     * @param chestplate 胸甲的 RegistryObject
     * @param leggings 护腿的 RegistryObject
     * @param boots 靴子的 RegistryObject
     * @param bonuses 加成列表，用 {@link #bonuses()} 构建
     */
    public static SetDef registerSet(String id, String displayName,
                                      RegistryObject<? extends Item> helmet,
                                      RegistryObject<? extends Item> chestplate,
                                      RegistryObject<? extends Item> leggings,
                                      RegistryObject<? extends Item> boots,
                                      List<AttributeBonus> bonuses,
                                      SetAbility ability) {
        SetDef set = new SetDef(id, displayName,
                helmet.get(), chestplate.get(), leggings.get(), boots.get(), bonuses, ability);
        SETS.add(set);
        return set;
    }

    /**
     * 检查玩家身上所有已注册套装的加成状态。
     * 在 LivingEquipmentChangeEvent 和 PlayerTickEvent 中调用。
     */
    public static void checkAllSets(Player player) {
        if (player.level().isClientSide) return;
        for (SetDef set : SETS) {
            checkSet(player, set);
        }
    }

    private static void checkSet(Player player, SetDef set) {
        boolean wearing = isWearingSet(player, set);
        for (AttributeBonus bonus : set.bonuses) {
            AttributeInstance attr = player.getAttribute(bonus.attribute);
            if (attr == null) continue;
            boolean has = attr.getModifier(bonus.uuid) != null;
            if (wearing && !has) {
                attr.addPermanentModifier(new AttributeModifier(
                        bonus.uuid, bonus.name, bonus.amount, bonus.operation));
            } else if (!wearing && has) {
                attr.removeModifier(bonus.uuid);
            }
        }
        // 特殊能力（如飞行）
        if (set.ability != null) {
            set.ability.onTick(player, wearing);
        }
        // 脱下后截断超出血量
        if (!wearing && player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    /** 检查玩家是否穿着指定套装的任意一件（用于免摔伤等） */
    public static boolean isWearingFullSet(Player player, String setId) {
        for (SetDef set : SETS) {
            if (set.id.equals(setId)) return isWearingSet(player, set);
        }
        return false;
    }

    private static boolean isWearingSet(Player player, SetDef set) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(set.helmet)
                && player.getItemBySlot(EquipmentSlot.CHEST).is(set.chestplate)
                && player.getItemBySlot(EquipmentSlot.LEGS).is(set.leggings)
                && player.getItemBySlot(EquipmentSlot.FEET).is(set.boots);
    }
}
