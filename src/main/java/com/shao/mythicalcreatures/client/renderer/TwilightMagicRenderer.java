package com.shao.mythicalcreatures.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.client.model.TwilightMagicModel;
import com.shao.mythicalcreatures.entity.custom.TwilightMagicEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TwilightMagicRenderer extends MobRenderer<TwilightMagicEntity, TwilightMagicModel<TwilightMagicEntity>> {

	private static final ResourceLocation TEXTURE =
		new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/twilight_magic.png");

	public TwilightMagicRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new TwilightMagicModel<>(ctx.bakeLayer(TwilightMagicModel.LAYER)), 0.3F);
	}

	@Override
	public ResourceLocation getTextureLocation(TwilightMagicEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(TwilightMagicEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.translate(0, 0.5, 0);
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
		poseStack.popPose();
	}
}
