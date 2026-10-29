package com.shao.mythical_creatures_reborn.client.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.shao.mythical_creatures_reborn.client.renderer.MagicBurstRenderer;
import com.shao.mythical_creatures_reborn.entity.MagicBurstEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 紫悦魔法爆发的“延迟渲染”队列（仅 Oculus/Iris 光影启用时工作）。
 *
 * 原理（参照 DiexvSword 的 CosmicItemLateRenderQueue）：
 *   常规实体渲染阶段把需要的数据（实体完整 model-view 矩阵、当时的投影矩阵、实体/partialTick）入队，
 *   不立即画；等 RenderLevelStageEvent.AFTER_LEVEL（延迟渲染与合成全部完成后）再用保存的矩阵重画，
 *   直接写主渲染目标，从而避免被 Iris 的延迟管线吞掉发光效果。
 */
public final class MagicBurstLateRenderQueue {

    private static final List<Entry> ENTRIES = new ArrayList<>();

    public static void enqueue(MagicBurstEntity entity, float partialTick, PoseStack poseStack) {
        PoseStack.Pose pose = poseStack.last();
        ENTRIES.add(new Entry(
                entity,
                partialTick,
                new Matrix4f(pose.pose()),
                new Matrix4f(RenderSystem.getModelViewMatrix()),
                new Matrix4f(RenderSystem.getProjectionMatrix())));
    }

    public static void renderAfterLevel() {
        if (ENTRIES.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            ENTRIES.clear();
            return;
        }

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        Matrix4f previousProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        try {
            // 恢复到主渲染目标并重置状态，保证延迟阶段也能正常写出发光层
            mc.getMainRenderTarget().bindWrite(false);
            RenderSystem.disableScissor();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.defaultBlendFunc();

            Iterator<Entry> it = ENTRIES.iterator();
            while (it.hasNext()) {
                Entry e = it.next();
                // 还原常规渲染阶段保存的矩阵，让 CPU 端顶点与 shader 的 ModelViewMat 一致
                modelView.last().pose().set(e.modelView());
                RenderSystem.applyModelViewMatrix();
                RenderSystem.setProjectionMatrix(new Matrix4f(e.projection()), VertexSorting.DISTANCE_TO_ORIGIN);

                PoseStack local = new PoseStack();
                local.last().pose().set(e.pose());
                MagicBurstRenderer.drawBurst(local, buffers, e.entity(), e.partialTick());
                it.remove();
            }
            buffers.endBatch(MagicBurstRenderer.MAGIC_TYPE);
        } finally {
            RenderSystem.setProjectionMatrix(previousProjection, VertexSorting.DISTANCE_TO_ORIGIN);
            modelView.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    private record Entry(MagicBurstEntity entity, float partialTick, Matrix4f pose, Matrix4f modelView, Matrix4f projection) {}

    private MagicBurstLateRenderQueue() {}
}
