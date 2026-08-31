package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.CockatriceEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CockatriceEntityModel extends GeoModel<CockatriceEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/cockatrice.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/cockatrice.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/cockatrice.animation.json");



    @Override
    public ResourceLocation getModelResource(CockatriceEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(CockatriceEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(CockatriceEntity entity) {
        return ANIMATION;
    }
}
