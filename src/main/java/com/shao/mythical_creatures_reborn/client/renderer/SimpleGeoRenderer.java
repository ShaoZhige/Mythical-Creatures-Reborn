package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.SimpleGeoModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 通用 GeckoLib 实体渲染器：配套 {@link SimpleGeoModel}，按「资源基准名」注册。
 *
 * <p>把原来 30 多个「只有构造器」的渲染器类收敛成一个泛型实现；需要额外行为
 * （例如超大实体禁用视锥剔除）时用 {@link #noCull} 或继承后覆写。</p>
 *
 * <p>Generic GeckoLib entity renderer paired with {@link SimpleGeoModel}; replaces the
 * former per-entity renderer classes that only differed in which model they instantiated.
 * Use {@link #noCull} for giant entities whose model gets frustum-culled too eagerly.</p>
 *
 * @param <T> 该渲染器绘制的实体类型
 */
public class SimpleGeoRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {

    /** 为 true 时跳过视锥剔除（超大实体防止抬头/靠近时模型消失）。 */
    private final boolean cullDisabled;

    public SimpleGeoRenderer(EntityRendererProvider.Context ctx, String baseName) {
        this(ctx, baseName, baseName, false);
    }

    public SimpleGeoRenderer(EntityRendererProvider.Context ctx, String baseName, String animationName) {
        this(ctx, baseName, animationName, false);
    }

    public SimpleGeoRenderer(EntityRendererProvider.Context ctx, String baseName, String animationName, boolean disableCull) {
        super(ctx, new SimpleGeoModel<>(baseName, animationName));
        this.cullDisabled = disableCull;
    }

    /**
     * 禁用视锥剔除的渲染器（超大型实体：雪魔、穗龙斯拉、大熊星座）。
     *
     * <p>动画名必须从 {@code MobCatalog} 传入，不能写死成 {@code baseName}，
     * 否则巨型生物一旦配了独立动画文件，本分支会静默丢掉动画接线。</p>
     *
     * <p>Renderer with frustum culling disabled — for the oversized entities whose model
     * would otherwise pop out of view when you look up or stand next to it.</p>
     */
    public static <T extends Entity & GeoAnimatable> SimpleGeoRenderer<T> noCull(
            EntityRendererProvider.Context ctx, String baseName, String animationName) {
        return new SimpleGeoRenderer<>(ctx, baseName, animationName, true);
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
        return cullDisabled || super.shouldRender(entity, frustum, x, y, z);
    }
}
