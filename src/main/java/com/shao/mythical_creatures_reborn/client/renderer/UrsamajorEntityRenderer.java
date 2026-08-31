package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.UrsamajorEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.UrsamajorEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class UrsamajorEntityRenderer extends GeoEntityRenderer<UrsamajorEntity> {

    public UrsamajorEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new UrsamajorEntityModel());
    }

    /** 超大型实体禁用视锥剔除，防止抬头或靠近时模型消失 */
    @Override
    public boolean shouldRender(UrsamajorEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }
}
