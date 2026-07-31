package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.RainbowCentipedeEntityModel;
import com.shao.mythicalcreatures.entity.custom.RainbowCentipedeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RainbowCentipedeEntityRenderer extends GeoEntityRenderer<RainbowCentipedeEntity> {

    public RainbowCentipedeEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RainbowCentipedeEntityModel());
    }
}
