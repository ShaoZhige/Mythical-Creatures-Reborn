package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.ApplejackEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ApplejackModel extends GeoModel<ApplejackEntity> {
    @Override public ResourceLocation getModelResource(ApplejackEntity e)     { return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/applejack.geo.json"); }
    @Override public ResourceLocation getTextureResource(ApplejackEntity e)   { return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/apple_jack.png"); }
    @Override public ResourceLocation getAnimationResource(ApplejackEntity e) { return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/applejack.animation.json"); }
}
