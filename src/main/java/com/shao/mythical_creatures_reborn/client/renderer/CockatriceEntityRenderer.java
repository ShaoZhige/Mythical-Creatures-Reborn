package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.CockatriceEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.CockatriceEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CockatriceEntityRenderer extends GeoEntityRenderer<CockatriceEntity> {

    public CockatriceEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CockatriceEntityModel());
    }

}