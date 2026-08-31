package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.HolyLightRadianceEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.HolyLightRadianceEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HolyLightRadianceEntityRenderer extends GeoEntityRenderer<HolyLightRadianceEntity> {
    public HolyLightRadianceEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HolyLightRadianceEntityModel());
    }
}
