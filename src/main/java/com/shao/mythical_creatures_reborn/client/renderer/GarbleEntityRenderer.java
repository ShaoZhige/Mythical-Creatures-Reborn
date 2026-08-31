package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.GarbleEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.GarbleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GarbleEntityRenderer extends GeoEntityRenderer<GarbleEntity> {

    public GarbleEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GarbleEntityModel());
    }

}