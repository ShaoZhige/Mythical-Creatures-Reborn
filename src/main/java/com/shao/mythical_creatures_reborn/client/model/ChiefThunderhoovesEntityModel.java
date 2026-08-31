package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.ChiefThunderhoovesEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChiefThunderhoovesEntityModel extends GeoModel<ChiefThunderhoovesEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/chiefthunderhooves.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/cthunderhooves.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(ChiefThunderhoovesEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ChiefThunderhoovesEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ChiefThunderhoovesEntity entity) {
        return ANIMATION;
    }
}
