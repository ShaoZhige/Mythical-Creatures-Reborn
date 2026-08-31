package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.RainbowCentipedeEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.RainbowCentipedeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RainbowCentipedeEntityRenderer extends GeoEntityRenderer<RainbowCentipedeEntity> {

    public RainbowCentipedeEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RainbowCentipedeEntityModel());
    }
}
