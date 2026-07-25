package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.BearEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BearEntityModel extends GeoModel<BearEntity> {

    @Override
    public ResourceLocation getModelResource(BearEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/bear.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BearEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/bear.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BearEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/bear.animation.json");
    }
}
