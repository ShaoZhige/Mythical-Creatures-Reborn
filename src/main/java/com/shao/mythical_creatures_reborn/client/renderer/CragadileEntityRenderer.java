package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.CragadileEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.CragadileEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CragadileEntityRenderer extends GeoEntityRenderer<CragadileEntity> {

    public CragadileEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CragadileEntityModel());
    }
}
