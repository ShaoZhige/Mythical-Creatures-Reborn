package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.MavisEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.MavisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MavisEntityRenderer extends GeoEntityRenderer<MavisEntity> {

    public MavisEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MavisEntityModel());
    }
}
