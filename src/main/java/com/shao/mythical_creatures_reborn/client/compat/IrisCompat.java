package com.shao.mythical_creatures_reborn.client.compat;

import net.minecraftforge.fml.ModList;

/**
 * Oculus / Iris 光影兼容检测。
 *
 * 当玩家启用了光影包（Iris shader pack）时，Minecraft 会切换到延迟渲染管线，
 * 自定义的非标准 RenderType（如本模组的魔法爆发加色层）可能被延迟管线吞掉而不可见。
 * 通过把发光特效推迟到 RenderLevelStageEvent.AFTER_LEVEL 阶段重画来规避。
 *
 * 检测逻辑照搬参考模组 DiexvSword：oculus 模组加载 + 反射 IrisApi.getInstance().isShaderPackInUse()，
 * 不硬依赖 oculus/iris 的类，未安装时零开销。
 */
public final class IrisCompat {

    private static boolean loggedCompatMode;
    private static final boolean OCULUS_LOADED = ModList.get().isLoaded("oculus");

    public static boolean isOculusLoaded() {
        return OCULUS_LOADED;
    }

    public static boolean isShaderPackActive() {
        if (!OCULUS_LOADED) return false;
        try {
            Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object api = apiClass.getMethod("getInstance").invoke(null);
            Object active = apiClass.getMethod("isShaderPackInUse").invoke(api);
            return Boolean.TRUE.equals(active);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }

    public static void logCompatModeOnce() {
        if (!isShaderPackActive() || loggedCompatMode) return;
        loggedCompatMode = true;
        org.apache.logging.log4j.LogManager.getLogger(IrisCompat.class)
                .info("[MythicalCreatures] 检测到 Oculus/Iris 光影包已启用，紫悦魔法爆发改用延迟渲染兼容模式。");
    }

    private IrisCompat() {}
}
