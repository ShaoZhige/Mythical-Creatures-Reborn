package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.CragadileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CragadileEntityModel extends GeoModel<CragadileEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/cragadile.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/cragadile.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(CragadileEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(CragadileEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(CragadileEntity entity) {
        return ANIMATION;
    }
}
