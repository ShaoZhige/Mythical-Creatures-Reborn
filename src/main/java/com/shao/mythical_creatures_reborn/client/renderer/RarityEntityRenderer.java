package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.RarityEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.RarityEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RarityEntityRenderer extends GeoEntityRenderer<RarityEntity> {
    public RarityEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RarityEntityModel());
    }
}
