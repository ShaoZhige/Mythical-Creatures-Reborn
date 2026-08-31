package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.RarityEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RarityEntityModel extends GeoModel<RarityEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/rarity.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/rarity.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/rarity.animation.json");


    @Override
    public ResourceLocation getModelResource(RarityEntity entity) {
        return MODEL;
    }
    @Override
    public ResourceLocation getTextureResource(RarityEntity entity) {
        return TEXTURE;
    }
    @Override
    public ResourceLocation getAnimationResource(RarityEntity entity) {
        return ANIMATION;
    }
}
