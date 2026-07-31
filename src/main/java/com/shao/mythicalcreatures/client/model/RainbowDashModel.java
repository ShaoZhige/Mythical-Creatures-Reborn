package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.RainbowDashEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RainbowDashModel extends GeoModel<RainbowDashEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/rainbow_dash.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/rainbow_dash.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/rainbow_dash.animation.json");


    @Override public ResourceLocation getModelResource(RainbowDashEntity e)     { return MODEL; }
    @Override public ResourceLocation getTextureResource(RainbowDashEntity e)   { return TEXTURE; }
    @Override public ResourceLocation getAnimationResource(RainbowDashEntity e) { return ANIMATION; }
}
