package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.CentipedeEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.CentipedeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CentipedeEntityRenderer extends GeoEntityRenderer<CentipedeEntity> {

    public CentipedeEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CentipedeEntityModel());
    }
}
