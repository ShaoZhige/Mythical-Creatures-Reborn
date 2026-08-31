package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.ToughGuyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ToughGuyEntityModel extends GeoModel<ToughGuyEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/toughguy.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/toughguy.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(ToughGuyEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ToughGuyEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ToughGuyEntity entity) {
        return ANIMATION;
    }
}
