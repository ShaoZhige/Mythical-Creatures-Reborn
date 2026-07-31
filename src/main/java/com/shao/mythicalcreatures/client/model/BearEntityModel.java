package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.BearEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BearEntityModel extends GeoModel<BearEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/bear.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/bear.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/bear.animation.json");



    @Override
    public ResourceLocation getModelResource(BearEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BearEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BearEntity entity) {
        return ANIMATION;
    }
}
