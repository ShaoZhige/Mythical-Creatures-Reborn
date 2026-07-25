package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.RarityEntityModel;
import com.shao.mythicalcreatures.entity.custom.RarityEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RarityEntityRenderer extends GeoEntityRenderer<RarityEntity> {
    public RarityEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RarityEntityModel());
    }
}
