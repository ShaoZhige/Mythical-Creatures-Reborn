package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.PhoenixEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PhoenixEntityModel extends GeoModel<PhoenixEntity> {

    @Override
    public ResourceLocation getModelResource(PhoenixEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/phoenix.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PhoenixEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/phoenix.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PhoenixEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/phoenix.animation.json");
    }
}
