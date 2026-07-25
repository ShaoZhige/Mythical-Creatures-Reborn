package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.KingbowserEntityModel;
import com.shao.mythicalcreatures.entity.custom.KingbowserEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KingbowserEntityRenderer extends GeoEntityRenderer<KingbowserEntity> {

    public KingbowserEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new KingbowserEntityModel());
    }

}