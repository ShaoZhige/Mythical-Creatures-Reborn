package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.RainbowDashEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RainbowDashModel extends GeoModel<RainbowDashEntity> {
    @Override public ResourceLocation getModelResource(RainbowDashEntity e)     { return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/rainbow_dash.geo.json"); }
    @Override public ResourceLocation getTextureResource(RainbowDashEntity e)   { return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/rainbow_dash.png"); }
    @Override public ResourceLocation getAnimationResource(RainbowDashEntity e) { return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/rainbow_dash.animation.json"); }
}
