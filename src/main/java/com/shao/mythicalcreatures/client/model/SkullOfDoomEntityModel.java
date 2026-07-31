package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.SkullOfDoomEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SkullOfDoomEntityModel extends GeoModel<SkullOfDoomEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/skullofdoom.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/skullofdoom.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(SkullOfDoomEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SkullOfDoomEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(SkullOfDoomEntity entity) {
        return ANIMATION;
    }
}
