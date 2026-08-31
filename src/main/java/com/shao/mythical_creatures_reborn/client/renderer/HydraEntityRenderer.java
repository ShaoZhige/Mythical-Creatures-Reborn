package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.HydraEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.HydraEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HydraEntityRenderer extends GeoEntityRenderer<HydraEntity> {

    public HydraEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HydraEntityModel());
    }
}
