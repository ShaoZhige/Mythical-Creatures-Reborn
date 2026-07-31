package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.PrinceRutherfordEntityModel;
import com.shao.mythicalcreatures.entity.custom.PrinceRutherfordEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PrinceRutherfordEntityRenderer extends GeoEntityRenderer<PrinceRutherfordEntity> {

    public PrinceRutherfordEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PrinceRutherfordEntityModel());
    }
}
