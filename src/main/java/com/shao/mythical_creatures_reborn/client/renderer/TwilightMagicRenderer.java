package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.TwilightMagicModel;
import com.shao.mythical_creatures_reborn.entity.custom.TwilightMagicEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TwilightMagicRenderer extends GeoEntityRenderer<TwilightMagicEntity> {

    public TwilightMagicRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TwilightMagicModel<>());
    }
}
