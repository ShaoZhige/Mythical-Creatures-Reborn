package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.RobotSombraEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.RobotSombraEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RobotSombraEntityRenderer extends GeoEntityRenderer<RobotSombraEntity> {

    public RobotSombraEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RobotSombraEntityModel());
    }
}
