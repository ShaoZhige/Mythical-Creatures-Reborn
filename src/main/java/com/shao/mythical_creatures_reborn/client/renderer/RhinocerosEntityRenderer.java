package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.RhinocerosEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.RhinocerosEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RhinocerosEntityRenderer extends GeoEntityRenderer<RhinocerosEntity> {

    public RhinocerosEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RhinocerosEntityModel());
    }
}
