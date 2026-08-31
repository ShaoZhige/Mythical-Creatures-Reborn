package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.ChiefThunderhoovesEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.ChiefThunderhoovesEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChiefThunderhoovesEntityRenderer extends GeoEntityRenderer<ChiefThunderhoovesEntity> {

    public ChiefThunderhoovesEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ChiefThunderhoovesEntityModel());
    }
}
