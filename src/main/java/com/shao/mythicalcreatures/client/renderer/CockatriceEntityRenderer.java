package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.CockatriceEntityModel;
import com.shao.mythicalcreatures.entity.custom.CockatriceEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CockatriceEntityRenderer extends GeoEntityRenderer<CockatriceEntity> {

    public CockatriceEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CockatriceEntityModel());
    }

}