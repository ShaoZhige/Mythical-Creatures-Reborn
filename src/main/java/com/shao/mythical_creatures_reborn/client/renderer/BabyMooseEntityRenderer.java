package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.BabyMooseEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.BabyMooseEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BabyMooseEntityRenderer extends GeoEntityRenderer<BabyMooseEntity> {

    public BabyMooseEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BabyMooseEntityModel());
    }
}
