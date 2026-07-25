package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.GarbleEntityModel;
import com.shao.mythicalcreatures.entity.custom.GarbleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GarbleEntityRenderer extends GeoEntityRenderer<GarbleEntity> {

    public GarbleEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GarbleEntityModel());
    }

}