package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.BlackWidowEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlackWidowEntityModel extends GeoModel<BlackWidowEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/blackwidow.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/blackwidow.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(BlackWidowEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BlackWidowEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BlackWidowEntity entity) {
        return ANIMATION;
    }
}
