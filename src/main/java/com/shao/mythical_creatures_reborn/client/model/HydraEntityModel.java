package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.HydraEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HydraEntityModel extends GeoModel<HydraEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/hydra.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/hydra.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(HydraEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(HydraEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(HydraEntity entity) {
        return ANIMATION;
    }
}
