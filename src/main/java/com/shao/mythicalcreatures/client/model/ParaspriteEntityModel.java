package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.ParaspriteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ParaspriteEntityModel extends GeoModel<ParaspriteEntity> {

    @Override
    public ResourceLocation getModelResource(ParaspriteEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/parasprite.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ParaspriteEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/parasprite.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ParaspriteEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/parasprite.animation.json");
    }
}
