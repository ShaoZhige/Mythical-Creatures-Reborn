package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.RainbowDashModel;
import com.shao.mythical_creatures_reborn.entity.custom.RainbowDashEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RainbowDashRenderer extends GeoEntityRenderer<RainbowDashEntity> {
    public RainbowDashRenderer(EntityRendererProvider.Context ctx) { super(ctx, new RainbowDashModel()); }
}
