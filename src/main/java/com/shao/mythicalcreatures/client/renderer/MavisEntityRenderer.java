package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.MavisEntityModel;
import com.shao.mythicalcreatures.entity.custom.MavisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MavisEntityRenderer extends GeoEntityRenderer<MavisEntity> {

    public MavisEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MavisEntityModel());
    }
}
