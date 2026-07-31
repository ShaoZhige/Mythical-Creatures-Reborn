package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.PhoenixEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PhoenixEntityModel extends GeoModel<PhoenixEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/phoenix.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/phoenix.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/phoenix.animation.json");



    @Override
    public ResourceLocation getModelResource(PhoenixEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(PhoenixEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(PhoenixEntity entity) {
        return ANIMATION;
    }
}
