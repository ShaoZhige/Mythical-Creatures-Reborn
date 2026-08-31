package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.PrinceRutherfordEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.PrinceRutherfordEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PrinceRutherfordEntityRenderer extends GeoEntityRenderer<PrinceRutherfordEntity> {

    public PrinceRutherfordEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PrinceRutherfordEntityModel());
    }
}
