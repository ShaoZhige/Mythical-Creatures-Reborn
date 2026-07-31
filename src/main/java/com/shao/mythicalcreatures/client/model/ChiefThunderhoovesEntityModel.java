package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.ChiefThunderhoovesEntity;
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
