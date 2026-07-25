package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.PhoenixEntityModel;
import com.shao.mythicalcreatures.entity.custom.PhoenixEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PhoenixEntityRenderer extends GeoEntityRenderer<PhoenixEntity> {

    public PhoenixEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PhoenixEntityModel());
    }

}