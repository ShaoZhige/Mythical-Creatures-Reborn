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
        public static final String FS_TAMING = "mythicalcreatures:fluttershy_cutiemark";
        public static final String PP_TAMING = "mythicalcreatures:pinkie_pie_cutiemark";
        public static final String RY_TAMING = "mythicalcreatures:rarity_cutiemark";
        public static final String HL_TAMING = "mythicalcreatures:holy_light_radiance_cutiemark";

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
            entity("mythicalcreatures:rainbow_dash",      220, 0.35, 9);
            entity("mythicalcreatures:twilight_sparkle",  280, 0.3, 12);
            entity("mythicalcreatures:applejack",         220, 0.4, 9);
            entity("mythicalcreatures:fluttershy",        220, 0.25, 9);
            ENTITY_DEFAULTS.put("mythicalcreatures:fluttershy|fly_speed", 0.15);
            entity("mythicalcreatures:pinkie_pie",        220, 0.25, 9);
            entity("mythicalcreatures:rarity",            220, 0.25, 9);
            entity("mythicalcreatures:holy_light_radiance",220.0, 0.25, 9.0);
            entity("mythicalcreatures:bear",              40.0, 0.25, 6.0);
            entity("mythicalcreatures:cockatrice",        40.0, 0.25, 6.0);
            entity("mythicalcreatures:garble",            420, 0.28, 18);
            entity("mythicalcreatures:kingbowser_9000",   40.0, 0.25, 6.0);
            entity("mythicalcreatures:parasprite",        40.0, 0.25, 6.0);
            entity("mythicalcreatures:phoenix",           520, 0.25, 22);
            entity("mythicalcreatures:ursa_major",        520, 0.25, 22);
            entity("mythicalcreatures:buffalo", 100, 0.25, 9);
            entity("mythicalcreatures:chief_thunderhooves", 420, 0.28, 18);
            entity("mythicalcreatures:black_widow", 35, 0.3, 5);
            entity("mythicalcreatures:leviathan", 60.0, 0.2, 8.0);
            entity("mythicalcreatures:centipede", 35, 0.3, 5);
            entity("mythicalcreatures:hydra", 650, 0.22, 28);
            entity("mythicalcreatures:windigo", 520, 0.3, 22);
            entity("mythicalcreatures:baby_moose", 20.0, 0.3, 2.0);
            entity("mythicalcreatures:adult_moose", 45.0, 0.22, 6.0);
            entity("mythicalcreatures:tough_guy", 50, 0.25, 7);
            entity("mythicalcreatures:mavis", 35, 0.35, 5);
            entity("mythicalcreatures:manticore", 520, 0.3, 22);
            entity("mythicalcreatures:rainbow_centipede", 45, 0.32, 6);
            entity("mythicalcreatures:arctic_scorpion", 420, 0.28, 18);
            entity("mythicalcreatures:timber_wolf", 40, 0.35, 6);
            entity("mythicalcreatures:crabzilla", 650, 0.18, 28);
            entity("mythicalcreatures:iron_will", 420, 0.22, 18);
            entity("mythicalcreatures:skull_of_doom", 50, 0.1, 7);
            entity("mythicalcreatures:prince_rutherford", 420, 0.25, 18);
            entity("mythicalcreatures:spikezilla", 650, 0.2, 28);
            entity("mythicalcreatures:rhinoceros", 60.0, 0.2, 7.0);
            entity("mythicalcreatures:robot_sombra", 55, 0.28, 7);
            entity("mythicalcreatures:cragadile", 50.0, 0.28, 7.0);
            entity("mythicalcreatures:twilight_magic",    20.0, 0.50, 8.0);
            ENTITY_DEFAULTS.put("mythicalcreatures:twilight_magic|fly_speed", 0.183);
            ENTITY_DEFAULTS.put("mythicalcreatures:rainbow_dash|fly_speed", 0.333);
            ENTITY_DEFAULTS.put("mythicalcreatures:twilight_sparkle|fly_speed", 0.267);
            // 自主飞行生物（接 fly_speed 配置）：凤凰 / 雪魔 / 蝎尾狮 / 穗龙斯拉 / 盖伯
            ENTITY_DEFAULTS.put("mythicalcreatures:phoenix|fly_speed", 0.40);
            ENTITY_DEFAULTS.put("mythicalcreatures:windigo|fly_speed", 0.20);
            ENTITY_DEFAULTS.put("mythicalcreatures:manticore|fly_speed", 0.30);
            ENTITY_DEFAULTS.put("mythicalcreatures:spikezilla|fly_speed", 0.25);
            ENTITY_DEFAULTS.put("mythicalcreatures:garble|fly_speed", 0.30);

            // 飞行坐骑骑乘调参默认值（空配置 = 小马手感；玩家可在 overrides 覆盖）
            for (String id : new String[]{"mythicalcreatures:twilight_sparkle", "mythicalcreatures:rainbow_dash"}) {
                ENTITY_DEFAULTS.put(id + "|ridden_speed_factor", 1.0);
                ENTITY_DEFAULTS.put(id + "|vertical_up", 0.3);
                ENTITY_DEFAULTS.put(id + "|vertical_down", -0.4);
                ENTITY_DEFAULTS.put(id + "|vertical_hover", -0.04);
                ENTITY_DEFAULTS.put(id + "|horizontal_factor", 1.0);
                ENTITY_DEFAULTS.put(id + "|inertia_decay", 0.9);
            }

            // 柔柔：飞行坐骑，但默认比紫悦/云宝飞得更慢、更飘
            ENTITY_DEFAULTS.put("mythicalcreatures:fluttershy|ridden_speed_factor", 0.6);
            ENTITY_DEFAULTS.put("mythicalcreatures:fluttershy|vertical_up", 0.18);
            ENTITY_DEFAULTS.put("mythicalcreatures:fluttershy|vertical_down", -0.25);
            ENTITY_DEFAULTS.put("mythicalcreatures:fluttershy|vertical_hover", -0.03);
            ENTITY_DEFAULTS.put("mythicalcreatures:fluttershy|horizontal_factor", 0.6);
            ENTITY_DEFAULTS.put("mythicalcreatures:fluttershy|inertia_decay", 0.92);

            // 地面坐骑骑乘调参默认值（空配置 = 苹果嘉儿标准手感；玩家可在 overrides 覆盖）
            for (String id : new String[]{"mythicalcreatures:applejack", "mythicalcreatures:holy_light_radiance",
                                          "mythicalcreatures:pinkie_pie", "mythicalcreatures:rarity"}) {
                ENTITY_DEFAULTS.put(id + "|ridden_speed_factor", 1.15); // 骑乘移动速度倍率（基于实体 move_speed）
                ENTITY_DEFAULTS.put(id + "|jump_height", 0.63);         // 满蓄力跳跃初速度基数
            }
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
                "格式: [\"注册名\", \"属性\", 数值]  |  Format: [\"name\", \"attr\", value]",
                "详细教程见  |  Full guide: https://github.com/ShaoZhige/Mythical-Creatures-Reborn/wiki",
                "",
                "--- 实体  |  Entity ---",
                "  属性: max_health / move_speed / attack_damage / fly_speed",
"  飞行坐骑骑乘调参(可覆盖): ridden_speed_factor / vertical_up / vertical_down / vertical_hover / horizontal_factor / inertia_decay",
"  地面坐骑骑乘调参(可覆盖): ridden_speed_factor / jump_height",
"  紫悦/云宝 自主飞行(可覆盖): flight_chance / fly_cooldown_min / fly_cooldown_max / fly_duration_min / fly_duration_max",
"  示例: [\"mythicalcreatures:bear\", \"max_health\", 80.0]",
                "",
                "--- 物品  |  Item ---",
                "  武器: attack_damage  |  护甲: armor / armor_toughness / armor_kb_resist  |  耐久: max_damage",
                "  示例: [\"mythicalcreatures:twilight_sword\", \"attack_damage\", 12.0]",
                "  示例: [\"mythicalcreatures:twilight_sword\", \"max_damage\", 9999]",
                "",
                "--- 全局  |  Global ---",
                "  注册名: global_params",
                "  可用: sword_cooldown(40) repair_interval(60) repair_amount(1) bleeding_base(1.0) bleeding_amp(0.5)",
                "        wing_flap_speed(0.4) wing_decay_speed(0.15) follow_range(16) cutie_mark_slots(1)",
                "  示例: [\"global_params\", \"sword_cooldown\", 5]",
                "",
                "留空 = 全部默认  |  Empty = all defaults"
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

        /**
         * 获取实体属性值：优先取玩家 override，否则取内置默认。
         * 注意：默认缺失时静默返回 0.0（可能让实体数值归零），调用方务必保证 D.ENTITY_DEFAULTS 已含该键。
         *
         * Gets an entity attribute: player overrides win, then the built-in default.
         * Warning: a missing default silently returns 0.0 (which can zero out a stat),
         * so callers must ensure the key exists in D.ENTITY_DEFAULTS.
         */
        public double entityAttr(String entityId, String attr) {
            var m = parsed != null ? parsed.get(entityId) : null;
            if (m != null && m.containsKey(attr))
                return m.get(attr);
            return D.ENTITY_DEFAULTS.getOrDefault(entityId + "|" + attr, 0.0);
        }

        /**
         * 获取全局参数（item/animation 等）：优先取覆盖，否则用调用方传入的 fallback。
         * 与 entityAttr 不同，此方法不会静默返回 0.0——缺失时一定回退到 fallback。
         *
         * Global params (items/animation/etc.): override wins, otherwise the supplied fallback.
         * Unlike entityAttr(), a missing key returns the fallback, never a silent 0.0.
         */
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
