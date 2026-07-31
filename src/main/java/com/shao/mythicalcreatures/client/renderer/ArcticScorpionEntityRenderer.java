package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.ArcticScorpionEntityModel;
import com.shao.mythicalcreatures.entity.custom.ArcticScorpionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ArcticScorpionEntityRenderer extends GeoEntityRenderer<ArcticScorpionEntity> {

    public ArcticScorpionEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ArcticScorpionEntityModel());
    }
}
