package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.AdultMooseEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.AdultMooseEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AdultMooseEntityRenderer extends GeoEntityRenderer<AdultMooseEntity> {

    public AdultMooseEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new AdultMooseEntityModel());
    }
}
