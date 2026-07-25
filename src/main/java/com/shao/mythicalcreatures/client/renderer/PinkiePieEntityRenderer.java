package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.PinkiePieEntityModel;
import com.shao.mythicalcreatures.entity.custom.PinkiePieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PinkiePieEntityRenderer extends GeoEntityRenderer<PinkiePieEntity> {
    public PinkiePieEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PinkiePieEntityModel());
    }
}
