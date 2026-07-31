package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.CrabzillaEntityModel;
import com.shao.mythicalcreatures.entity.custom.CrabzillaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CrabzillaEntityRenderer extends GeoEntityRenderer<CrabzillaEntity> {

    public CrabzillaEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CrabzillaEntityModel());
    }
}
