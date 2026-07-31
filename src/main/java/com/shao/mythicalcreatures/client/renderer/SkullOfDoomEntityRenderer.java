package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.SkullOfDoomEntityModel;
import com.shao.mythicalcreatures.entity.custom.SkullOfDoomEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkullOfDoomEntityRenderer extends GeoEntityRenderer<SkullOfDoomEntity> {

    public SkullOfDoomEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SkullOfDoomEntityModel());
    }
}
