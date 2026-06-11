package com.shao.mythicalcreatures.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@SuppressWarnings("unchecked")
public class CutieMarkCurioRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack,
            SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> parent,
            MultiBufferSource buffer, int light, float limbSwing, float limbSwingAmount,
            float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        if (!(parent.getModel() instanceof HumanoidModel<?> humanoid)) return;

        ResourceLocation regName = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (regName == null) return;
        ResourceLocation texture = new ResourceLocation(regName.getNamespace(),
                "textures/item/" + regName.getPath() + ".png");

        ModelPart leg = humanoid.leftLeg;

        poseStack.pushPose();
        leg.translateAndRotate(poseStack);

        // 位置：左腿外侧上方（往左边再移出去）
        poseStack.translate(0.15, 0.23, 0.0);

        // 面朝外（左腿外侧，面朝左侧）
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        // 缩放到合适大小（约原来 1/6）
        float s = 0.03F;
        poseStack.scale(s, s, s);

        // 绘制朝外的平面贴图（8x8 世界单位，缩放后约 1 像素）
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        Matrix4f m = poseStack.last().pose();
        Matrix3f n = poseStack.last().normal();
        float half = 4.0F;
        // UV: 左右镜像
        vc.vertex(m, -half,  half, 0).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m,  half,  half, 0).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m,  half, -half, 0).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m, -half, -half, 0).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();

        poseStack.popPose();
    }
}
