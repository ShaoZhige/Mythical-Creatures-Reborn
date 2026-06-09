package com.shao.mythicalcreatures.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.shao.mythicalcreatures.entity.custom.TwilightMagicEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class TwilightMagicModel<T extends TwilightMagicEntity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation("mythicalcreatures", "twilight_magic"), "main");

    private final ModelPart core;
    private final ModelPart ring;

    public TwilightMagicModel(ModelPart root) {
        this.core = root.getChild("core");
        this.ring = root.getChild("ring");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 核心立方体（大尺寸）
        root.addOrReplaceChild("core",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-6, -6, -6, 12, 12, 12),
                PartPose.offset(0, 0, 0));

        // 外圈光环
        root.addOrReplaceChild("ring",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-8, -8, -8, 16, 16, 16),
                PartPose.offset(0, 0, 0));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        // 旋转动画
        core.yRot = ageInTicks * 0.15F;
        core.xRot = ageInTicks * 0.1F;
        ring.yRot = -ageInTicks * 0.08F;
        ring.xRot = ageInTicks * 0.06F;
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer buf, int light, int overlay,
                                float r, float g, float b, float a) {
        core.render(stack, buf, light, overlay, r, g, b, a * 0.6F);
        ring.render(stack, buf, light, overlay, r, g, b, a * 0.25F);
    }
}
