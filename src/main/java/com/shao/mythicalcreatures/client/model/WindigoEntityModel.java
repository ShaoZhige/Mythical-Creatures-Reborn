package com.shao.mythicalcreatures.client.model;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.WindigoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WindigoEntityModel extends GeoModel<WindigoEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MythicalCreaturesMod.MODID, "geo/windigo.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/windigo.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MythicalCreaturesMod.MODID, "animations/mod_placeholder.animation.json");



    @Override
    public ResourceLocation getModelResource(WindigoEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(WindigoEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(WindigoEntity entity) {
        return ANIMATION;
    }
}
