package com.shao.mythical_creatures_reborn.util;

/**
 * GeckoLib 运行环境自检（GeckoLib runtime environment check）。
 *
 * 本模组的生物与渲染器深度依赖 GeckoLib：PonyEntity 实现 GeoEntity，全部模型继承 GeoModel、
 * 渲染器继承 GeoEntityRenderer。一旦 GeckoLib 缺失，首个被加载的相关类就会抛出 NoClassDefFoundError，
 * 崩溃位置随机且难以定位，因此这里在注册任何内容之前先探测核心类，缺失时给出可读的中英文提示。
 *
 * mods.toml 中 geckolib 的依赖声明刻意放宽为可选，使以下几种提供方式都不会被 Forge 的依赖检查拦截：
 * 原生的 Forge 版 GeckoLib，或通过 Sinytra Connector 在 Forge 上运行的 Fabric 版 GeckoLib。
 * 两版 GeckoLib 的公开 API 签名一致（core 层代码共用，平台层差异仅存在于方法内部实现），
 * 因此本模组无需在代码层面区分提供方。
 */
public final class GeckoLibCompat {

    /** 探测用核心类：core 层（两版共用）与平台层（实体基类）各取一个，足以覆盖实际依赖面 */
    private static final String[] REQUIRED_CLASSES = {
            "software.bernie.geckolib.core.animatable.GeoAnimatable",
            "software.bernie.geckolib.animatable.GeoEntity"
    };

    private GeckoLibCompat() {}

    /**
     * 确认 GeckoLib 可用，缺失时以可读的错误中止加载。
     * 需在任意注册动作（会连带加载实体 / 渲染器类）之前调用。
     */
    public static void verifyOrThrow() {
        ClassLoader loader = GeckoLibCompat.class.getClassLoader();

        for (String className : REQUIRED_CLASSES) {
            try {
                // 只加载与链接、不初始化（initialize=false），避免提前触发 GeckoLib 自身的静态初始化
                Class.forName(className, false, loader);
            } catch (ClassNotFoundException | LinkageError e) {
                throw new IllegalStateException(
                        "Mythical Creatures Reborn requires GeckoLib, but it was not found (missing class: "
                                + className + ").\n"
                                + "Please install GeckoLib 4.x - either the Forge build, "
                                + "or the Fabric build running through Sinytra Connector.\n"
                                + "神话生物重制版需要 GeckoLib 才能运行，但未检测到该类：" + className + "。\n"
                                + "请安装 GeckoLib 4.x：Forge 版，或通过 Sinytra Connector 在 Forge 上运行的 Fabric 版。", e);
            }
        }
    }
}
