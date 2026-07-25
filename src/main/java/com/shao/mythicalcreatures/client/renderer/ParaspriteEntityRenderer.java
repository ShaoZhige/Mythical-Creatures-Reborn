package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.ParaspriteEntityModel;
import com.shao.mythicalcreatures.entity.custom.ParaspriteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ParaspriteEntityRenderer extends GeoEntityRenderer<ParaspriteEntity> {

    public ParaspriteEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ParaspriteEntityModel());
    }

}