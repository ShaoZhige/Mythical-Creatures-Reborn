package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.ArcticScorpionEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ArcticScorpionEntityModel extends GeoModel<ArcticScorpionEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/arcticscorpion.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/arcticscorpion.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(ArcticScorpionEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ArcticScorpionEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ArcticScorpionEntity entity) {
        return ANIMATION;
    }
}
