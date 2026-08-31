package com.shao.mythical_creatures_reborn.client;

import com.shao.mythical_creatures_reborn.client.renderer.CutieMarkCurioRenderer;
import com.shao.mythical_creatures_reborn.item.ModItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosIntegration {

    public static void registerRenderers() {
        CutieMarkCurioRenderer renderer = new CutieMarkCurioRenderer();
        CuriosRendererRegistry.register(ModItems.APPLEJACK_CUTIEMARK.get(), () -> renderer);
        CuriosRendererRegistry.register(ModItems.PINKIE_PIE_CUTIEMARK.get(), () -> renderer);
        CuriosRendererRegistry.register(ModItems.FLUTTERSHY_CUTIEMARK.get(), () -> renderer);
        CuriosRendererRegistry.register(ModItems.RARITY_CUTIEMARK.get(), () -> renderer);
        CuriosRendererRegistry.register(ModItems.RAINBOW_DASH_CUTIEMARK.get(), () -> renderer);
        CuriosRendererRegistry.register(ModItems.TWILIGHT_CUTIEMARK.get(), () -> renderer);
        CuriosRendererRegistry.register(ModItems.HOLY_LIGHT_RADIANCE_CUTIEMARK.get(), () -> renderer);
    }
}
