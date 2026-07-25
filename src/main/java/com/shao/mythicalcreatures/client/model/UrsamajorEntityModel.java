package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.UrsamajorEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UrsamajorEntityModel extends GeoModel<UrsamajorEntity> {

    @Override
    public ResourceLocation getModelResource(UrsamajorEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/ursa_major.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(UrsamajorEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/ursamajor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(UrsamajorEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/ursa_major.animation.json");
    }
}
