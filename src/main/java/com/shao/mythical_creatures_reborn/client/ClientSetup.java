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
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.SeparateTransformsModel;
import com.shao.mythical_creatures_reborn.client.model.TooltipPreview3DModel;
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
     * 给 {@code forge:separate_transforms} 物品套一层「按姿势切换视角」的包装，
     * 让「鼠标悬停看 3D、物品栏里仍是 2D」同时成立。
     *
     * <p>背景：Tooltip Overhaul 的悬停预览面板和物品栏都用 {@code ItemDisplayContext.GUI}，
     * 模型层单看上下文分不清两者，{@code perspectives.gui} 只能二选一。
     * 唯一的差异是姿势矩阵 —— 物品栏是纯平移+对角缩放（无旋转），预览面板额外压了自转/俯仰。
     * 于是用 {@link TooltipPreview3DModel} 在「GUI + 姿势带旋转」时改返回 3D 模型。</p>
     *
     * <p>必须等模型烘焙完（{@code bakedTopLevelModels} 成型）才能替换，所以挂在
     * {@link ModelEvent.ModifyBakingResult}；挂在 {@code RegisterAdditional} 上太早，模型还没烘出来。
     * 没装 Tooltip Overhaul 时这段逻辑同样安全：只是「带旋转的 GUI 渲染也走 3D」，不影响物品栏。</p>
     */
    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        installTooltipPreview3D(event, "alicorn_sword", "alicorn_sword_3d");
    }

    /**
     * 把 {@code <itemModel>#inventory} 换成 {@link TooltipPreview3DModel}，悬停预览改用 {@code <preview3dModel>}。
     *
     * <p>取不到模型时静默跳过（例如资源包把模型换了、或这个版本里模型名对不上），
     * 宁可退回原本的 2D 行为，也不要在客户端启动阶段抛异常。</p>
     *
     * @param itemModel     物品在 {@code #inventory} 变体下的模型名（不含命名空间与 {@code #inventory}）
     * @param preview3dModel 悬停预览时要用的 3D 模型名（同上）
     */
    private static void installTooltipPreview3D(ModelEvent.ModifyBakingResult event, String itemModel, String preview3dModel) {
        ModelResourceLocation guiLocation =
                new ModelResourceLocation(new ResourceLocation(MythicalCreaturesMod.MODID, itemModel), "inventory");
        ModelResourceLocation previewLocation =
                new ModelResourceLocation(new ResourceLocation(MythicalCreaturesMod.MODID, preview3dModel), "inventory");

        BakedModel guiModel = event.getModels().get(guiLocation);
        BakedModel previewModel = event.getModels().get(previewLocation);
        if (guiModel == null || previewModel == null) {
            return;
        }
        // 只包装 separate_transforms 模型：普通模型的「视角变体」是烘焙期就固化好的，
        // 运行时换模型没有意义，包装反而多一层无谓调用。
        if (!(guiModel instanceof SeparateTransformsModel.Baked)) {
            return;
        }
        // 资源包重载会重跑烘焙，这里防一手重复包装（虽然新模型是新对象，保险起见）。
        if (guiModel instanceof TooltipPreview3DModel) {
            return;
        }

        event.getModels().put(guiLocation, new TooltipPreview3DModel(guiModel, previewModel));
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
