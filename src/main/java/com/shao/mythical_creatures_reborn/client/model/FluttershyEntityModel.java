package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.FluttershyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FluttershyEntityModel extends GeoModel<FluttershyEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/fluttershy.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/fluttershy.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/fluttershy.animation.json");


    @Override
    public ResourceLocation getModelResource(FluttershyEntity entity) {
        return MODEL;
    }
    @Override
    public ResourceLocation getTextureResource(FluttershyEntity entity) {
        return TEXTURE;
    }
    @Override
    public ResourceLocation getAnimationResource(FluttershyEntity entity) {
        return ANIMATION;
    }
}
