package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.UrsamajorEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UrsamajorEntityModel extends GeoModel<UrsamajorEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/ursa_major.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/ursamajor.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/ursa_major.animation.json");



    @Override
    public ResourceLocation getModelResource(UrsamajorEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(UrsamajorEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(UrsamajorEntity entity) {
        return ANIMATION;
    }
}
