package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.TwilightSparkleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TwilightSparkleModel extends GeoModel<TwilightSparkleEntity> {

    @Override
    public ResourceLocation getModelResource(TwilightSparkleEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/twilight_sparkle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TwilightSparkleEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/twilight_sparkle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TwilightSparkleEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/twilight_sparkle.animation.json");
    }
}
