package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.TwilightMagicModel;
import com.shao.mythicalcreatures.entity.custom.TwilightMagicEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TwilightMagicRenderer extends GeoEntityRenderer<TwilightMagicEntity> {

    public TwilightMagicRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TwilightMagicModel<>());
    }
}
