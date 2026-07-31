package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.LeviathanEntityModel;
import com.shao.mythicalcreatures.entity.custom.LeviathanEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LeviathanEntityRenderer extends GeoEntityRenderer<LeviathanEntity> {

    public LeviathanEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new LeviathanEntityModel());
    }
}
