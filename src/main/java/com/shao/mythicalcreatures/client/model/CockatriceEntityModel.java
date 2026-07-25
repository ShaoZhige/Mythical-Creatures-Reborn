package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.CockatriceEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CockatriceEntityModel extends GeoModel<CockatriceEntity> {

    @Override
    public ResourceLocation getModelResource(CockatriceEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "geo/cockatrice.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CockatriceEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/cockatrice.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CockatriceEntity entity) {
        return new ResourceLocation(MythicalCreaturesMod.MODID, "animations/cockatrice.animation.json");
    }
}
