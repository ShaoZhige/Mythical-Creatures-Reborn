package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.TimberWolfEntityModel;
import com.shao.mythicalcreatures.entity.custom.TimberWolfEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TimberWolfEntityRenderer extends GeoEntityRenderer<TimberWolfEntity> {

    public TimberWolfEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TimberWolfEntityModel());
    }
}
