package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.PrinceRutherfordEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PrinceRutherfordEntityModel extends GeoModel<PrinceRutherfordEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/princeyakfur.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/princeyakfur.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(PrinceRutherfordEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(PrinceRutherfordEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(PrinceRutherfordEntity entity) {
        return ANIMATION;
    }
}
