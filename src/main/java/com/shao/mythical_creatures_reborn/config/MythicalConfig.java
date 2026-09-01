package com.shao.mythical_creatures_reborn.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import java.io.IOException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    /** 配置加载事件里捕获的 COMMON ModConfig 引用，供 persistIfDirty 写回 common.toml 使用 */
    public static ModConfig COMMON_CONFIG = null;

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "mythical_creatures_reborn/common.toml");
    }

    /* ================================================================
     * 默认值（仅当 overrides 未覆盖时使用）
     * ================================================================ */

    public static final class D {
        // 实体 taming 物品
        public static final String RD_TAMING = "mythical_creatures_reborn:rainbow_dash_cutiemark";
        public static final String TS_TAMING = "mythical_creatures_reborn:twilight_cutiemark";
        public static final String AJ_TAMING = "mythical_creatures_reborn:applejack_cutiemark";
        public static final String FS_TAMING = "mythical_creatures_reborn:fluttershy_cutiemark";
        public static final String PP_TAMING = "mythical_creatures_reborn:pinkie_pie_cutiemark";
        public static final String RY_TAMING = "mythical_creatures_reborn:rarity_cutiemark";
        public static final String HL_TAMING = "mythical_creatures_reborn:holy_light_radiance_cutiemark";

        // 紫悦之杖召唤列表
        public static final List<String> TWILICANE_SPAWN = List.of(
            "mythical_creatures_reborn:twilight_sparkle", "mythical_creatures_reborn:rainbow_dash",
            "mythical_creatures_reborn:applejack", "mythical_creatures_reborn:bear",
            "mythical_creatures_reborn:cockatrice", "mythical_creatures_reborn:garble",
            "mythical_creatures_reborn:kingbowser_9000", "mythical_creatures_reborn:parasprite",
            "mythical_creatures_reborn:phoenix", "mythical_creatures_reborn:ursa_major",
            "mythical_creatures_reborn:fluttershy", "mythical_creatures_reborn:holy_light_radiance",
            "mythical_creatures_reborn:pinkie_pie", "mythical_creatures_reborn:rarity"
        );

        // 默认实体属性（用于没有覆盖时）
        // ── 平衡基线说明（改之前必读）──────────────────────────────────────
        // 以下数值为「原模组 MythicalC v1.2.7 的平衡基线」，属【可调参数】而非代码派生：
        //   · 全部可通过 common.toml 的 [["mythical_creatures_reborn:xxx","max_health",N]] 覆盖，无需改代码；
        //   · 梯级约定（仅作一致性参考，非强制；Boss 数值基线已于 v0.6 起整体 ×1.5：生命与攻击伤害，普通生物不变）：
        //       顶级 Boss  ~975 血 / 42-54 伤（hydra、spikezilla、crabzilla）
        //       次级 Boss  ~780 血 / 44 伤（phoenix、ursa_major、windigo、manticore）
        //       中型精英  ~630 血 / 35 伤（garble、chief、arctic_scorpion、iron_will、prince）
        //       小怪/坐骑 ~35-100 血 / 5-9 伤（bear、buffalo、普通敌对等）
        //   · move_speed 为 MC 标准单位（≈0.25 步行，0.35 较快），非百分比。
        //   · 每个具体数值的「设计理由」已随原模组遗失，标记为【意图未知·沿用原版】；
        //     调整时按手感测试即可，不必追求还原某个理论值。改一个生物请参考上述梯级保持一致。
        static final Map<String, Double> ENTITY_DEFAULTS = new HashMap<>();
        static void entity(String id, double hp, double spd, double dmg) {
            ENTITY_DEFAULTS.put(id + "|max_health", hp);
            ENTITY_DEFAULTS.put(id + "|move_speed", spd);
            ENTITY_DEFAULTS.put(id + "|attack_damage", dmg);
        }

        static {
            // 实体基线属性（hp / move_speed / attack_damage）。数值梯级含义见上方块注释。
            entity("mythical_creatures_reborn:rainbow_dash",      220, 0.35, 9);
            entity("mythical_creatures_reborn:twilight_sparkle",  280, 0.3, 12);
            entity("mythical_creatures_reborn:applejack",         220, 0.4, 9);
            entity("mythical_creatures_reborn:fluttershy",        220, 0.25, 9);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:fluttershy|fly_speed", 0.15);
            entity("mythical_creatures_reborn:pinkie_pie",        220, 0.25, 9);
            entity("mythical_creatures_reborn:rarity",            220, 0.25, 9);
            entity("mythical_creatures_reborn:holy_light_radiance",220.0, 0.25, 9.0);
            entity("mythical_creatures_reborn:bear",              40.0, 0.25, 6.0);
            entity("mythical_creatures_reborn:cockatrice",        40.0, 0.25, 6.0);
            entity("mythical_creatures_reborn:garble",            630, 0.28, 35);
            entity("mythical_creatures_reborn:kingbowser_9000",   60.0, 0.25, 12.0);
            entity("mythical_creatures_reborn:parasprite",        40.0, 0.25, 6.0);
            entity("mythical_creatures_reborn:phoenix",           780, 0.25, 44);
            entity("mythical_creatures_reborn:ursa_major",        780, 0.25, 44);
            entity("mythical_creatures_reborn:buffalo", 100, 0.25, 9);
            entity("mythical_creatures_reborn:chief_thunderhooves", 630, 0.28, 35);
            entity("mythical_creatures_reborn:black_widow", 35, 0.3, 5);
            entity("mythical_creatures_reborn:leviathan", 60.0, 0.2, 8.0);
            entity("mythical_creatures_reborn:centipede", 35, 0.3, 5);
            entity("mythical_creatures_reborn:hydra", 975, 0.22, 54);
            entity("mythical_creatures_reborn:windigo", 780, 0.4, 44);
            // 雪魔改为飞行单位：fly_speed 必填，否则 FLYING_SPEED 属性静默 0.0 飞不起来（同末日颅骨）
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:windigo|fly_speed", 0.30);
            entity("mythical_creatures_reborn:baby_moose", 20.0, 0.3, 2.0);
            entity("mythical_creatures_reborn:adult_moose", 45.0, 0.22, 6.0);
            entity("mythical_creatures_reborn:tough_guy", 50, 0.25, 7);
            entity("mythical_creatures_reborn:mavis", 35, 0.35, 5);
            entity("mythical_creatures_reborn:manticore", 780, 0.3, 44);
            entity("mythical_creatures_reborn:rainbow_centipede", 45, 0.32, 6);
            entity("mythical_creatures_reborn:arctic_scorpion", 630, 0.28, 35);
            entity("mythical_creatures_reborn:timber_wolf", 40, 0.35, 6);
            entity("mythical_creatures_reborn:crabzilla", 975, 0.18, 42);
            entity("mythical_creatures_reborn:iron_will", 630, 0.22, 35);
            entity("mythical_creatures_reborn:skull_of_doom", 50, 0.1, 7);
            entity("mythical_creatures_reborn:prince_rutherford", 630, 0.25, 35);
            entity("mythical_creatures_reborn:spikezilla", 975, 0.2, 54);
            entity("mythical_creatures_reborn:rhinoceros", 60.0, 0.2, 7.0);
            entity("mythical_creatures_reborn:robot_sombra", 55, 0.28, 7);
            entity("mythical_creatures_reborn:cragadile", 75.0, 0.28, 14.0);
            entity("mythical_creatures_reborn:twilight_magic",    20.0, 0.50, 8.0);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:twilight_magic|fly_speed", 0.183);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:rainbow_dash|fly_speed", 0.333);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:twilight_sparkle|fly_speed", 0.267);
            // 自主飞行生物（接 fly_speed 配置）：凤凰 / 蝎尾狮 / 穗龙斯拉 / 盖伯
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:phoenix|fly_speed", 0.40);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:manticore|fly_speed", 0.30);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:spikezilla|fly_speed", 0.25);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:garble|fly_speed", 0.30);
            // 末日颅骨：会飞（蜜蜂式悬停）+ 白天燃烧（亡灵）；fly_speed 必填，否则 entityAttr 静默 0.0 飞不起来
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:skull_of_doom|fly_speed", 0.22);

            // 飞行坐骑骑乘调参默认值（空配置 = 小马手感；玩家可在 overrides 覆盖）
            for (String id : new String[]{"mythical_creatures_reborn:twilight_sparkle", "mythical_creatures_reborn:rainbow_dash"}) {
                ENTITY_DEFAULTS.put(id + "|ridden_speed_factor", 1.0);
                ENTITY_DEFAULTS.put(id + "|vertical_up", 0.3);
                ENTITY_DEFAULTS.put(id + "|vertical_down", -0.4);
                ENTITY_DEFAULTS.put(id + "|vertical_hover", -0.04);
                ENTITY_DEFAULTS.put(id + "|horizontal_factor", 1.0);
                ENTITY_DEFAULTS.put(id + "|inertia_decay", 0.9);
            }

            // 柔柔：飞行坐骑，但默认比紫悦/云宝飞得更慢、更飘
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:fluttershy|ridden_speed_factor", 0.6);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:fluttershy|vertical_up", 0.18);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:fluttershy|vertical_down", -0.25);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:fluttershy|vertical_hover", -0.03);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:fluttershy|horizontal_factor", 0.6);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:fluttershy|inertia_decay", 0.92);

            // 地面坐骑骑乘调参默认值（空配置 = 苹果嘉儿标准手感；玩家可在 overrides 覆盖）
            for (String id : new String[]{"mythical_creatures_reborn:applejack", "mythical_creatures_reborn:holy_light_radiance",
                                          "mythical_creatures_reborn:pinkie_pie", "mythical_creatures_reborn:rarity"}) {
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
        private final Map<String, Map<String, Double>> parsed = new HashMap<>();

        /** 解析后的 {target -> {attr -> comment}} 映射（来自 overrides 第 4 元，可选注释） */
        private final Map<String, Map<String, String>> comments = new HashMap<>();

        private static final Logger LOGGER = LogManager.getLogger(Data.class);

        @SuppressWarnings({"rawtypes", "unchecked"})
        Data(ForgeConfigSpec.Builder b) {
            b.comment(
                "格式: [\"注册名\", \"属性\", 数值]  |  Format: [\"name\", \"attr\", value]",
                "详细教程见  |  Full guide: https://github.com/ShaoZhige/Mythical-Creatures-Reborn/wiki",
                "",
                "注释  |  Comments:",
                "  支持 # 注释（含 overrides 数组内的分组注释），配置加载时会自动跳过、不参与解析。",
                "  例：",
                "  overrides = [",
                "      # ===== 生物属性 ===== #",
                "      [\"mythical_creatures_reborn:ursa_major\", \"max_health\", 300.0],",
                "      [\"mythical_creatures_reborn:bear\", \"max_health\", 120.0],",
                "      # ===== 装备属性 ===== #",
                "      [\"mythical_creatures_reborn:twilight_sword\", \"attack_damage\", 15.0],",
                "      # ===== 全局参数 ===== #",
                "      [\"global_params\", \"follow_range\", 32],",
                "  ]",
                "",
                "--- 实体  |  Entity ---",
                "  属性: max_health / move_speed / attack_damage / fly_speed",
"  飞行坐骑骑乘调参(可覆盖): ridden_speed_factor / vertical_up / vertical_down / vertical_hover / horizontal_factor / inertia_decay",
"  地面坐骑骑乘调参(可覆盖): ridden_speed_factor / jump_height",
"  紫悦/云宝 自主飞行(可覆盖): flight_chance / fly_cooldown_min / fly_cooldown_max / fly_duration_min / fly_duration_max",
"  示例: [\"mythical_creatures_reborn:bear\", \"max_health\", 80.0]",
                "",
                "--- 物品  |  Item ---",
                "  武器: attack_damage  |  护甲: armor / armor_toughness / armor_kb_resist  |  耐久: max_damage",
                "  示例: [\"mythical_creatures_reborn:twilight_sword\", \"attack_damage\", 12.0]",
                "  示例: [\"mythical_creatures_reborn:twilight_sword\", \"max_damage\", 9999]",
                "",
                "--- 全局  |  Global ---",
                "  注册名: global_params",
                "  可用: sword_cooldown(40) repair_interval(60) repair_amount(1) bleeding_base(1.0) bleeding_amp(0.5)",
                "        wing_flap_speed(0.4) wing_decay_speed(0.15) follow_range(32) cutie_mark_slots(1)",
                "  示例: [\"global_params\", \"sword_cooldown\", 5]",
                "",
                "留空 = 全部默认  |  Empty = all defaults"
            );
            overrides = b.defineList("overrides", ArrayList::new,
                o -> o instanceof List<?> list && (list.size() == 3 || list.size() == 4)
                  && list.get(0) instanceof String
                  && list.get(1) instanceof String
                  && list.get(2) instanceof Number
                  && (list.size() == 3 || list.get(3) instanceof String));
        }

        /** 在配置加载后调用，解析 overrides 列表 */
        public void bake() {
            parsed.clear();
            comments.clear();
            @SuppressWarnings("unchecked")
            var entries = (List<?>) overrides.get();
            for (Object entry : entries) {
                if (!(entry instanceof List<?> list) || (list.size() != 3 && list.size() != 4)) {
                    LOGGER.warn("忽略格式错误的 override 条目（应为 [注册名, 属性, 数值] 或 [注册名, 属性, 数值, 注释]）: {}", entry);
                    continue;
                }
                String target = String.valueOf(list.get(0)).trim();
                String attr   = String.valueOf(list.get(1)).trim();
                try {
                    double val = ((Number) list.get(2)).doubleValue();
                    parsed.computeIfAbsent(target, k -> new HashMap<>()).put(attr, val);
                    if (list.size() >= 4 && list.get(3) instanceof String c && !c.isEmpty())
                        comments.computeIfAbsent(target, k -> new HashMap<>()).put(attr, c);
                } catch (Exception e) {
                    LOGGER.warn("override 解析失败，已忽略: {} -> {} ({})", target, attr, e.getMessage());
                }
            }
            // 校验 override 目标是否为已知实体（捕获配置拼写错误，避免「静默无效」）
            for (String target : parsed.keySet()) {
                if (target.equals("global_params")) continue;
                boolean knownEntity = D.ENTITY_DEFAULTS.keySet().stream()
                        .anyMatch(k -> k.startsWith(target + "|"));
                if (!knownEntity)
                    LOGGER.warn("override 目标「{}」不在已知实体列表中，该条覆盖可能永久无效", target);
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

        /* ================================================================
         * 可视化编辑器支持：override 读写与持久化
         *  GUI 改完的数值直接写回本文件 overrides（第 4 元为可选注释）。
         * ================================================================ */

        /** 某实体/属性是否存在 override */
        public boolean isOverridden(String target, String attr) {
            var m = parsed.get(target);
            return m != null && m.containsKey(attr);
        }

        /** 取某实体/属性的注释（无则空串） */
        public String commentOf(String target, String attr) {
            var m = comments.get(target);
            return m != null ? m.getOrDefault(attr, "") : "";
        }

        /** 写回单条 override（含可选注释），仅更新内存（实时预览用），落盘由 persistIfDirty() 完成 */
        public void setOverride(String target, String attr, double value, String comment) {
            parsed.computeIfAbsent(target, k -> new HashMap<>()).put(attr, value);
            if (comment != null && !comment.isEmpty())
                comments.computeIfAbsent(target, k -> new HashMap<>()).put(attr, comment);
            else if (comments.containsKey(target))
                comments.get(target).remove(attr);
        }

        /** 移除单条 override（含其注释），仅更新内存 */
        public void resetOverride(String target, String attr) {
            if (parsed.containsKey(target)) {
                parsed.get(target).remove(attr);
                if (parsed.get(target).isEmpty()) parsed.remove(target);
            }
            if (comments.containsKey(target)) {
                comments.get(target).remove(attr);
                if (comments.get(target).isEmpty()) comments.remove(target);
            }
        }

        /** 清空全部 override，仅更新内存 */
        public void clearAllOverrides() {
            parsed.clear();
            comments.clear();
        }

        /** 将 parsed+comments 重建为 [[target, attr, value, comment?]] 列表 */
        private List<List<Object>> buildOverridesList() {
            List<List<Object>> out = new ArrayList<>();
            for (var e1 : parsed.entrySet()) {
                String target = e1.getKey();
                for (var e2 : e1.getValue().entrySet()) {
                    List<Object> row = new ArrayList<>();
                    row.add(target);
                    row.add(e2.getKey());
                    row.add(e2.getValue());
                    String c = comments.getOrDefault(target, Map.of()).getOrDefault(e2.getKey(), "");
                    if (!c.isEmpty()) row.add(c);
                    out.add(row);
                }
            }
            return out;
        }

        /** 备份并写回 common.toml 的 overrides（覆盖式重写整个数组，保留 b.comment 说明块） */
        @SuppressWarnings({"rawtypes", "unchecked"})
        public void persistIfDirty() {
            List<List<Object>> list = buildOverridesList();
            overrides.set(list);
            ModConfig cfg = MythicalConfig.COMMON_CONFIG;
            if (cfg == null) {
                LOGGER.error("找不到 COMMON 配置，无法将 override 写入 common.toml");
                return;
            }
            Path path = cfg.getFullPath();
            try {
                Files.copy(path, Paths.get(path + ".bak"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {}
            try {
                cfg.getConfigData().set("overrides", list);
                cfg.save();
                LOGGER.info("已将 {} 条生物属性 override 写入 common.toml", list.size());
            } catch (Exception e) {
                LOGGER.error("保存 common.toml 失败: {}", e.getMessage());
            }
        }
    }
}
