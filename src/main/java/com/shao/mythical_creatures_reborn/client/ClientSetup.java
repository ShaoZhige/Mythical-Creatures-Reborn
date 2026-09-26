package com.shao.mythical_creatures_reborn.client;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.client.renderer.MagicBurstRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.RainbowBeamRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.ScaledThrownItemRenderer;
import com.shao.mythical_creatures_reborn.client.renderer.SimpleGeoRenderer;
import com.shao.mythical_creatures_reborn.entity.MobCatalog;
import com.shao.mythical_creatures_reborn.entity.ModEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
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
import software.bernie.geckolib.core.animatable.GeoAnimatable;

@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    /**
     * 实体渲染器注册。
     *
     * <p>投掷物 / 特效仍逐个登记（它们不是 GeckoLib 生物）；所有 GeckoLib 生物统一由
     * {@link MobCatalog#ALL} 驱动，渲染资源名（geo / 贴图 / 动画）都写在元数据表里，
     * 这里不再逐个写死。</p>
     */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // ── 投掷物 / 视觉特效（无 GeckoLib 模型，走原版 ThrownItemRenderer）──
        event.registerEntityRenderer(ModEntities.TWILIGHT_STAR.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.UNSTABLE_ITEM.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_CLOUD.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.APPLE_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_DASH_SLASH.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.RAINBOW_BEAM.get(), RainbowBeamRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_BURST.get(), MagicBurstRenderer::new);
        event.registerEntityRenderer(ModEntities.PHOENIX_FEATHER.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.METEOR_FIREBALL.get(),
                ctx -> new ScaledThrownItemRenderer<>(ctx, 2.0F));
        event.registerEntityRenderer(ModEntities.BALLOON_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.BUTTERFLY_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.CUPCAKE_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.PRECIOUS_GEM_PROJECTILE.get(), ThrownItemRenderer::new);

        // ── 生物：由 MobCatalog 元数据表驱动（新增生物不需要动这里）──
        for (MobCatalog.MobDef<?> mob : MobCatalog.ALL) {
            registerMobRenderer(event, mob);
        }
    }

    /** 捕获通配符：把 {@link MobCatalog.Mob} 的具体实体类型传进泛型的 {@code registerEntityRenderer}。 */
    private static <T extends Mob & GeoAnimatable> void registerMobRenderer(
            EntityRenderersEvent.RegisterRenderers event, MobCatalog.MobDef<T> mob) {
        EntityType<T> type = mob.type().get();
        String base = mob.renderBase();
        String animation = mob.animation();
        float scale = mob.renderScale();
        event.registerEntityRenderer(type, ctx -> {
            SimpleGeoRenderer<T> renderer = mob.cullDisabled()
                    ? SimpleGeoRenderer.noCull(ctx, base, animation)
                    : new SimpleGeoRenderer<>(ctx, base, animation);
            // 小型生物：只缩放渲染，geo / 贴图 / UV / 动画全部不动
            if (scale != 1.0F) {
                renderer.withScale(scale);
            }
            return renderer;
        });
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
