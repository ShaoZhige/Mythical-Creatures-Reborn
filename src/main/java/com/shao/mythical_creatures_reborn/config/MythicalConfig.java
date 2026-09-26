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
            entity("mythical_creatures_reborn:kingbowser_9000",   199.0, 0.45, 20.0);
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:kingbowser_9000|armor", 15.0);
            entity("mythical_creatures_reborn:parasprite",        5.0, 0.25, 3.0);
            entity("mythical_creatures_reborn:phoenix",           780, 0.25, 44);
            entity("mythical_creatures_reborn:ursa_major",        780, 0.25, 44);
            entity("mythical_creatures_reborn:buffalo", 35, 0.25, 9);
            entity("mythical_creatures_reborn:chief_thunderhooves", 630, 0.28, 35);
            entity("mythical_creatures_reborn:black_widow", 35, 0.3, 5);
            entity("mythical_creatures_reborn:leviathan", 60.0, 0.2, 8.0);
            entity("mythical_creatures_reborn:centipede", 35, 0.3, 5);
            entity("mythical_creatures_reborn:hydra", 975, 0.22, 54);
            entity("mythical_creatures_reborn:windigo", 780, 0.4, 44);
            // 雪魔改为飞行单位：fly_speed 必填，否则 FLYING_SPEED 属性静默 0.0 飞不起来（同末日颅骨）
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:windigo|fly_speed", 0.30);
            // 麋鹿生态重构：小麋鹿削弱、大麋鹿略增强（0.8-beta）
            entity("mythical_creatures_reborn:baby_moose", 14.0, 0.3, 1.0);
            entity("mythical_creatures_reborn:adult_moose", 60.0, 0.22, 8.0);
            entity("mythical_creatures_reborn:tough_guy", 50, 0.25, 7);
            entity("mythical_creatures_reborn:mavis", 35, 0.35, 5);
            entity("mythical_creatures_reborn:manticore", 780, 0.3, 44);
            entity("mythical_creatures_reborn:rainbow_centipede", 45, 0.32, 6);
            entity("mythical_creatures_reborn:arctic_scorpion", 630, 0.28, 35);
            entity("mythical_creatures_reborn:timber_wolf", 40, 0.35, 6);
            entity("mythical_creatures_reborn:crabzilla", 975, 0.18, 42);
            entity("mythical_creatures_reborn:iron_will", 400, 0.22, 15);
            entity("mythical_creatures_reborn:skull_of_doom", 50, 0.1, 7);
            entity("mythical_creatures_reborn:prince_rutherford", 630, 0.25, 35);
            // 穗龙斯拉：移速 ×2.5（0.2 → 0.5）。横扫 Goal 以 1.2× 移速追目标，
            // 原速下追不上地面目标；0.5 时实际追击速度 ≈ 0.6 方块/tick。
            entity("mythical_creatures_reborn:spikezilla", 975, 0.5, 54);
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
            // 帕拉斯prite：改为蜜蜂式常驻飞行（永不落地），fly_speed 必填，否则 FLYING_SPEED 静默 0.0 飞不起来
            ENTITY_DEFAULTS.put("mythical_creatures_reborn:parasprite|fly_speed", 0.25);

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

            // ── 技能调参 ────────────────────────────────────────────────────
            // 登记后可在编辑器的「生物」分类里改，也能写进 common.toml。
            // 只有在此登记过的键才会在 GUI 暴露（见 MobStatsManager.ABILITY_KEYS）。

            // 雪魔：地面冲锋 + 空中追击 + 不稳定物品霰弹
            abi("mythical_creatures_reborn:windigo", "charge_damage_mult", 1.5);  // 冲锋伤害倍率
            abi("mythical_creatures_reborn:windigo", "charge_speed", 1.8);        // 冲锋速度
            abi("mythical_creatures_reborn:windigo", "charge_duration", 18);      // 冲锋持续(tick)
            abi("mythical_creatures_reborn:windigo", "charge_cooldown", 25);      // 冲锋冷却(tick)
            abi("mythical_creatures_reborn:windigo", "charge_min_dist", 4.0);     // 触发冲锋的最小距离
            abi("mythical_creatures_reborn:windigo", "aggro_buildup", 30);        // 蓄怒阈值(tick)
            abi("mythical_creatures_reborn:windigo", "aggro_decay_interval", 20); // 怒气衰减间隔(tick)
            abi("mythical_creatures_reborn:windigo", "trigger_odds", 12);         // 触发概率分母(1/N)
            abi("mythical_creatures_reborn:windigo", "destroy_radius", 2);        // 冲锋破坏方块半径
            abi("mythical_creatures_reborn:windigo", "hit_radius", 2.5);          // 冲锋命中判定半径
            abi("mythical_creatures_reborn:windigo", "drop_chance", 0.1);         // 掉落概率
            abi("mythical_creatures_reborn:windigo", "hover_min_dist", 12.0);     // 空中追击最小距离
            abi("mythical_creatures_reborn:windigo", "hover_max_dist", 32.0);     // 空中追击最大距离
            abi("mythical_creatures_reborn:windigo", "hover_offset", 14.0);       // 悬停高度偏移
            abi("mythical_creatures_reborn:windigo", "shot_count_min", 15);       // 霰弹最少颗数
            abi("mythical_creatures_reborn:windigo", "shot_count_max", 25);       // 霰弹最多颗数
            abi("mythical_creatures_reborn:windigo", "shot_spread", 0.1);         // 霰弹散布(弧度)

            // 麋鹿：邻近防御（大/小麋鹿都有 MooseProximityTargetGoal）
            for (String id : new String[]{"mythical_creatures_reborn:baby_moose", "mythical_creatures_reborn:adult_moose"}) {
                abi(id, "proximity_range", 6.0);      // 进入该范围才产生仇恨
            }
            // 麋鹿冲撞：**只有大麋鹿**挂了 MooseChargeGoal（小麋鹿只跟随族群、不冲撞），
            // 因此冲撞相关键只登记在 adult_moose 上，避免编辑器里出现改了没用的项。
            abi("mythical_creatures_reborn:adult_moose", "charge_damage_mult", 2.5);   // 冲撞伤害倍率
            abi("mythical_creatures_reborn:adult_moose", "charge_speed", 1.1);         // 冲撞速度
            abi("mythical_creatures_reborn:adult_moose", "charge_duration", 40);       // 冲撞持续(tick)
            abi("mythical_creatures_reborn:adult_moose", "charge_cooldown", 25);       // 冲撞冷却(tick)
            abi("mythical_creatures_reborn:adult_moose", "charge_min_dist", 1.8);      // 触发冲撞的最小距离
            abi("mythical_creatures_reborn:adult_moose", "trigger_odds", 25);          // 触发概率分母(1/N)

            // 穗龙斯拉：横扫
            abi("mythical_creatures_reborn:spikezilla", "sweep_damage_mult", 1.6);   // 横扫伤害倍率
            abi("mythical_creatures_reborn:spikezilla", "sweep_half_angle", 60.0);   // 横扫扇形半角(度)
            abi("mythical_creatures_reborn:spikezilla", "sweep_knockback", 1.4);     // 额外击退(水平)
            abi("mythical_creatures_reborn:spikezilla", "sweep_knockback_y", 0.4);   // 额外击退(垂直)
            abi("mythical_creatures_reborn:spikezilla", "sweep_cooldown", 18);       // 横扫冷却(tick)
            abi("mythical_creatures_reborn:spikezilla", "sweep_windup", 6);          // 前摇(tick)

            // 末日颅骨：俯冲扑击
            abi("mythical_creatures_reborn:skull_of_doom", "dive_speed", 0.6);         // 俯冲速度
            abi("mythical_creatures_reborn:skull_of_doom", "climb_speed", 0.32);       // 拉起速度
            abi("mythical_creatures_reborn:skull_of_doom", "dive_trigger_range", 20.0);// 触发俯冲的距离
            abi("mythical_creatures_reborn:skull_of_doom", "dive_max_ticks", 50);      // 俯冲最长(tick)
            abi("mythical_creatures_reborn:skull_of_doom", "climb_ticks", 30);         // 拉起耗时(tick)
            abi("mythical_creatures_reborn:skull_of_doom", "dive_cooldown", 15);       // 俯冲冷却(tick)
            abi("mythical_creatures_reborn:skull_of_doom", "dive_hit_inflate", 1.0);   // 命中判定膨胀

            // 紫悦：远程攻击 / 魔法团召唤 / 施法特效
            abi("mythical_creatures_reborn:twilight_sparkle", "summon_chance", 0.35);  // 召唤魔法团的概率
            abi("mythical_creatures_reborn:twilight_sparkle", "summon_count", 3);      // 一次召唤几只
            abi("mythical_creatures_reborn:twilight_sparkle", "summon_cooldown", 120); // 召唤冷却(tick)
            abi("mythical_creatures_reborn:twilight_sparkle", "burst_particles", 28);  // 施法特效粒子数
            // 魔法爆发特效实体（紫悦施法时的紫色冲击波）：纯视觉、不造成伤害
            abi("mythical_creatures_reborn:magic_burst", "life", 16);                  // 特效寿命(tick)
        }

        /** 技能/AI 调参登记（语义同 entity()，只是键不是属性名） */
        static void abi(String id, String key, double v) { ENTITY_DEFAULTS.put(id + "|" + key, v); }

        /* ================================================================
         * 投掷物默认值 | Projectile defaults
         *  投掷物不走 Attribute 体系（伤害是命中时直接 hurt），因此单独一张表。
         *  键格式同实体：{@code 注册名|属性}；读取用 {@code Data#projectileAttr}。
         * ================================================================ */
        public static final Map<String, Double> PROJECTILE_DEFAULTS = new LinkedHashMap<>();

        /** 投掷物通用参数在 overrides 里的注册名 */
        public static final String PROJECTILE_PARAMS = "projectile_params";

        static void projectile(String id, String key, double v) { PROJECTILE_DEFAULTS.put(id + "|" + key, v); }

        static {
            // ── 通用（基类 ModThrowableProjectile 消费）──
            projectile(PROJECTILE_PARAMS, "hit_radius", 1.0);          // 实体命中判定半径
            projectile(PROJECTILE_PARAMS, "mob_shot_lifetime", 200);   // 生物发射时的存活(tick)

            // ── 各投掷物 ──
            projectile("mythical_creatures_reborn:apple_projectile", "damage", 9.0);
            projectile("mythical_creatures_reborn:balloon_projectile", "damage", 5.0);
            projectile("mythical_creatures_reborn:butterfly_projectile", "damage", 6.0);
            projectile("mythical_creatures_reborn:cupcake_projectile", "damage", 8.0);
            projectile("mythical_creatures_reborn:rainbow_cloud", "damage", 7.0);
            projectile("mythical_creatures_reborn:twilight_star", "damage", 13.0);
            projectile("mythical_creatures_reborn:meteor_fireball", "damage", 13.0);
            projectile("mythical_creatures_reborn:phoenix_feather", "damage", 6.0);
            projectile("mythical_creatures_reborn:phoenix_feather", "fire_seconds", 5.0);   // 点燃秒数
            projectile("mythical_creatures_reborn:precious_gem_projectile", "impact_damage", 4.0);
            projectile("mythical_creatures_reborn:precious_gem_projectile", "effect_duration", 60.0); // 效果时长(tick)
            projectile("mythical_creatures_reborn:rainbow_beam", "damage", 8.0);
            projectile("mythical_creatures_reborn:rainbow_beam", "beam_length", 14.0);      // 光束长度
            projectile("mythical_creatures_reborn:rainbow_beam", "beam_size", 0.6);         // 光束粗细
            projectile("mythical_creatures_reborn:rainbow_beam", "damage_interval", 4.0);   // 伤害间隔(tick)
            projectile("mythical_creatures_reborn:rainbow_dash_slash", "damage", 10.0);
            projectile("mythical_creatures_reborn:rainbow_dash_slash", "range", 18.0);      // 斩击射程
            projectile("mythical_creatures_reborn:rainbow_dash_slash", "area_x", 17.0);     // 判定盒 X
            projectile("mythical_creatures_reborn:rainbow_dash_slash", "area_z", 17.0);     // 判定盒 Z
            projectile("mythical_creatures_reborn:rainbow_dash_slash", "area_y_up", 10.0);  // 判定盒 向上
            projectile("mythical_creatures_reborn:rainbow_dash_slash", "area_y_down", 10.0);// 判定盒 向下
            projectile("mythical_creatures_reborn:unstable_item", "magic_damage", 15.0);
            projectile("mythical_creatures_reborn:unstable_item", "frost_trigger_chance", 0.3); // 覆雪触发概率
            projectile("mythical_creatures_reborn:mavis_orb_projectile", "damage", 6.0);        // 梅菲斯之球
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
                "  属性: max_health / move_speed / attack_damage / fly_speed / armor",
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
                    // TOML 允许 nan / inf 字面量，手改配置或恶意网络包都可能带进来；
                    // 非有限值会让属性变成 NaN（实体位置/血量立刻失控），直接忽略并告警。
                    if (!Double.isFinite(val)) {
                        LOGGER.warn("override 数值非有限（NaN/Infinity），已忽略: {} -> {}", target, attr);
                        continue;
                    }
                    parsed.computeIfAbsent(target, k -> new HashMap<>()).put(attr, val);
                    if (list.size() >= 4 && list.get(3) instanceof String c && !c.isEmpty())
                        comments.computeIfAbsent(target, k -> new HashMap<>()).put(attr, c);
                } catch (Exception e) {
                    LOGGER.warn("override 解析失败，已忽略: {} -> {} ({})", target, attr, e.getMessage());
                }
            }
            // 校验 override 目标是否为已知实体（捕获配置拼写错误，避免「静默无效」）
            for (String target : parsed.keySet()) {
                if (target.equals("global_params") || target.equals(D.PROJECTILE_PARAMS)) continue;
                boolean knownEntity = D.ENTITY_DEFAULTS.keySet().stream()
                        .anyMatch(k -> k.startsWith(target + "|"));
                boolean knownProjectile = D.PROJECTILE_DEFAULTS.keySet().stream()
                        .anyMatch(k -> k.startsWith(target + "|"));
                if (!knownEntity && !knownProjectile)
                    LOGGER.warn("override 目标「{}」不在已知实体/投掷物列表中，该条覆盖可能永久无效", target);
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

        /**
         * 获取投掷物数值：优先取玩家 override，否则取内置默认。
         * 与 {@link #entityAttr} 同构（缺失时静默返回 0.0），调用方需保证键已在
         * {@code D.PROJECTILE_DEFAULTS} 登记。
         *
         * Projectile value: override wins, then the built-in default (silently 0.0 if missing).
         */
        public double projectileAttr(String projectileId, String attr) {
            var m = parsed != null ? parsed.get(projectileId) : null;
            if (m != null && m.containsKey(attr))
                return m.get(attr);
            return D.PROJECTILE_DEFAULTS.getOrDefault(projectileId + "|" + attr, 0.0);
        }

        /**
         * 获取投掷物通用参数（{@code projectile_params}）：override 优先，否则回退 fallback。
         * 与 {@link #get} 同构 —— 缺失时返回 fallback，不会静默 0.0。
         */
        public double projectileParam(String attr, double fallback) {
            return get(D.PROJECTILE_PARAMS, attr, fallback);
        }

        /** 投掷物通用 int 参数 */
        public int projectileParamInt(String attr, int fallback) {
            return (int) projectileParam(attr, fallback);
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
            // 第二道闸：网络包入口已拦过一次，这里兜住任何程序化写入（NaN 会让属性彻底失效）。
            if (!Double.isFinite(value)) {
                LOGGER.warn("拒绝写入非有限数值（NaN/Infinity）: {} -> {}", target, attr);
                return;
            }
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

        /**
         * 备份并写回 common.toml 的 overrides（覆盖式重写整个数组，保留其余内容与注释块）。
         * <p>
         * <b>直接写文件而不走 ForgeConfigSpec：</b> {@code ModConfig.save()} 只序列化 nightconfig 的
         * {@code configData}，与 spec 缓存并行写入会互相打架，导致保存失效。
         * 下次进世界 {@code Loading} → {@link #bake()} 重新读文件即生效。
         * </p>
         */
        public void persistIfDirty() {
            ModConfig cfg = MythicalConfig.COMMON_CONFIG;
            if (cfg == null) {
                LOGGER.error("找不到 COMMON 配置，无法将 override 写入 common.toml");
                return;
            }
            Path path = cfg.getFullPath();
            // 先备份，保证任何写坏都能回滚（.bak 与主文件同目录）
            try {
                Files.copy(path, Paths.get(path + ".bak"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.warn("备份 common.toml 失败（{} 未更新）: {}", path + ".bak", e.getMessage());
            }
            try {
                String content = Files.readString(path);
                String replaced = replaceOverridesBlock(content, buildOverridesList());
                Files.writeString(path, replaced);
                LOGGER.info("已将 override 直接写入 common.toml（{} 条）", buildOverridesList().size());
            } catch (IOException e) {
                LOGGER.error("写回 common.toml 失败: {}", e.getMessage());
            }
        }

        /** 将 parsed+comments 序列化为 overrides 数组的 TOML 文本（不含缩进前导） */
        private String buildOverridesText(List<List<Object>> list) {
            StringBuilder sb = new StringBuilder();
            for (List<Object> row : list) {
                if (sb.length() > 0) sb.append(",\n");
                sb.append("    [");
                for (int i = 0; i < row.size(); i++) {
                    if (i > 0) sb.append(", ");
                    Object v = row.get(i);
                    if (v instanceof String s) {
                        sb.append('"').append(s.replace("\\", "\\\\").replace("\"", "\\\"")).append('"');
                    } else if (v instanceof Number n) {
                        // 整数不带小数点，小数保留（避免 100 -> 100.0 之类类型抖动）
                        if (n.doubleValue() == Math.rint(n.doubleValue()))
                            sb.append(n.longValue());
                        else
                            sb.append(n.doubleValue());
                    } else {
                        sb.append('"').append(String.valueOf(v)).append('"');
                    }
                }
                sb.append("]");
            }
            return sb.toString();
        }

        /**
         * 在整份 common.toml 文本里，把 {@code overrides = [...]} 段替换为新数组。
         * 其余所有内容（顶部注释、其他键）原样保留。
         *
         * <p>定位前先把「行首为 # 的注释行」等长遮蔽成空格（{@link #maskCommentLines}）。
         * 顶部示例注释里同样写着 {@code overrides = [} 与 {@code ]}，直接 {@code indexOf} 会命中
         * 注释里的那一对、把注释段当成数组替换掉 —— 写出的数组体因此丢掉 {@code #}，
         * 下次启动解析整个文件就报 {@code Invalid separator ',' in table name}。</p>
         */
        private String replaceOverridesBlock(String content, List<List<Object>> list) {
            String masked = maskCommentLines(content);
            int idx = masked.indexOf("overrides");
            if (idx < 0) {
                // 文件里根本没有 overrides 键（极端情况）：追加到末尾
                return content + "\noverrides = [\n" + buildOverridesText(list) + "\n]\n";
            }
            // 定位 '=' 之后、'[' 开始
            int eq = masked.indexOf('=', idx);
            int open = masked.indexOf('[', eq);
            if (eq < 0 || open < 0) {
                LOGGER.error("common.toml 里 overrides 结构异常，无法定位数组，放弃写入");
                return content;
            }
            // 用括号配对找到匹配的 ']'（只在非注释内容里数括号）
            int depth = 0;
            int close = open;
            for (int i = open; i < masked.length(); i++) {
                char c = masked.charAt(i);
                if (c == '[') depth++;
                else if (c == ']') {
                    depth--;
                    if (depth == 0) { close = i; break; }
                }
            }
            String before = content.substring(0, open + 1);
            String after = content.substring(close);
            String body = buildOverridesText(list);
            return before + "\n" + body + "\n" + after;
        }

        /**
         * 把整份文本里「行首（允许前导空格 / Tab）为 # 的注释行」遮蔽成等长空格，保留换行与全部下标。
         * 用于按索引定位真正的键与方括号时跳过注释内容。
         */
        private static String maskCommentLines(String content) {
            StringBuilder sb = new StringBuilder(content.length());
            int i = 0;
            while (i < content.length()) {
                int nl = content.indexOf('\n', i);
                int end = nl < 0 ? content.length() : nl;
                int p = i;
                while (p < end && (content.charAt(p) == ' ' || content.charAt(p) == '\t')) p++;
                boolean comment = p < end && content.charAt(p) == '#';
                for (int k = i; k < end; k++) sb.append(comment ? ' ' : content.charAt(k));
                if (nl >= 0) sb.append('\n');
                i = nl < 0 ? content.length() : nl + 1;
            }
            return sb.toString();
        }
    }
}
