package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.PinkiePieEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.PinkiePieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PinkiePieEntityRenderer extends GeoEntityRenderer<PinkiePieEntity> {
    public PinkiePieEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PinkiePieEntityModel());
    }
}
