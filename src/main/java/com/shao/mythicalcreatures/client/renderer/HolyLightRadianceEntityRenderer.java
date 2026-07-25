package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.HolyLightRadianceEntityModel;
import com.shao.mythicalcreatures.entity.custom.HolyLightRadianceEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HolyLightRadianceEntityRenderer extends GeoEntityRenderer<HolyLightRadianceEntity> {
    public HolyLightRadianceEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HolyLightRadianceEntityModel());
    }
}
