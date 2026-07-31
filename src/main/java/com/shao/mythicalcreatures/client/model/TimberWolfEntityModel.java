package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.TimberWolfEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TimberWolfEntityModel extends GeoModel<TimberWolfEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/timberwolf.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/timberwolf.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(TimberWolfEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TimberWolfEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TimberWolfEntity entity) {
        return ANIMATION;
    }
}
