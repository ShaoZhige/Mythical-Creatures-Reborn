package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.IronWillEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IronWillEntityModel extends GeoModel<IronWillEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/ironwill.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/ironwill.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(IronWillEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(IronWillEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(IronWillEntity entity) {
        return ANIMATION;
    }
}
