package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.HydraEntityModel;
import com.shao.mythicalcreatures.entity.custom.HydraEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HydraEntityRenderer extends GeoEntityRenderer<HydraEntity> {

    public HydraEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HydraEntityModel());
    }
}
