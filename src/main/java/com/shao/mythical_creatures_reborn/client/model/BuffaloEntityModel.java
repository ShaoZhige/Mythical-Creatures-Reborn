package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.BuffaloEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BuffaloEntityModel extends GeoModel<BuffaloEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/buffalo.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/buffalo.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(BuffaloEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BuffaloEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BuffaloEntity entity) {
        return ANIMATION;
    }
}
