package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.BuffaloEntityModel;
import com.shao.mythicalcreatures.entity.custom.BuffaloEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BuffaloEntityRenderer extends GeoEntityRenderer<BuffaloEntity> {

    public BuffaloEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BuffaloEntityModel());
    }
}
