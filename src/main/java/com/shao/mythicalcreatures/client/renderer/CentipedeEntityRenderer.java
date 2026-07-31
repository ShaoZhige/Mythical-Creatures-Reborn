package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.CentipedeEntityModel;
import com.shao.mythicalcreatures.entity.custom.CentipedeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CentipedeEntityRenderer extends GeoEntityRenderer<CentipedeEntity> {

    public CentipedeEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CentipedeEntityModel());
    }
}
