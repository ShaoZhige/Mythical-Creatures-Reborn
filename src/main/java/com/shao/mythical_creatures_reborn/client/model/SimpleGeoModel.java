package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.model.GeoModel;

/**
 * 按「资源基准名」自动拼路径的通用 GeckoLib 模型。
 *
 * <p>约定：{@code geo/<名>.geo.json}、{@code textures/entity/<名>.png}、
 * {@code animations/<名>.animation.json}。只传一个名字时三处同名；
 * 动画要复用别的文件（例如尚未单独制作动画、共用 {@code mod_placeholder}）时，
 * 用两参构造显式指定动画名。</p>
 *
 * <p>为什么不用 GeckoLib 自带的 {@code DefaultedEntityGeoModel}：它把路径拼成
 * {@code geo/entity/<名>.geo.json} 与 {@code animations/entity/<名>.animation.json}（多一层
 * {@code entity/} 子目录），与本项目现有资源布局不一致，直接用会 404。</p>
 *
 * <p>Generic GeckoLib model that derives its resource paths from a single base name.
 * Convention: {@code geo/<name>.geo.json}, {@code textures/entity/<name>.png},
 * {@code animations/<name>.animation.json}. Pass a second argument to share an
 * existing animation file (e.g. {@code mod_placeholder}) instead.</p>
 *
 * @param <T> 该模型服务的可动画对象类型（实体、方块实体、物品皆可）
 */
public class SimpleGeoModel<T extends GeoAnimatable> extends GeoModel<T> {

    /** 兜底动画名：请求的状态没有专属片段时回落到它。 */
    private static final String FALLBACK = "idle";

    private final ResourceLocation modelResource;
    private final ResourceLocation textureResource;
    private final ResourceLocation animationResource;

    /** 三处同名（geo / texture / animation 都叫 {@code baseName}）。 */
    public SimpleGeoModel(String baseName) {
        this(baseName, baseName);
    }

    /**
     * @param baseName      geo 与贴图的基准名
     * @param animationName 动画基准名（可与 {@code baseName} 不同，如共用 {@code mod_placeholder}）
     */
    public SimpleGeoModel(String baseName, String animationName) {
        this.modelResource = resource("geo/" + baseName + ".geo.json");
        this.textureResource = resource("textures/entity/" + baseName + ".png");
        this.animationResource = resource("animations/" + animationName + ".animation.json");
    }

    private static ResourceLocation resource(String path) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, path);
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return textureResource;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return animationResource;
    }

    /**
     * 取动画时的兜底：请求的状态没有专属片段就回落到 {@code idle}。
     *
     * <p>本项目的动画状态机按名字请求片段（{@code idle / walk / run / fly / attack}），
     * 但并非每个模型都做了全部状态 —— 例如只会飞的生物没做 {@code walk}、
     * 末日颅骨只有 {@code idle + fly}。GeckoLib 在找不到名字时的行为是：
     * {@link AnimationProcessor#buildAnimationQueue} 向 {@code System.out} 打印
     * {@code Unable to find animation: ...} 并返回 null，{@code AnimationController#setAnimation}
     * 随即回退到 {@code stop()} —— 控制器退回 STOPPED、模型渲染成绑定姿势。
     * 更糟的是失败时控制器**不会记住**这个名字，于是每一帧都重新尝试一次，
     * 结果是「控制台每帧刷屏 + 生物摆着绑定姿势滑行」。</p>
     *
     * <p>这里把"找不到"改成回落到 {@code idle}：不刷屏、也不会退化成绑定姿势。
     * 缺失的状态只是没有专属动作（观感退化为待机），属于可接受的降级；
     * 想彻底消除降级就得给对应模型补上那段动画。</p>
     *
     * <p>Fall back to {@code idle} when the requested animation name is absent, instead of letting
     * GeckoLib print to stdout and stop the controller every single frame.</p>
     */
    @Override
    public Animation getAnimation(T animatable, String name) {
        Animation animation = super.getAnimation(animatable, name);
        if (animation == null && !FALLBACK.equals(name)) {
            animation = super.getAnimation(animatable, FALLBACK);
        }
        return animation;
    }
}
