package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.ToughGuyEntity;
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
