package com.shao.mythical_creatures_reborn.client.compat;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 监听关卡渲染阶段：当 Oculus/Iris 光影启用时，在 AFTER_LEVEL 阶段重画紫悦魔法爆发。
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class MagicBurstRenderEvents {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == Stage.AFTER_LEVEL && IrisCompat.isShaderPackActive()) {
            IrisCompat.logCompatModeOnce();
            MagicBurstLateRenderQueue.renderAfterLevel();
        }
    }

    private MagicBurstRenderEvents() {}
}
