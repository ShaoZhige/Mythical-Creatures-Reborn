package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.TwilightSparkleModel;
import com.shao.mythicalcreatures.entity.custom.TwilightSparkleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TwilightSparkleRenderer extends GeoEntityRenderer<TwilightSparkleEntity> {

    public TwilightSparkleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TwilightSparkleModel());
    }
}
