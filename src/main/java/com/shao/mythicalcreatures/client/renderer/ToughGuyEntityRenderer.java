package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.ToughGuyEntityModel;
import com.shao.mythicalcreatures.entity.custom.ToughGuyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ToughGuyEntityRenderer extends GeoEntityRenderer<ToughGuyEntity> {

    public ToughGuyEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ToughGuyEntityModel());
    }
}
