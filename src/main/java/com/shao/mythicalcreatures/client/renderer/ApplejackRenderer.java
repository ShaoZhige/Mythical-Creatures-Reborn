package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.ApplejackModel;
import com.shao.mythicalcreatures.entity.custom.ApplejackEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ApplejackRenderer extends GeoEntityRenderer<ApplejackEntity> {
    public ApplejackRenderer(EntityRendererProvider.Context ctx) { super(ctx, new ApplejackModel()); }
}
