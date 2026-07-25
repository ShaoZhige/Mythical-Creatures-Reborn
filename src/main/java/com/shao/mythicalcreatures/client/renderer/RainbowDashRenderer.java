package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.RainbowDashModel;
import com.shao.mythicalcreatures.entity.custom.RainbowDashEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RainbowDashRenderer extends GeoEntityRenderer<RainbowDashEntity> {
    public RainbowDashRenderer(EntityRendererProvider.Context ctx) { super(ctx, new RainbowDashModel()); }
}
