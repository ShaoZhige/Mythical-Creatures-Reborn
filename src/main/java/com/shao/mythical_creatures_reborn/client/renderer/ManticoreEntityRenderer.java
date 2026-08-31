package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.ManticoreEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.ManticoreEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ManticoreEntityRenderer extends GeoEntityRenderer<ManticoreEntity> {

    public ManticoreEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ManticoreEntityModel());
    }
}
