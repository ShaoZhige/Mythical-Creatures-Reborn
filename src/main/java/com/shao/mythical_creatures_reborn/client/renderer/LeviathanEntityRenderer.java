package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.LeviathanEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.LeviathanEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LeviathanEntityRenderer extends GeoEntityRenderer<LeviathanEntity> {

    public LeviathanEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new LeviathanEntityModel());
    }
}
