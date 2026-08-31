package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.GarbleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GarbleEntityModel extends GeoModel<GarbleEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/garble.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/garble.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/garble.animation.json");



    @Override
    public ResourceLocation getModelResource(GarbleEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GarbleEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GarbleEntity entity) {
        return ANIMATION;
    }
}
