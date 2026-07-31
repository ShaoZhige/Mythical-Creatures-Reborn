package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.CragadileEntityModel;
import com.shao.mythicalcreatures.entity.custom.CragadileEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CragadileEntityRenderer extends GeoEntityRenderer<CragadileEntity> {

    public CragadileEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CragadileEntityModel());
    }
}
