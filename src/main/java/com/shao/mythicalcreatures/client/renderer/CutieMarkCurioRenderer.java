package com.shao.mythicalcreatures.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.shao.mythicalcreatures.client.CutieMarkConfig;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import top.theillusivec4.curios.api.CuriosApi;
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
        int idx = slotContext.index();

        ResourceLocation texture = textureFor(stack.getItem());
        if (texture == null) return;

        // 是否只有一个可爱标志（仅在第一个槽位需要判断第二个槽，避免每个槽位都查一次 Curios）
        boolean hasSecond = (idx == 0) && hasSlotFilled(slotContext, 1);

        if (idx == 0) {
            // 第一个：永远渲染左腿
            renderSide(humanoid, poseStack, buffer, light, texture, "left");
            // 没有第二个时也渲染右腿（同一个图案）
            if (!hasSecond)
                renderSide(humanoid, poseStack, buffer, light, texture, "right");
        } else if (idx == 1) {
            // 第二个：渲染右腿
            renderSide(humanoid, poseStack, buffer, light, texture, "right");
        }
        // idx >= 2：不渲染
    }

    // 按物品缓存可爱标志纹理（物品为常量，避免每帧 new ResourceLocation + 字符串拼接）
    private static final java.util.Map<net.minecraft.world.item.Item, ResourceLocation> TEXTURE_CACHE =
            new java.util.concurrent.ConcurrentHashMap<>();
    private static ResourceLocation textureFor(net.minecraft.world.item.Item item) {
        ResourceLocation regName = ForgeRegistries.ITEMS.getKey(item);
        if (regName == null) return null;
        return TEXTURE_CACHE.computeIfAbsent(item, k -> new ResourceLocation(
                regName.getNamespace(), "textures/item/" + regName.getPath() + ".png"));
    }

    private static boolean hasSlotFilled(SlotContext ctx, int index) {
        try {
            var inv = CuriosApi.getCuriosInventory(ctx.entity()).resolve();
            if (inv.isPresent()) {
                var curios = inv.get().getCurios();
                var stacks = curios.get("cutie_mark");
                return index < stacks.getSlots() && !stacks.getStacks().getStackInSlot(index).isEmpty();
            }
        } catch (Exception ignored) {}
        return false;
    }

    private static void renderSide(HumanoidModel<?> model, PoseStack ps, MultiBufferSource buf,
                                    int light, ResourceLocation tex, String side) {
        float s, ox, oy, oz;
        if ("left".equals(side)) {
            s = CutieMarkConfig.DATA.leftScale.get().floatValue();
            ox = CutieMarkConfig.DATA.leftX.get().floatValue();
            oy = CutieMarkConfig.DATA.leftY.get().floatValue();
            oz = CutieMarkConfig.DATA.leftZ.get().floatValue();
        } else {
            s = CutieMarkConfig.DATA.rightScale.get().floatValue();
            ox = CutieMarkConfig.DATA.rightX.get().floatValue();
            oy = CutieMarkConfig.DATA.rightY.get().floatValue();
            oz = CutieMarkConfig.DATA.rightZ.get().floatValue();
        }
        renderCutieMark(model, ps, buf, light, tex, side, s, ox, oy, oz);
    }

    /**
     * 在玩家大腿外侧渲染可爱标志。
     *
     * @param side  "left" 贴左腿，"right" 贴右腿；不匹配则静默忽略
     * @param scale 纹理缩放（正方形）
     * @param x     外侧偏移量（正值=向外推，负值=向内推）；右腿自动镜像
     * @param y     垂直偏移
     * @param z     前后偏移
     */
    private static void renderCutieMark(HumanoidModel<?> model, PoseStack poseStack,
                                         MultiBufferSource buffer, int light, ResourceLocation texture,
                                         String side, float scale, float x, float y, float z) {
        final ModelPart leg;
        final boolean isLeft;
        if ("left".equals(side)) {
            leg = model.leftLeg;
            isLeft = true;
        } else if ("right".equals(side)) {
            leg = model.rightLeg;
            isLeft = false;
        } else {
            return; // 非法字符串，不做任何事
        }

        poseStack.pushPose();
        leg.translateAndRotate(poseStack);

        float offsetX = isLeft ? x : -x;
        poseStack.translate(offsetX, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(isLeft ? 90 : -90));

        poseStack.scale(scale, scale, scale);

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        Matrix4f m = poseStack.last().pose();
        Matrix3f n = poseStack.last().normal();
        float half = 4.0F;
        float u0 = isLeft ? 0 : 1;
        float u1 = isLeft ? 1 : 0;
        vc.vertex(m, -half,  half, 0).color(255, 255, 255, 255).uv(u1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m,  half,  half, 0).color(255, 255, 255, 255).uv(u0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m,  half, -half, 0).color(255, 255, 255, 255).uv(u0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m, -half, -half, 0).color(255, 255, 255, 255).uv(u1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0, 0, 1).endVertex();

        poseStack.popPose();
    }
}
