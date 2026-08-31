package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.BlackWidowEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.BlackWidowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlackWidowEntityRenderer extends GeoEntityRenderer<BlackWidowEntity> {

    public BlackWidowEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BlackWidowEntityModel());
    }
}
