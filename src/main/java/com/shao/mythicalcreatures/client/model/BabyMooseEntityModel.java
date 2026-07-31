package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.BabyMooseEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BabyMooseEntityModel extends GeoModel<BabyMooseEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/moose.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/moose.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(BabyMooseEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BabyMooseEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BabyMooseEntity entity) {
        return ANIMATION;
    }
}
