package com.shao.mythicalcreatures.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.RainbowBeamEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RainbowBeamRenderer extends EntityRenderer<RainbowBeamEntity> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MythicalCreaturesMod.MODID, "textures/entity/rainbow_beam.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);
    private static final float S = 0.30F;  // 柱截面半边长

    public RainbowBeamRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(RainbowBeamEntity beam, float entityYaw, float partialTicks,
                       PoseStack stack, MultiBufferSource buffer, int packedLight) {
        stack.pushPose();

        // 直接读取本地玩家视角（帧级精度，比实体 tick 的旋转更新快一帧）
        Player localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null && localPlayer.getUUID().equals(beam.getOwnerUUID().orElse(null))) {
            float yaw = Mth.rotLerp(partialTicks, localPlayer.yRotO, localPlayer.getYRot());
            float pitch = Mth.lerp(partialTicks, localPlayer.xRotO, localPlayer.getXRot());
            stack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
            stack.mulPose(Axis.XP.rotationDegrees(-pitch));
        } else {
            // 非本地玩家的光束，用实体自身旋转
            stack.mulPose(Axis.YP.rotationDegrees(180.0F - Mth.lerp(partialTicks, beam.yRotO, beam.getYRot())));
            stack.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partialTicks, beam.xRotO, beam.getXRot())));
        }

        float spin = beam.getSpinAngle() + partialTicks * 30.0F;
        stack.mulPose(Axis.ZP.rotationDegrees(spin));

        VertexConsumer v = buffer.getBuffer(RENDER_TYPE);
        Matrix4f m = stack.last().pose();
        Matrix3f n = stack.last().normal();

        float l = RainbowBeamEntity.getBeamLength();
        // 4 faces of a square pillar, extending in model-forward (-Z)
        // MC 实体默认面朝 -Z，配合 180-yaw / -pitch 旋转后即为玩家朝向

        // +Y face (top)
        quad(v, m, n, -S,  S, 0,  S,  S, 0,  S,  S, -l, -S,  S, -l, 180, 0, 1, 0);
        // -Y face (bottom)
        quad(v, m, n, -S, -S, 0, -S, -S, -l,  S, -S, -l,  S, -S, 0, 130, 0, -1, 0);
        // -X face (left)
        quad(v, m, n, -S, -S, 0, -S,  S, 0, -S,  S, -l, -S, -S, -l, 160, -1, 0, 0);
        // +X face (right)
        quad(v, m, n,  S, -S, 0,  S, -S, -l,  S,  S, -l,  S,  S, 0, 160, 1, 0, 0);

        stack.popPose();
    }

    private static void quad(VertexConsumer v, Matrix4f m, Matrix3f n,
                              float x1, float y1, float z1, float x2, float y2, float z2,
                              float x3, float y3, float z3, float x4, float y4, float z4,
                              int a, float nx, float ny, float nz) {
        v.vertex(m, x1, y1, z1).color(255, 255, 255, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(n, nx, ny, nz).endVertex();
        v.vertex(m, x2, y2, z2).color(255, 255, 255, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(n, nx, ny, nz).endVertex();
        v.vertex(m, x3, y3, z3).color(255, 255, 255, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(n, nx, ny, nz).endVertex();
        v.vertex(m, x4, y4, z4).color(255, 255, 255, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(n, nx, ny, nz).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(RainbowBeamEntity entity) {
        return TEXTURE;
    }
}
