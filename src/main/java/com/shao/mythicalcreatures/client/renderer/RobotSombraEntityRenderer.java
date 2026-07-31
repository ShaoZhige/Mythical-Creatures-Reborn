package com.shao.mythicalcreatures.client.renderer;

import com.shao.mythicalcreatures.client.model.RobotSombraEntityModel;
import com.shao.mythicalcreatures.entity.custom.RobotSombraEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RobotSombraEntityRenderer extends GeoEntityRenderer<RobotSombraEntity> {

    public RobotSombraEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RobotSombraEntityModel());
    }
}
