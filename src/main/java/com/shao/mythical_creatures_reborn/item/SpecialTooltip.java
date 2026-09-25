package com.shao.mythical_creatures_reborn.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * 套装 / 特殊装备 / 可爱标志 的 tooltip 渲染。
 *
 * <p>三个约定：效果一行一个（多次 {@code tooltip.add()} —— 翻译文本不认 {@code \n}，不能靠换行符）；
 * 效果文本不用灰色（灰色只留给「按住 Shift」这类提示语）；同类效果颜色统一，颜色集中定义在本类，
 * 不散落在语言文件里。</p>
 */
public final class SpecialTooltip {

    private static final String PREFIX = "tooltip.mythical_creatures_reborn.";

    // 提示语（灰色是合适的：它只是提示，不是效果本身）
    public static final String SET_HINT_KEY     = PREFIX + "set.hint";
    public static final String SPECIAL_HINT_KEY = PREFIX + "special.hint";
    public static final String MARK_HINT_KEY    = PREFIX + "cutiemark.hold_shift";

    /** 标题行颜色。白色不与 {@link Fx} 里任何效果色重复。 */
    private static final ChatFormatting HEADER_COLOR = ChatFormatting.WHITE;

    /**
     * 效果类型 → 统一配色。
     * 所有物品（套装 / 特殊装备 / 可爱标志）的同类效果都取这里的颜色，保证一致。
     */
    public enum Fx {
        ATTACK     (ChatFormatting.RED),            // 攻击伤害
        STRENGTH   (ChatFormatting.RED),            // 力量
        MAX_HEALTH (ChatFormatting.LIGHT_PURPLE),   // 最大生命
        ABSORPTION (ChatFormatting.LIGHT_PURPLE),   // 伤害吸收
        REGEN      (ChatFormatting.GREEN),          // 生命恢复
        SPEED      (ChatFormatting.AQUA),           // 速度 / 飞行
        HASTE      (ChatFormatting.YELLOW),         // 急迫
        JUMP       (ChatFormatting.GREEN),          // 跳跃提升
        REPAIR     (ChatFormatting.LIGHT_PURPLE),   // 修补
        VISION     (ChatFormatting.GOLD),           // 夜视 / 发光
        PROTECT    (ChatFormatting.GOLD),           // 防火 / 水下呼吸
        FOOD       (ChatFormatting.GOLD),           // 饱和
        SPECIAL    (ChatFormatting.DARK_PURPLE);    // 特殊机制

        public final ChatFormatting color;
        Fx(ChatFormatting color) { this.color = color; }
    }

    /** 一行效果：语言键后缀 + 效果类型（决定颜色） */
    private record Line(String key, Fx fx) {}

    private static Line line(String key, Fx fx) { return new Line(key, fx); }

    // ==================== 套装效果表（顺序即显示顺序）====================
    private static final Map<String, List<Line>> SET_EFFECTS = Map.of(
        "apple",        List.of(line("saturation",   Fx.FOOD)),
        "applejack",    List.of(line("strength",     Fx.STRENGTH),
                                line("regen",        Fx.REGEN)),
        "bear_fur",     List.of(line("strength",     Fx.STRENGTH)),
        "bowsers",      List.of(line("max_health",   Fx.MAX_HEALTH),
                                line("fire_res",     Fx.PROTECT)),
        "dark_crystal", List.of(line("night_vision", Fx.VISION),
                                line("attack",       Fx.ATTACK)),
        "fluttershy",   List.of(line("regen",        Fx.REGEN),
                                line("absorption",   Fx.ABSORPTION)),
        "pinkie_pie",   List.of(line("speed",        Fx.SPEED),
                                line("haste",        Fx.HASTE)),
        "rainbow_dash", List.of(line("flight",       Fx.SPEED),
                                line("fall_immune",  Fx.SPEED)),
        "twilight",     List.of(line("max_health",   Fx.MAX_HEALTH))
    );

    // ==================== 特殊装备效果表 ====================
    // 注意：Map.of 最多 10 组键值对，这里 11 组，必须用 Map.ofEntries
    private static final Map<String, List<Line>> SPECIAL_EFFECTS = Map.ofEntries(
        Map.entry("bear_claw_sword",    List.of(line("bleed",     Fx.SPECIAL))),
        Map.entry("bowsers_sword",      List.of(line("meteor",    Fx.SPECIAL))),
        Map.entry("digger",             List.of(line("area",      Fx.SPECIAL))),
        Map.entry("mane_six",           List.of(line("instakill", Fx.SPECIAL))),
        Map.entry("phoenix_bow",        List.of(line("feather",   Fx.SPECIAL))),
        Map.entry("rainbow_dash_sword", List.of(line("cloud",     Fx.SPECIAL),
                                                line("beam",      Fx.SPECIAL))),
        Map.entry("twilicane",          List.of(line("summon",    Fx.SPECIAL))),
        Map.entry("twilight_star",      List.of(line("throw",     Fx.SPECIAL))),
        Map.entry("twilight_sword",     List.of(line("magic",     Fx.SPECIAL),
                                                line("ray",       Fx.SPECIAL))),
        Map.entry("unstable_item",      List.of(line("throw",     Fx.SPECIAL))),
        Map.entry("ursa_claws",         List.of(line("aoe",       Fx.ATTACK),
                                                line("dual",      Fx.SPECIAL)))
    );

    // ==================== 可爱标志效果表 ====================
    private static final Map<String, List<Line>> MARK_EFFECTS = Map.of(
        "applejack",           List.of(line("haste",           Fx.HASTE)),
        "fluttershy",          List.of(line("regen",           Fx.REGEN)),
        "holy_light_radiance", List.of(line("glowing",         Fx.VISION)),
        "pinkie_pie",          List.of(line("jump",            Fx.JUMP)),
        "rainbow_dash",        List.of(line("speed",           Fx.SPEED)),
        "rarity",              List.of(line("repair",          Fx.REPAIR)),
        "twilight",            List.of(line("water_breathing", Fx.PROTECT),
                                       line("night_vision",    Fx.VISION))
    );

    private SpecialTooltip() {}

    /** 逐行追加：标题一行 + 每个效果各一行（关键：每行一次 tooltip.add） */
    private static void appendLines(List<Component> tooltip, String headerKey,
                                    String baseKey, List<Line> lines) {
        tooltip.add(Component.translatable(headerKey).withStyle(HEADER_COLOR));
        for (Line l : lines) {
            tooltip.add(Component.translatable(baseKey + "." + l.key())
                    .withStyle(l.fx().color));
        }
    }

    /** 套装：Shift 按下逐行显示效果，否则显示灰色提示 */
    public static void appendSet(ItemStack stack, String setId, List<Component> tooltip) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(SET_HINT_KEY).withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        List<Line> lines = SET_EFFECTS.get(setId);
        if (lines == null) {
            // 未登记的套装：退回整段文本，至少不丢信息
            tooltip.add(Component.translatable(PREFIX + "set." + setId + ".detail")
                    .withStyle(ChatFormatting.GOLD));
            return;
        }
        appendLines(tooltip, PREFIX + "set.header", PREFIX + "set." + setId + ".effect", lines);
    }

    /** 特殊装备 / 武器 */
    public static void appendSpecial(String detailKey, ItemStack stack, List<Component> tooltip) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(SPECIAL_HINT_KEY).withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        List<Line> lines = SPECIAL_EFFECTS.get(detailKey);
        if (lines == null) {
            tooltip.add(Component.translatable(PREFIX + "special." + detailKey + ".detail")
                    .withStyle(ChatFormatting.DARK_PURPLE));
            return;
        }
        appendLines(tooltip, PREFIX + "special.header", PREFIX + "special." + detailKey + ".effect", lines);
    }

    /** 可爱标志（台词行由调用方先加，这里只管效果） */
    public static void appendMark(String markId, List<Component> tooltip) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(MARK_HINT_KEY).withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        List<Line> lines = MARK_EFFECTS.get(markId);
        if (lines == null) {
            tooltip.add(Component.translatable(PREFIX + "cutiemark." + markId + ".detail")
                    .withStyle(ChatFormatting.GOLD));
            return;
        }
        appendLines(tooltip, PREFIX + "cutiemark.header", PREFIX + "cutiemark." + markId + ".effect", lines);
    }
}
