package com.shao.mythical_creatures_reborn.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;

import java.util.*;

/**
 * 通用配置编辑器的数据层 | Data layer for the generic config editor.
 * <p>
 * 与「覆盖式配置」共用唯一数据源 {@link MythicalConfig#DATA}：这里只负责
 * ① 按「分类(生物/物品/全局)」枚举可编辑对象与它们真正支持的属性；
 * ② 构建对象×属性的快照供 GUI 展示；③ 把 GUI 改动写回 {@code common.toml} 的 overrides。
 * <p>
 * 三类对象共用同一个 overrides 列表（第 4 元为可选注释），读取方法分别为
 * {@code entityAttr}（生物）/ {@code equipAttr}（物品）/ {@code get("global_params",…)}（全局）。
 * 所有改动「重启后生效」——生物在构造时读配置、物品在 ItemAttributeModifierEvent 读配置、
 * 全局在各处 fallback 读配置，因此无需实时 apply 逻辑。
 * <p>
 * 修正了参考分支的取值 bug：默认值查询必须为 {@code target + "|" + attr}，
 * 而非 {@code target + "|" + target}（后者会让未手写 override 的属性静默归零）。
 */
public final class MobStatsManager {

    /** 全局参数在 overrides 里的注册名 | The registry key used for global params */
    public static final String GLOBAL = "global_params";

    /** 可编辑对象的三大分类 | The three editable categories */
    public enum Category {
        ENTITY, ITEM, GLOBAL
    }

    /* ============================================================
     * 属性定义 | Attribute definitions per category
     * ============================================================ */

    /** 全局参数默认值 | Global param defaults（用于快照展示与未覆盖时的 fallback） */
    public static final Map<String, Double> GLOBAL_DEFAULTS = new LinkedHashMap<>();
    static {
        GLOBAL_DEFAULTS.put("follow_range", 32.0);
        GLOBAL_DEFAULTS.put("wing_flap_speed", 0.4);
        GLOBAL_DEFAULTS.put("wing_decay_speed", 0.15);
        GLOBAL_DEFAULTS.put("bleeding_base", 1.0);
        GLOBAL_DEFAULTS.put("bleeding_amp", 0.5);
        GLOBAL_DEFAULTS.put("sword_cooldown", 40.0);
        GLOBAL_DEFAULTS.put("repair_interval", 60.0);
        GLOBAL_DEFAULTS.put("repair_amount", 1.0);
        GLOBAL_DEFAULTS.put("cutie_mark_slots", 1.0);
    }

    /** 实体核心三件套（所有生物都消费：applyCoreStats / createAttributes） */
    private static final List<String> CORE_KEYS = List.of("max_health", "move_speed", "attack_damage");

    /** 骑乘/飞行调参键（仅当实体在 ENTITY_DEFAULTS 里有对应默认值时才暴露） */
    private static final List<String> TUNING_KEYS = List.of(
            "ridden_speed_factor", "vertical_up", "vertical_down", "vertical_hover",
            "horizontal_factor", "inertia_decay", "jump_height");

    /** 物品——武器/工具（TieredItem）可改属性 */
    private static final List<String> WEAPON_KEYS = List.of("attack_damage", "attack_speed", "max_damage");

    /** 物品——护甲（ArmorItem）可改属性 */
    private static final List<String> ARMOR_KEYS = List.of("armor", "armor_toughness", "armor_kb_resist", "max_damage");

    /* ============================================================
     * 候选枚举 | Candidate enumeration
     * ============================================================ */

    /** 返回某分类下所有可编辑对象的注册名 | All editable ids in a category */
    public static List<String> candidateIds(Category cat) {
        List<String> out = new ArrayList<>();
        switch (cat) {
            case ENTITY -> {
                Set<String> ids = new LinkedHashSet<>();
                for (String k : MythicalConfig.D.ENTITY_DEFAULTS.keySet()) {
                    int i = k.indexOf('|');
                    if (i > 0) ids.add(k.substring(0, i));
                }
                out.addAll(ids);
            }
            case ITEM -> {
                for (Item item : BuiltInRegistries.ITEM) {
                    ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item);
                    if (rl == null || !"mythical_creatures_reborn".equals(rl.getNamespace())) continue;
                    // 只有武器/工具(TieredItem)与护甲(ArmorItem)有配置消费点，其余物品不暴露
                    if (item instanceof TieredItem || item instanceof ArmorItem) out.add(rl.toString());
                }
                out.sort(String::compareTo);
            }
            case GLOBAL -> out.add(GLOBAL);
        }
        return out;
    }

    /** 返回某对象真正支持修改的属性键 | The attributes an object actually supports */
    public static List<String> keysOf(String target, Category cat) {
        List<String> keys = new ArrayList<>();
        switch (cat) {
            case ENTITY -> {
                keys.addAll(CORE_KEYS);
                if (MythicalConfig.D.ENTITY_DEFAULTS.containsKey(target + "|fly_speed")) keys.add("fly_speed");
                for (String k : TUNING_KEYS)
                    if (MythicalConfig.D.ENTITY_DEFAULTS.containsKey(target + "|" + k)) keys.add(k);
            }
            case ITEM -> {
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(target));
                if (item instanceof TieredItem) keys.addAll(WEAPON_KEYS);
                if (item instanceof ArmorItem) keys.addAll(ARMOR_KEYS);
            }
            case GLOBAL -> keys.addAll(GLOBAL_DEFAULTS.keySet());
        }
        return keys;
    }

    /** 某对象的默认值 | Default value（物品读取其自带属性修饰符/耐久的真实默认值） */
    public static double defaultOf(String target, String key, Category cat) {
        return switch (cat) {
            case ENTITY -> MythicalConfig.D.ENTITY_DEFAULTS.getOrDefault(target + "|" + key, 0.0);
            case GLOBAL -> GLOBAL_DEFAULTS.getOrDefault(key, 0.0);
            case ITEM -> itemDefault(target, key);
        };
    }

    /** 物品自带属性的真实默认值（供 GUI 展示「默认 X」而非笼统的「原版默认」） */
    private static double itemDefault(String target, String key) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(target));
        if (item == null) return 0.0;
        if ("max_damage".equals(key)) return item.getMaxDamage(ItemStack.EMPTY);
        // 护甲取自身槽位，武器/工具取主手槽位
        EquipmentSlot slot = (item instanceof ArmorItem armor) ? armor.getType().getSlot() : EquipmentSlot.MAINHAND;
        Attribute attr = switch (key) {
            case "attack_damage" -> Attributes.ATTACK_DAMAGE;
            case "attack_speed" -> Attributes.ATTACK_SPEED;
            case "armor" -> Attributes.ARMOR;
            case "armor_toughness" -> Attributes.ARMOR_TOUGHNESS;
            case "armor_kb_resist" -> Attributes.KNOCKBACK_RESISTANCE;
            default -> null;
        };
        if (attr == null) return 0.0;
        double sum = 0.0;
        for (AttributeModifier m : item.getDefaultAttributeModifiers(slot).get(attr)) {
            if (m.getOperation() == AttributeModifier.Operation.ADDITION) sum += m.getAmount();
        }
        // 攻击伤害/攻速的修饰符是「相对空手基准」的偏移，需还原为总值显示（与 EquipmentConfigHandler 语义一致）
        if ("attack_damage".equals(key)) return sum + 1.0;
        if ("attack_speed".equals(key)) return sum + 4.0;
        return sum;
    }

    /** 某对象的当前生效值 | Current value（override 优先，否则回退默认） */
    public static double currentOf(String target, String key, Category cat) {
        return switch (cat) {
            case ENTITY -> MythicalConfig.DATA.entityAttr(target, key);
            case GLOBAL -> MythicalConfig.DATA.get(target, key, GLOBAL_DEFAULTS.getOrDefault(key, 0.0));
            case ITEM -> MythicalConfig.DATA.equipAttr(target, key);
        };
    }

    /* ============================================================
     * 快照构建 | Snapshot building
     * ============================================================ */

    /** 全量快照：三类 × 所有候选对象 × 各自支持的属性（用于打开编辑器时下发） */
    public static List<Target> buildSnapshot() {
        List<Target> out = new ArrayList<>();
        for (Category cat : Category.values()) {
            for (String id : candidateIds(cat)) out.add(build(id, cat));
        }
        return out;
    }

    public static Target build(String target, Category cat) {
        List<Row> rows = new ArrayList<>();
        for (String key : keysOf(target, cat)) {
            double def = defaultOf(target, key, cat);
            double cur = currentOf(target, key, cat);
            boolean overridden = MythicalConfig.DATA.isOverridden(target, key);
            String comment = MythicalConfig.DATA.commentOf(target, key);
            rows.add(new Row(key, def, cur, overridden, comment));
        }
        return new Target(target, cat, rows);
    }

    /* ============================================================
     * 写回 | Persist（仅改内存；save() 才落盘 common.toml）
     * ============================================================ */

    public static void set(String target, String key, double value, String comment) {
        MythicalConfig.DATA.setOverride(target, key, value, comment);
    }

    public static void reset(String target, String key) {
        MythicalConfig.DATA.resetOverride(target, key);
    }

    public static void resetAll() {
        MythicalConfig.DATA.clearAllOverrides();
    }

    public static void save() {
        MythicalConfig.DATA.persistIfDirty();
    }

    /* ============================================================
     * 快照容器 | Snapshot holders
     * ============================================================ */

    public static final class Target {
        public final String id;
        public final Category category;
        public final List<Row> rows;
        public Target(String id, Category category, List<Row> rows) {
            this.id = id;
            this.category = category;
            this.rows = rows;
        }
        /** 是否有任意属性被覆盖（用于决定是否默认显示在列表里） */
        public boolean hasOverrides() {
            for (Row r : rows) if (r.overridden) return true;
            return false;
        }
    }

    public static final class Row {
        public final String key;
        public final double def;
        public double cur;
        public boolean overridden;
        public String comment;
        public Row(String key, double def, double cur, boolean overridden, String comment) {
            this.key = key;
            this.def = def;
            this.cur = cur;
            this.overridden = overridden;
            this.comment = comment == null ? "" : comment;
        }
    }
}
