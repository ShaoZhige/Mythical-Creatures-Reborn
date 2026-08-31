package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.CrabzillaEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CrabzillaEntityModel extends GeoModel<CrabzillaEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/crabzilla.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/crabzilla.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(CrabzillaEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(CrabzillaEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(CrabzillaEntity entity) {
        return ANIMATION;
    }
}
