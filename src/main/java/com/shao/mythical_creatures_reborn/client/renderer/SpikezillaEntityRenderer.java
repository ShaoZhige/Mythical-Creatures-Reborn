package com.shao.mythical_creatures_reborn.client.renderer;

import com.shao.mythical_creatures_reborn.client.model.SpikezillaEntityModel;
import com.shao.mythical_creatures_reborn.entity.custom.SpikezillaEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpikezillaEntityRenderer extends GeoEntityRenderer<SpikezillaEntity> {

    public SpikezillaEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SpikezillaEntityModel());
    }

    /** 超大型实体禁用视锥剔除，防止抬头或靠近时模型消失 */
    @Override
    public boolean shouldRender(SpikezillaEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }
}
