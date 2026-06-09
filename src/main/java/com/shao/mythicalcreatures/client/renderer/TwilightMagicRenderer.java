package com.shao.mythicalcreatures.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.client.model.TwilightMagicModel;
import com.shao.mythicalcreatures.entity.custom.TwilightMagicEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TwilightMagicRenderer extends MobRenderer<TwilightMagicEntity, TwilightMagicModel<TwilightMagicEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/twilight_magic.png");

    public TwilightMagicRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TwilightMagicModel<>(ctx.bakeLayer(TwilightMagicModel.LAYER)), 0);
    }

    @Override
    public void render(TwilightMagicEntity entity, float yaw, float partialTicks, PoseStack stack,
                        MultiBufferSource buffers, int light) {
        // 透明度渲染
        TwilightMagicModel<TwilightMagicEntity> model = this.getModel();
        model.renderToBuffer(stack,
                buffers.getBuffer(RenderType.entityTranslucent(TEXTURE)),
                light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(TwilightMagicEntity entity) {
        return TEXTURE;
    }
}
