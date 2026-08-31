package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.BuffaloEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.BuffaloEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BuffaloEntityRenderer extends GeoEntityRenderer<BuffaloEntity> {

    public BuffaloEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BuffaloEntityModel());
    }
}
