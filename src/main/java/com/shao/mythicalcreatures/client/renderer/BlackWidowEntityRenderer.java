package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.BlackWidowEntityModel;
import com.shao.mythicalcreatures.entity.custom.BlackWidowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlackWidowEntityRenderer extends GeoEntityRenderer<BlackWidowEntity> {

    public BlackWidowEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BlackWidowEntityModel());
    }
}
