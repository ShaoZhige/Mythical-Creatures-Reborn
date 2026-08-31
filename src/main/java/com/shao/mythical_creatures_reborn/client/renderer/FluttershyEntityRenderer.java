package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.FluttershyEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.FluttershyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FluttershyEntityRenderer extends GeoEntityRenderer<FluttershyEntity> {
    public FluttershyEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new FluttershyEntityModel());
    }
}
