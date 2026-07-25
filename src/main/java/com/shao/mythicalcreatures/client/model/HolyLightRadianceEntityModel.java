package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.HolyLightRadianceEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HolyLightRadianceEntityModel extends GeoModel<HolyLightRadianceEntity> {
    @Override
    public ResourceLocation getModelResource(HolyLightRadianceEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/holy_light_radiance.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(HolyLightRadianceEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/holy_light_radiance.png");
    }
    @Override
    public ResourceLocation getAnimationResource(HolyLightRadianceEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/holy_light_radiance.animation.json");
    }
}
