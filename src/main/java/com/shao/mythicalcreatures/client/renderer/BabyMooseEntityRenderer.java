package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.BabyMooseEntityModel;
import com.shao.mythicalcreatures.entity.custom.BabyMooseEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BabyMooseEntityRenderer extends GeoEntityRenderer<BabyMooseEntity> {

    public BabyMooseEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BabyMooseEntityModel());
    }
}
