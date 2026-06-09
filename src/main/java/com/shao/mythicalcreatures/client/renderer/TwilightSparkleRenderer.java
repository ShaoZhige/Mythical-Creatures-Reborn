package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.client.model.TwilightSparkleModel;
import com.shao.mythicalcreatures.entity.custom.TwilightSparkleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TwilightSparkleRenderer extends MobRenderer<TwilightSparkleEntity, TwilightSparkleModel<TwilightSparkleEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/twilight_sparkle.png");

    public TwilightSparkleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TwilightSparkleModel<>(ctx.bakeLayer(TwilightSparkleModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TwilightSparkleEntity entity) {
        return TEXTURE;
    }
}
