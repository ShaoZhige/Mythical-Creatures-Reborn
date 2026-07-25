package com.shao.mythicalcreatures.client;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.client.model.TwilightMagicModel;
import com.shao.mythicalcreatures.client.renderer.ApplejackRenderer;
import com.shao.mythicalcreatures.client.renderer.BearEntityRenderer;
import com.shao.mythicalcreatures.client.renderer.CockatriceEntityRenderer;
import com.shao.mythicalcreatures.client.renderer.FluttershyEntityRenderer;
import com.shao.mythicalcreatures.client.renderer.GarbleEntityRenderer;
import com.shao.mythicalcreatures.client.renderer.HolyLightRadianceEntityRenderer;
import com.shao.mythicalcreatures.client.renderer.KingbowserEntityRenderer;
import com.shao.mythicalcreatures.client.renderer.ParaspriteEntityRenderer;
import com.shao.mythicalcreatures.client.renderer.PhoenixEntityRenderer;
import com.shao.mythicalcreatures.client.renderer.PinkiePieEntityRenderer;
import com.shao.mythicalcreatures.client.renderer.RainbowBeamRenderer;
import com.shao.mythicalcreatures.client.renderer.RainbowDashRenderer;
import com.shao.mythicalcreatures.client.renderer.RarityEntityRenderer;
import com.shao.mythicalcreatures.client.renderer.TwilightMagicRenderer;
import com.shao.mythicalcreatures.client.renderer.TwilightSparkleRenderer;
import com.shao.mythicalcreatures.client.renderer.UrsamajorEntityRenderer;
import com.shao.mythicalcreatures.entity.ModEntities;
import com.shao.mythicalcreatures.client.renderer.ScaledThrownItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
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
        event.registerEntityRenderer(ModEntities.RAINBOW_DASH.get(), RainbowDashRenderer::new);
        event.registerEntityRenderer(ModEntities.APPLEJACK.get(), ApplejackRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_DASH_SLASH.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_BEAM.get(), RainbowBeamRenderer::new);
        event.registerEntityRenderer(ModEntities.PHOENIX_FEATHER.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.METEOR_FIREBALL.get(),
                ctx -> new ScaledThrownItemRenderer<>(ctx, 2.0F));

        // 趣味投掷物
        event.registerEntityRenderer(ModEntities.BALLOON_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.BUTTERFLY_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.CUPCAKE_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.TWILIGHT_MAGIC.get(), TwilightMagicRenderer::new);
        event.registerEntityRenderer(ModEntities.BEAR.get(), BearEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.COCKATRICE.get(), CockatriceEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.GARBLE.get(), GarbleEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.KINGBOWSER_9000.get(), KingbowserEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.PARASPRITE.get(), ParaspriteEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.PHOENIX.get(), PhoenixEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.URSA_MAJOR.get(), UrsamajorEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.FLUTTERSHY.get(), FluttershyEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.HOLY_LIGHT_RADIANCE.get(), HolyLightRadianceEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.PINKIE_PIE.get(), PinkiePieEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.RARITY.get(), RarityEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TwilightMagicModel.LAYER, TwilightMagicModel::createBodyLayer);
    }

    /** 注册3D模型，使 forge:separate_transforms 能引用它 */
    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(new ModelResourceLocation(
                new ResourceLocation(MythicalCreaturesMod.MODID, "alicorn_sword_3d"), "inventory"));
        event.register(new ModelResourceLocation(
                new ResourceLocation(MythicalCreaturesMod.MODID, "alicorn_sword_2d"), "inventory"));
        event.register(new ModelResourceLocation(
                new ResourceLocation(MythicalCreaturesMod.MODID, "twilicane_3d"), "inventory"));
        event.register(new ModelResourceLocation(
                new ResourceLocation(MythicalCreaturesMod.MODID, "twilicane_2d"), "inventory"));
        event.register(new ModelResourceLocation(
                new ResourceLocation(MythicalCreaturesMod.MODID, "ursa_claws_3d"), "inventory"));
    }
}
