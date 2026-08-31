package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.ParaspriteEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.ParaspriteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ParaspriteEntityRenderer extends GeoEntityRenderer<ParaspriteEntity> {

    public ParaspriteEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ParaspriteEntityModel());
    }

}