package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.AdultMooseEntityModel;
import com.shao.mythicalcreatures.entity.custom.AdultMooseEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AdultMooseEntityRenderer extends GeoEntityRenderer<AdultMooseEntity> {

    public AdultMooseEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new AdultMooseEntityModel());
    }
}
