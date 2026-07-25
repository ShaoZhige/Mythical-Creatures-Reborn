package com.shao.mythicalcreatures.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * 极简覆盖式配置 | Minimal override-based config。
 * <p>
 * <b>此文件仅在进入世界时加载一次。| Loaded once on world join.</b>
 * 默认所有属性使用代码内置值，配置文件里什么都不用改。| All defaults are in-code; config file can stay empty.
 * 只需要在 overrides 列表里填入你想改的项即可。| Only add entries you want to override.
 * </p>
 */
public class MythicalConfig {

    public static final ForgeConfigSpec SPEC;
    public static final Data DATA;

    static {
        Pair<Data, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Data::new);
        SPEC = pair.getRight();
        DATA = pair.getLeft();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "mythicalcreatures/common.toml");
    }

    /* ================================================================
     * 默认值（仅当 overrides 未覆盖时使用）
     * ================================================================ */

    public static final class D {
        // 实体 taming 物品
        public static final String RD_TAMING = "mythicalcreatures:rainbow_dash_cutiemark";
        public static final String TS_TAMING = "mythicalcreatures:twilight_cutiemark";
        public static final String AJ_TAMING = "mythicalcreatures:applejack_cutiemark";
        public static final String FS_TAMING = "minecraft:apple";
        public static final String PP_TAMING = "minecraft:apple";
        public static final String RY_TAMING = "minecraft:apple";
        public static final String HL_TAMING = "minecraft:apple";

        // 紫悦之杖召唤列表
        public static final List<String> TWILICANE_SPAWN = List.of(
            "mythicalcreatures:twilight_sparkle", "mythicalcreatures:rainbow_dash",
            "mythicalcreatures:applejack", "mythicalcreatures:bear",
            "mythicalcreatures:cockatrice", "mythicalcreatures:garble",
            "mythicalcreatures:kingbowser_9000", "mythicalcreatures:parasprite",
            "mythicalcreatures:phoenix", "mythicalcreatures:ursa_major",
            "mythicalcreatures:fluttershy", "mythicalcreatures:holy_light_radiance",
            "mythicalcreatures:pinkie_pie", "mythicalcreatures:rarity"
        );

        // 默认实体属性 (用于没有覆盖时)
        static final Map<String, Double> ENTITY_DEFAULTS = new HashMap<>();
        static void entity(String id, double hp, double spd, double dmg) {
            ENTITY_DEFAULTS.put(id + "|max_health", hp);
            ENTITY_DEFAULTS.put(id + "|move_speed", spd);
            ENTITY_DEFAULTS.put(id + "|attack_damage", dmg);
        }

        static {
            entity("mythicalcreatures:rainbow_dash",      40.0, 0.35, 5.0);
            entity("mythicalcreatures:twilight_sparkle",  40.0, 0.30, 4.0);
            entity("mythicalcreatures:applejack",         50.0, 0.40, 6.0);
            entity("mythicalcreatures:fluttershy",        30.0, 0.25, 4.0);
            ENTITY_DEFAULTS.put("mythicalcreatures:fluttershy|fly_speed", 0.6);
            entity("mythicalcreatures:pinkie_pie",        30.0, 0.25, 4.0);
            entity("mythicalcreatures:rarity",            30.0, 0.25, 4.0);
            entity("mythicalcreatures:holy_light_radiance",30.0, 0.25, 4.0);
            entity("mythicalcreatures:bear",              40.0, 0.25, 6.0);
            entity("mythicalcreatures:cockatrice",        40.0, 0.25, 6.0);
            entity("mythicalcreatures:garble",            50.0, 0.28, 8.0);
            entity("mythicalcreatures:kingbowser_9000",   40.0, 0.25, 6.0);
            entity("mythicalcreatures:parasprite",        40.0, 0.25, 6.0);
            entity("mythicalcreatures:phoenix",           40.0, 0.25, 6.0);
            entity("mythicalcreatures:ursa_major",        40.0, 0.25, 6.0);
            entity("mythicalcreatures:twilight_magic",    20.0, 0.50, 0.0);
            ENTITY_DEFAULTS.put("mythicalcreatures:twilight_magic|fly_speed", 0.55);
            ENTITY_DEFAULTS.put("mythicalcreatures:rainbow_dash|fly_speed", 1.0);
            ENTITY_DEFAULTS.put("mythicalcreatures:twilight_sparkle|fly_speed", 0.8);
        }
    }

    /* ================================================================
     * 配置字段 —— 只有一个 overrides 列表
     * ================================================================ */

    public static class Data {
        @SuppressWarnings("rawtypes")
        public final ForgeConfigSpec.ConfigValue overrides;

        /** 解析后的 {target -> {attr -> value}} 映射 */
        private Map<String, Map<String, Double>> parsed;

        @SuppressWarnings({"rawtypes", "unchecked"})
        Data(ForgeConfigSpec.Builder b) {
            b.comment(
                "属性覆盖 | Attribute Overrides",
                "格式: [\"注册名\", \"属性\", 数值] | Format: [\"registry_name\", \"attribute\", value]",
                "",
                "--- 实体属性 | Entity attributes ---",
                "  注册名 = 完整 Entity ID | Full Entity ID",
                "  属性   = max_health / move_speed / attack_damage / fly_speed",
                "  示例 | Example:  [\"mythicalcreatures:bear\", \"max_health\", 80.0]",
                "  示例 | Example:  [\"mythicalcreatures:rainbow_dash\", \"fly_speed\", 0.6]",
                "",
                "--- 物品属性 | Item attributes ---",
                "  注册名 = 完整 Item ID（每件物品单独覆盖）| Full Item ID (per-item override)",
                "  属性   = 武器 Weapon: attack_damage",
                "          护甲 Armor:  armor / armor_toughness / armor_kb_resist",
                "          通用 Any:   max_damage (有耐久物品的耐久上限)",
                "  注: max_damage 需 Mixin 支持，仅重启后生效 | max_damage requires restart",
                "  示例 | Example:  [\"mythicalcreatures:bowsers_sword\", \"attack_damage\", 12.0]",
                "  示例 | Example:  [\"mythicalcreatures:bowsers_helmet\", \"armor\", 5]",
                "",
                "--- 全局参数 | Global params ---",
                "  注册名 = global_params",
                "  sword_cooldown    = 紫悦之剑技能冷却 (tick) | Twilight sword cooldown",
                "  repair_interval   = 修补触发间隔 (tick) | Repair trigger interval",
                "  repair_amount     = 每次修补恢复耐久 | Durability restored per repair",
                "  bleeding_base     = 流血基础伤害 | Bleeding base damage",
                "  bleeding_amp      = 流血每级额外伤害 | Bleeding damage per level",
                "  wing_flap_speed   = 翅膀扇动速度 | Wing flap animation speed",
                "  wing_decay_speed  = 翅膀收起衰减速度 | Wing decay speed",
                "  follow_range      = 生物跟随范围 (方块) | Entity follow range",
                "  示例 | Example:  [\"global_params\", \"sword_cooldown\", 10]",
                "  示例 | Example:  [\"global_params\", \"bleeding_base\", 2.0]",
                "",
                "--- 其他 | Other ---",
                "  global_params.cutie_mark_slots = 可爱标志栏位数 (默认1) | Cutie mark slot count",
                "",
                "留空则不覆盖任何属性 | Leave empty to use all defaults"
            );
            overrides = b.defineList("overrides", ArrayList::new,
                o -> o instanceof List<?> list && list.size() == 3
                  && list.get(0) instanceof String
                  && list.get(1) instanceof String
                  && list.get(2) instanceof Number);
        }

        /** 在配置加载后调用，解析 overrides 列表 */
        public void bake() {
            parsed = new HashMap<>();
            @SuppressWarnings("unchecked")
            var entries = (List<?>) overrides.get();
            for (Object entry : entries) {
                if (!(entry instanceof List<?> list) || list.size() != 3) continue;
                String target = String.valueOf(list.get(0)).trim();
                String attr   = String.valueOf(list.get(1)).trim();
                try {
                    double val = ((Number) list.get(2)).doubleValue();
                    parsed.computeIfAbsent(target, k -> new HashMap<>()).put(attr, val);
                } catch (Exception ignored) {}
            }
        }

        /** 获取实体属性值（优先覆盖，否则取默认） */
        public double entityAttr(String entityId, String attr) {
            var m = parsed != null ? parsed.get(entityId) : null;
            if (m != null && m.containsKey(attr))
                return m.get(attr);
            return D.ENTITY_DEFAULTS.getOrDefault(entityId + "|" + attr, 0.0);
        }

        /** 获取全局参数值（items/animation 等） */
        public double get(String key, String attr, double fallback) {
            var m = parsed != null ? parsed.get(key) : null;
            if (m != null && m.containsKey(attr))
                return m.get(attr);
            return fallback;
        }

        /** 获取全局 int 参数 */
        public int getInt(String key, String attr, int fallback) {
            return (int) get(key, attr, fallback);
        }

        /** 获取装备属性值 */
        public double equipAttr(String eqKey, String attr) {
            var m = parsed != null ? parsed.get(eqKey) : null;
            if (m != null && m.containsKey(attr))
                return m.get(attr);
            return 0.0;
        }

        /** twilicane 召唤列表 */
        public List<? extends String> twilicaneSpawnList() {
            return D.TWILICANE_SPAWN;
        }
    }
}
