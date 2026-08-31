package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.PinkiePieEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PinkiePieEntityModel extends GeoModel<PinkiePieEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/pinkie_pie.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/pinkie_pie.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/pinkie_pie.animation.json");


    @Override
    public ResourceLocation getModelResource(PinkiePieEntity entity) {
        return MODEL;
    }
    @Override
    public ResourceLocation getTextureResource(PinkiePieEntity entity) {
        return TEXTURE;
    }
    @Override
    public ResourceLocation getAnimationResource(PinkiePieEntity entity) {
        return ANIMATION;
    }
}
