package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.IronWillEntityModel;
import com.shao.mythicalcreatures.entity.custom.IronWillEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IronWillEntityRenderer extends GeoEntityRenderer<IronWillEntity> {

    public IronWillEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new IronWillEntityModel());
    }
}
