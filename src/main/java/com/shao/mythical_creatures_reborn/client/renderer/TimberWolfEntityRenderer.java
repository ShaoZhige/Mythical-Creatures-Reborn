package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.TimberWolfEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.TimberWolfEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TimberWolfEntityRenderer extends GeoEntityRenderer<TimberWolfEntity> {

    public TimberWolfEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TimberWolfEntityModel());
    }
}
