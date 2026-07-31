package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.ManticoreEntityModel;
import com.shao.mythicalcreatures.entity.custom.ManticoreEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ManticoreEntityRenderer extends GeoEntityRenderer<ManticoreEntity> {

    public ManticoreEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ManticoreEntityModel());
    }
}
