package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.KingbowserEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KingbowserEntityModel extends GeoModel<KingbowserEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/kingbowser_9000.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/kingbowser_9000.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/kingbowser_9000.animation.json");



    @Override
    public ResourceLocation getModelResource(KingbowserEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(KingbowserEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(KingbowserEntity entity) {
        return ANIMATION;
    }
}
