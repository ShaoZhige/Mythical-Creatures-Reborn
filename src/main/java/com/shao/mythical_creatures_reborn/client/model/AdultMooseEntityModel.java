package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.AdultMooseEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AdultMooseEntityModel extends GeoModel<AdultMooseEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/moosebig.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/moosebig.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(AdultMooseEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(AdultMooseEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(AdultMooseEntity entity) {
        return ANIMATION;
    }
}
