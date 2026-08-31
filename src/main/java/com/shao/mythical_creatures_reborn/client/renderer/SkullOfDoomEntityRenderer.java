package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.SkullOfDoomEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.SkullOfDoomEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkullOfDoomEntityRenderer extends GeoEntityRenderer<SkullOfDoomEntity> {

    public SkullOfDoomEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SkullOfDoomEntityModel());
    }
}
