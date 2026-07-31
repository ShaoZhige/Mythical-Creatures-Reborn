package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.CrabzillaEntity;
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
