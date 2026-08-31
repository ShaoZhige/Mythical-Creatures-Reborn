package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.RhinocerosEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RhinocerosEntityModel extends GeoModel<RhinocerosEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/rhinoceros.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/rhinoceros.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(RhinocerosEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RhinocerosEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RhinocerosEntity entity) {
        return ANIMATION;
    }
}
