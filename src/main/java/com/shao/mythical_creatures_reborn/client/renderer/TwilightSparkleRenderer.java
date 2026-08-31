package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.TwilightSparkleModel;
import com.shao.mythical_creatures_reborn.entity.custom.TwilightSparkleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TwilightSparkleRenderer extends GeoEntityRenderer<TwilightSparkleEntity> {

    public TwilightSparkleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TwilightSparkleModel());
    }
}
