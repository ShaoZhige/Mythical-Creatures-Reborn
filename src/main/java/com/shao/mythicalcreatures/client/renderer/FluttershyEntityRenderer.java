package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.FluttershyEntityModel;
import com.shao.mythicalcreatures.entity.custom.FluttershyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FluttershyEntityRenderer extends GeoEntityRenderer<FluttershyEntity> {
    public FluttershyEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new FluttershyEntityModel());
    }
}
