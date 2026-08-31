package com.shao.mythical_creatures_reborn.client;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 可爱标志饰品客户端配置 | Cutie Mark client-side rendering config。
 * <p>
 * 此配置在游戏启动、F3+T 刷新资源和热重载时加载，修改后即时生效无需重启。| Loaded on startup, F3+T, and hot reload; takes effect immediately.
 * </p>
 */
public class CutieMarkConfig {

    public static final ForgeConfigSpec SPEC;
    public static final Data DATA;

    static {
        Pair<Data, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Data::new);
        SPEC = pair.getRight();
        DATA = pair.getLeft();
    }

    /** 配置加载事件里捕获的 CLIENT ModConfig 引用，供 GUI 改值后写回 client.toml */
    public static ModConfig CLIENT_CONFIG = null;

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC, "mythical_creatures_reborn/client.toml");
    }

    public static class Data {
        public final ForgeConfigSpec.DoubleValue leftScale, leftX, leftY, leftZ;
        public final ForgeConfigSpec.DoubleValue rightScale, rightX, rightY, rightZ;

        Data(ForgeConfigSpec.Builder b) {
            b.comment("可爱标志饰品渲染位置 — 客户端，修改后即时生效 | Cutie Mark rendering offsets — client-side, takes effect immediately");

            b.comment("左腿 | Left leg").push("left_leg");
            leftScale = b.defineInRange("scale", 0.025, 0.001, 1.0);
            leftX     = b.defineInRange("x",     0.13,  -2.0,  2.0);
            leftY     = b.defineInRange("y",     0.15,  -2.0,  2.0);
            leftZ     = b.defineInRange("z",     0.005, -2.0,  2.0);
            b.pop();

            b.comment("右腿 | Right leg\n" +
                      "x 为外侧偏移，游戏内会自动镜像为负值 | x is the outward offset, auto-mirrored in-game.").push("right_leg");
            rightScale = b.defineInRange("scale", 0.025, 0.001, 1.0);
            rightX     = b.defineInRange("x",     0.13,  -2.0,  2.0);
            rightY     = b.defineInRange("y",     0.15,  -2.0,  2.0);
            rightZ     = b.defineInRange("z",     0.005, -2.0,  2.0);
            b.pop();
        }
    }
}
