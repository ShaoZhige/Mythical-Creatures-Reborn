package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.HolyLightRadianceEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HolyLightRadianceEntityModel extends GeoModel<HolyLightRadianceEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/holy_light_radiance.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/holy_light_radiance.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/holy_light_radiance.animation.json");


    @Override
    public ResourceLocation getModelResource(HolyLightRadianceEntity entity) {
        return MODEL;
    }
    @Override
    public ResourceLocation getTextureResource(HolyLightRadianceEntity entity) {
        return TEXTURE;
    }
    @Override
    public ResourceLocation getAnimationResource(HolyLightRadianceEntity entity) {
        return ANIMATION;
    }
}
