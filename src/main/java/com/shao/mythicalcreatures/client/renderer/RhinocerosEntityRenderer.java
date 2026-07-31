package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.RhinocerosEntityModel;
import com.shao.mythicalcreatures.entity.custom.RhinocerosEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RhinocerosEntityRenderer extends GeoEntityRenderer<RhinocerosEntity> {

    public RhinocerosEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RhinocerosEntityModel());
    }
}
