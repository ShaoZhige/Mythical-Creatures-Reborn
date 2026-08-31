package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.BearEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.BearEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BearEntityRenderer extends GeoEntityRenderer<BearEntity> {

    public BearEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BearEntityModel());
    }

}