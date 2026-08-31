package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.CrabzillaEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.CrabzillaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CrabzillaEntityRenderer extends GeoEntityRenderer<CrabzillaEntity> {

    public CrabzillaEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CrabzillaEntityModel());
    }
}
