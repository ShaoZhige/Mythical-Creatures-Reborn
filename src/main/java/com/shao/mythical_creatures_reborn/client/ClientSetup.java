package com.shao.mythical_creatures_reborn.client;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.client.model.TwilightMagicModel;
import com.shao.mythical_creatures_reborn.client.renderer.ApplejackRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.BearEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.CockatriceEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.FluttershyEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.GarbleEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.HolyLightRadianceEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.KingbowserEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.ParaspriteEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.PhoenixEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.PinkiePieEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.RainbowBeamRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.MagicBurstRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.RainbowDashRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.RarityEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.TwilightMagicRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.TwilightSparkleRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.UrsamajorEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.BuffaloEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.ChiefThunderhoovesEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.BlackWidowEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.LeviathanEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.CentipedeEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.HydraEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.WindigoEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.BabyMooseEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.AdultMooseEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.ToughGuyEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.MavisEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.ManticoreEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.RainbowCentipedeEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.ArcticScorpionEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.TimberWolfEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.CrabzillaEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.IronWillEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.SkullOfDoomEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.PrinceRutherfordEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.SpikezillaEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.RhinocerosEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.RobotSombraEntityRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.CragadileEntityRenderer;
import com.shao.mythical_creatures_reborn.entity.ModEntities;
import com.shao.mythical_creatures_reborn.client.renderer.ScaledThrownItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import com.shao.mythical_creatures_reborn.client.gui.MainConfigScreen;

@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TWILIGHT_STAR.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.UNSTABLE_ITEM.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_CLOUD.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.APPLE_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.TWILIGHT_SPARKLE.get(), TwilightSparkleRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_DASH.get(), RainbowDashRenderer::new);
        event.registerEntityRenderer(ModEntities.APPLEJACK.get(), ApplejackRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_DASH_SLASH.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_BEAM.get(), RainbowBeamRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_BURST.get(), MagicBurstRenderer::new);
        event.registerEntityRenderer(ModEntities.PHOENIX_FEATHER.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.METEOR_FIREBALL.get(),
                ctx -> new ScaledThrownItemRenderer<>(ctx, 2.0F));

        // 趣味投掷物
        event.registerEntityRenderer(ModEntities.BALLOON_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.BUTTERFLY_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.CUPCAKE_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.PRECIOUS_GEM_PROJECTILE.get(), ThrownItemRenderer::new);
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
        event.registerEntityRenderer(ModEntities.BUFFALO.get(), BuffaloEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.CHIEF_THUNDERHOOVES.get(), ChiefThunderhoovesEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.BLACK_WIDOW_SPIDER.get(), BlackWidowEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.LEVIATHAN.get(), LeviathanEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.CENTIPEDE.get(), CentipedeEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.HYDRA.get(), HydraEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.WINDIGO.get(), WindigoEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.BABY_MOOSE.get(), BabyMooseEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.ADULT_MOOSE.get(), AdultMooseEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.TOUGH_GUY.get(), ToughGuyEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.MAVIS.get(), MavisEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.MANTICORE.get(), ManticoreEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_CENTIPEDE.get(), RainbowCentipedeEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.ARCTIC_SCORPION.get(), ArcticScorpionEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.TIMBER_WOLF.get(), TimberWolfEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.CRABZILLA.get(), CrabzillaEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.IRON_WILL.get(), IronWillEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.SKULL_OF_DOOM.get(), SkullOfDoomEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.PRINCE_RUTHERFORD.get(), PrinceRutherfordEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.SPIKEZILLA.get(), SpikezillaEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.RHINOCEROS.get(), RhinocerosEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.ROBOT_SOMBRA.get(), RobotSombraEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.CRAGADILE.get(), CragadileEntityRenderer::new);
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

    /**
     * 注册配置屏幕工厂：让模组列表的「配置」按钮（以及 Configured/Catalogue 等）打开本模组的
     * 主配置界面。Configured / Forge Config Screens 检测到已有自定义工厂时会自动让位。
     * Register a config screen factory so the mod list config button opens the main config screen.
     */
    @SubscribeEvent
    public static void registerConfigScreen(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, prevScreen) -> new MainConfigScreen(prevScreen)));
    }
}
