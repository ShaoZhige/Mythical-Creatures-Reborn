package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.BearEntityModel;
import com.shao.mythicalcreatures.entity.custom.BearEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BearEntityRenderer extends GeoEntityRenderer<BearEntity> {

    public BearEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BearEntityModel());
    }

}