package com.shao.mythical_creatures_reborn.client.model;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.RobotSombraEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RobotSombraEntityModel extends GeoModel<RobotSombraEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/ponyrobot.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/robot_sombra.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(RobotSombraEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RobotSombraEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RobotSombraEntity entity) {
        return ANIMATION;
    }
}
