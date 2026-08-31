package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.KingbowserEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.KingbowserEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KingbowserEntityRenderer extends GeoEntityRenderer<KingbowserEntity> {

    public KingbowserEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new KingbowserEntityModel());
    }

}