package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.ChiefThunderhoovesEntityModel;
import com.shao.mythicalcreatures.entity.custom.ChiefThunderhoovesEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChiefThunderhoovesEntityRenderer extends GeoEntityRenderer<ChiefThunderhoovesEntity> {

    public ChiefThunderhoovesEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ChiefThunderhoovesEntityModel());
    }
}
