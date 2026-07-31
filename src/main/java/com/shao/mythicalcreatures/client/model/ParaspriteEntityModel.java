package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.ParaspriteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ParaspriteEntityModel extends GeoModel<ParaspriteEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/parasprite.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/parasprite.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/parasprite.animation.json");



    @Override
    public ResourceLocation getModelResource(ParaspriteEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ParaspriteEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ParaspriteEntity entity) {
        return ANIMATION;
    }
}
