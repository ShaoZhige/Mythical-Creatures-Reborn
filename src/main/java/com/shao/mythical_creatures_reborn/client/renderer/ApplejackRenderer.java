package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.ApplejackModel;
import com.shao.mythical_creatures_reborn.entity.custom.ApplejackEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ApplejackRenderer extends GeoEntityRenderer<ApplejackEntity> {
    public ApplejackRenderer(EntityRendererProvider.Context ctx) { super(ctx, new ApplejackModel()); }
}
