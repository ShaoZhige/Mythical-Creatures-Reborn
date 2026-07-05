package com.shao.mythicalcreatures.client;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.client.model.TwilightMagicModel;
import com.shao.mythicalcreatures.client.renderer.RainbowBeamRenderer;
import com.shao.mythicalcreatures.client.renderer.TwilightMagicRenderer;
import com.shao.mythicalcreatures.client.renderer.TwilightSparkleRenderer;
import com.shao.mythicalcreatures.entity.ModEntities;
import com.shao.mythicalcreatures.client.renderer.ScaledThrownItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TWILIGHT_STAR.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_CLOUD.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.APPLE_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.TWILIGHT_SPARKLE.get(), TwilightSparkleRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_DASH_SLASH.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_BEAM.get(), RainbowBeamRenderer::new);
        event.registerEntityRenderer(ModEntities.PHOENIX_FEATHER.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.METEOR_FIREBALL.get(),
                ctx -> new ScaledThrownItemRenderer<>(ctx, 2.0F));
        event.registerEntityRenderer(ModEntities.TWILIGHT_MAGIC.get(), TwilightMagicRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TwilightMagicModel.LAYER, TwilightMagicModel::createBodyLayer);
    }
}
