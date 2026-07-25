package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.KingbowserEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KingbowserEntityModel extends GeoModel<KingbowserEntity> {

    @Override
    public ResourceLocation getModelResource(KingbowserEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/kingbowser_9000.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KingbowserEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/kingbowser_9000.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KingbowserEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/kingbowser_9000.animation.json");
    }
}
