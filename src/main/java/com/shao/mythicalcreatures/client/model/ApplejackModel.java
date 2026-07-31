package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.ApplejackEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ApplejackModel extends GeoModel<ApplejackEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/applejack.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/apple_jack.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/applejack.animation.json");


    @Override public ResourceLocation getModelResource(ApplejackEntity e)     { return MODEL; }
    @Override public ResourceLocation getTextureResource(ApplejackEntity e)   { return TEXTURE; }
    @Override public ResourceLocation getAnimationResource(ApplejackEntity e) { return ANIMATION; }
}
