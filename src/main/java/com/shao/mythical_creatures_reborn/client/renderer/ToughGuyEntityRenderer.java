package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.ToughGuyEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.ToughGuyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ToughGuyEntityRenderer extends GeoEntityRenderer<ToughGuyEntity> {

    public ToughGuyEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ToughGuyEntityModel());
    }
}
