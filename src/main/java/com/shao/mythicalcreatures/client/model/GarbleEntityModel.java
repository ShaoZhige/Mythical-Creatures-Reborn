package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.GarbleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GarbleEntityModel extends GeoModel<GarbleEntity> {

    @Override
    public ResourceLocation getModelResource(GarbleEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/garble.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GarbleEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/garble.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GarbleEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/garble.animation.json");
    }
}
