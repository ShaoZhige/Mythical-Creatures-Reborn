package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.ManticoreEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ManticoreEntityModel extends GeoModel<ManticoreEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/manticore.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/manticore.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(ManticoreEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ManticoreEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ManticoreEntity entity) {
        return ANIMATION;
    }
}
