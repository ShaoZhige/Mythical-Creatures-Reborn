package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.RarityEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RarityEntityModel extends GeoModel<RarityEntity> {
    @Override
    public ResourceLocation getModelResource(RarityEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/rarity.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(RarityEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/rarity.png");
    }
    @Override
    public ResourceLocation getAnimationResource(RarityEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/rarity.animation.json");
    }
}
