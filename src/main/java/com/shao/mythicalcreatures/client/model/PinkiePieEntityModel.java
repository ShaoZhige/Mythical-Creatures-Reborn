package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.PinkiePieEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PinkiePieEntityModel extends GeoModel<PinkiePieEntity> {
    @Override
    public ResourceLocation getModelResource(PinkiePieEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/pinkie_pie.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(PinkiePieEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/pinkie_pie.png");
    }
    @Override
    public ResourceLocation getAnimationResource(PinkiePieEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/pinkie_pie.animation.json");
    }
}
