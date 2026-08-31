package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.TwilightSparkleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TwilightSparkleModel extends GeoModel<TwilightSparkleEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/twilight_sparkle.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/twilight_sparkle.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/twilight_sparkle.animation.json");



    @Override
    public ResourceLocation getModelResource(TwilightSparkleEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TwilightSparkleEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TwilightSparkleEntity entity) {
        return ANIMATION;
    }
}
