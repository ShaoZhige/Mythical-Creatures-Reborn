package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.IronWillEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.IronWillEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IronWillEntityRenderer extends GeoEntityRenderer<IronWillEntity> {

    public IronWillEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new IronWillEntityModel());
    }
}
