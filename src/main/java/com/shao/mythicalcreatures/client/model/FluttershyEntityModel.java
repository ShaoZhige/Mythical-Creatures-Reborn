package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.FluttershyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FluttershyEntityModel extends GeoModel<FluttershyEntity> {
    @Override
    public ResourceLocation getModelResource(FluttershyEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/fluttershy.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(FluttershyEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/fluttershy.png");
    }
    @Override
    public ResourceLocation getAnimationResource(FluttershyEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/fluttershy.animation.json");
    }
}
