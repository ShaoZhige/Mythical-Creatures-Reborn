package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.RainbowCentipedeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RainbowCentipedeEntityModel extends GeoModel<RainbowCentipedeEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/giantcentipede.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/giantcentipede.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(RainbowCentipedeEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RainbowCentipedeEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RainbowCentipedeEntity entity) {
        return ANIMATION;
    }
}
