package com.shao.mythicalcreatures.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.TwilightSparkleEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TwilightSparkleModel<T extends TwilightSparkleEntity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(MythicalCreaturesMod.MODID, "twilight_sparkle"), "main");

    // 身体默认 Y 偏移（由 PartPose.offset 设定）
    private static final float BODY_DEFAULT_Y = 14.0F;

    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart horn;
    private final ModelPart leg_fr_upper, leg_fr_lower;
    private final ModelPart leg_fl_upper, leg_fl_lower;
    private final ModelPart leg_br_upper, leg_br_lower;
    private final ModelPart leg_bl_upper, leg_bl_lower;
    private final ModelPart tail;
    private final ModelPart wing_l, wing_r;

    public TwilightSparkleModel(ModelPart root) {
        this.body = root.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.horn = this.head.getChild("horn");

        this.leg_fr_upper = this.body.getChild("leg_fr_upper");
        this.leg_fr_lower = this.leg_fr_upper.getChild("leg_fr_lower");
        this.leg_fl_upper = this.body.getChild("leg_fl_upper");
        this.leg_fl_lower = this.leg_fl_upper.getChild("leg_fl_lower");
        this.leg_br_upper = this.body.getChild("leg_br_upper");
        this.leg_br_lower = this.leg_br_upper.getChild("leg_br_lower");
        this.leg_bl_upper = this.body.getChild("leg_bl_upper");
        this.leg_bl_lower = this.leg_bl_upper.getChild("leg_bl_lower");

        this.tail = this.body.getChild("tail");
        this.wing_l = this.body.getChild("wing_l");
        this.wing_r = this.body.getChild("wing_r");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // === 身体 (薰衣草紫) ===
        // 放大身体: 8x6x12 像素，更适合 Minecraft 风格
        PartDefinition bodyDef = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -6.0F, -6.0F, 8.0F, 6.0F, 12.0F),
                PartPose.offset(0.0F, BODY_DEFAULT_Y, 0.0F));

        // === 脖子 ===
        PartDefinition neckDef = bodyDef.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(0, 24)
                        .addBox(-2.0F, -5.0F, -2.0F, 4.0F, 5.0F, 4.0F)
                        .texOffs(0, 80)
                        .addBox(-2.0F, -5.0F, 2.0F, 4.0F, 5.0F, 2.0F),
                PartPose.offset(0.0F, -4.0F, -4.0F));

        // === 头部 (主方块 + 口鼻 + 耳朵 + 鬃毛) ===
        PartDefinition headDef = neckDef.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 32)
                        .addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 6.0F)   // 头部主体
                        .texOffs(32, 32)
                        .addBox(-2.0F, -4.0F, -6.0F, 4.0F, 3.0F, 2.0F)    // 口鼻
                        .texOffs(0, 48)
                        .addBox(2.0F, -8.0F, -1.0F, 1.0F, 3.0F, 2.0F)    // 右耳
                        .texOffs(8, 48)
                        .addBox(-3.0F, -8.0F, -1.0F, 1.0F, 3.0F, 2.0F)   // 左耳
                        .texOffs(64, 0)
                        .addBox(-3.0F, -6.0F, -4.5F, 6.0F, 3.0F, 1.0F)   // 鬃毛刘海
                        .texOffs(64, 16)
                        .addBox(-3.0F, -6.0F, 2.0F, 6.0F, 6.0F, 1.0F)    // 后鬃毛
                        .texOffs(64, 32)
                        .addBox(-4.0F, -8.0F, -2.0F, 8.0F, 2.0F, 4.0F),  // 鬃毛顶部
                PartPose.offset(0.0F, -3.0F, 0.0F));

        // === 角 (淡紫色) ===
        headDef.addOrReplaceChild("horn",
                CubeListBuilder.create().texOffs(16, 48)
                        .addBox(-0.5F, -10.0F, -0.5F, 1.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // === 前右腿 ===
        PartDefinition frUp = bodyDef.addOrReplaceChild("leg_fr_upper",
                CubeListBuilder.create().texOffs(0, 96)
                        .addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(-3.0F, 0.0F, -4.0F));
        frUp.addOrReplaceChild("leg_fr_lower",
                CubeListBuilder.create().texOffs(0, 105)
                        .addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F)
                        .texOffs(0, 113)
                        .addBox(-1.5F, 5.0F, -1.5F, 3.0F, 1.0F, 3.0F),   // 蹄
                PartPose.offset(0.0F, 6.0F, 0.0F));

        // === 前左腿 ===
        PartDefinition flUp = bodyDef.addOrReplaceChild("leg_fl_upper",
                CubeListBuilder.create().texOffs(16, 96)
                        .addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(3.0F, 0.0F, -4.0F));
        flUp.addOrReplaceChild("leg_fl_lower",
                CubeListBuilder.create().texOffs(16, 105)
                        .addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F)
                        .texOffs(16, 113)
                        .addBox(-1.5F, 5.0F, -1.5F, 3.0F, 1.0F, 3.0F),
                PartPose.offset(0.0F, 6.0F, 0.0F));

        // === 后右腿 ===
        PartDefinition brUp = bodyDef.addOrReplaceChild("leg_br_upper",
                CubeListBuilder.create().texOffs(32, 96)
                        .addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(-3.0F, 0.0F, 4.0F));
        brUp.addOrReplaceChild("leg_br_lower",
                CubeListBuilder.create().texOffs(32, 105)
                        .addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F)
                        .texOffs(32, 113)
                        .addBox(-1.5F, 5.0F, -1.5F, 3.0F, 1.0F, 3.0F),
                PartPose.offset(0.0F, 6.0F, 0.0F));

        // === 后左腿 ===
        PartDefinition blUp = bodyDef.addOrReplaceChild("leg_bl_upper",
                CubeListBuilder.create().texOffs(48, 96)
                        .addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(3.0F, 0.0F, 4.0F));
        blUp.addOrReplaceChild("leg_bl_lower",
                CubeListBuilder.create().texOffs(48, 105)
                        .addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F)
                        .texOffs(48, 113)
                        .addBox(-1.5F, 5.0F, -1.5F, 3.0F, 1.0F, 3.0F),
                PartPose.offset(0.0F, 6.0F, 0.0F));

        // === 尾巴 (深紫) ===
        bodyDef.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(0, 112)
                        .addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 3.0F)
                        .texOffs(16, 112)
                        .addBox(-1.5F, -1.0F, 2.0F, 3.0F, 4.0F, 4.0F)
                        .texOffs(32, 112)
                        .addBox(-1.0F, 0.0F, 5.0F, 2.0F, 3.0F, 4.0F),
                PartPose.offset(0.0F, -2.0F, 6.0F));

        // === 左翅膀 (白色/淡紫) ===
        bodyDef.addOrReplaceChild("wing_l",
                CubeListBuilder.create().texOffs(64, 48)
                        .addBox(-9.0F, -1.0F, -2.0F, 6.0F, 2.0F, 4.0F)   // 翅膀基底
                        .texOffs(64, 64)
                        .addBox(-6.0F, -0.5F, -1.5F, 4.0F, 1.0F, 3.0F)   // 中段
                        .texOffs(64, 80)
                        .addBox(-3.0F, -0.25F, -1.0F, 3.0F, 0.5F, 2.0F), // 尖端
                PartPose.offset(-2.0F, -4.0F, -2.0F));

        // === 右翅膀 (白色/淡紫) ===
        bodyDef.addOrReplaceChild("wing_r",
                CubeListBuilder.create().texOffs(96, 48)
                        .addBox(3.0F, -1.0F, -2.0F, 6.0F, 2.0F, 4.0F)
                        .texOffs(96, 64)
                        .addBox(2.0F, -0.5F, -1.5F, 4.0F, 1.0F, 3.0F)
                        .texOffs(96, 80)
                        .addBox(0.0F, -0.25F, -1.0F, 3.0F, 0.5F, 2.0F),
                PartPose.offset(2.0F, -4.0F, -2.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

        // ===== 重置所有部位到默认值（防止累加漂移） =====
        body.xRot = 0.0F;
        body.yRot = 0.0F;
        body.zRot = 0.0F;
        body.xScale = 1.0F;
        body.yScale = 1.0F;
        body.zScale = 1.0F;

        neck.xRot = 0.0F;
        neck.yRot = 0.0F;
        neck.zRot = 0.0F;

        head.xRot = 0.0F;
        head.yRot = 0.0F;
        head.zRot = 0.0F;

        tail.xRot = 0.0F;
        tail.yRot = 0.0F;
        tail.zRot = 0.0F;

        wing_l.xRot = 0.0F;
        wing_l.yRot = 0.0F;
        wing_l.zRot = 0.0F;
        wing_r.xRot = 0.0F;
        wing_r.yRot = 0.0F;
        wing_r.zRot = 0.0F;

        for (ModelPart p : new ModelPart[]{leg_fr_upper, leg_fr_lower,
                leg_fl_upper, leg_fl_lower, leg_br_upper, leg_br_lower,
                leg_bl_upper, leg_bl_lower}) {
            p.xRot = 0.0F;
            p.yRot = 0.0F;
            p.zRot = 0.0F;
        }

        // ===== 呼吸浮动（绝对赋值，不累加！） =====
        float breath = ageInTicks * 0.08F;
        body.y = BODY_DEFAULT_Y + Mth.sin(breath) * 0.08F;

        // ===== 头部旋转（跟随视角 + idle 微动） =====
        head.xRot = headPitch * Mth.DEG_TO_RAD;
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;

        float idle = ageInTicks * 0.05F;
        head.xRot += Mth.sin(idle) * 0.03F;
        head.yRot += Mth.cos(idle * 0.7F) * 0.02F;

        // ===== 颈部轻微摆动 =====
        neck.xRot = Mth.sin(breath + 1.0F) * 0.04F;

        // ===== 行走/飞行动画 =====
        float walk1 = Mth.cos(limbSwing * 0.6662F);
        float walk2 = Mth.cos(limbSwing * 0.6662F + Mth.PI);
        float amp = limbSwingAmount * 1.0F;

        boolean isFlying = entity.isFlying();

        if (isFlying) {
            // === 飞行模式 ===
            leg_fr_upper.xRot = 0.5F;
            leg_fl_upper.xRot = 0.5F;
            leg_br_upper.xRot = 0.5F;
            leg_bl_upper.xRot = 0.5F;
            leg_fr_lower.xRot = 0.3F;
            leg_fl_lower.xRot = 0.3F;
            leg_br_lower.xRot = 0.3F;
            leg_bl_lower.xRot = 0.3F;

            float flap = Mth.sin(ageInTicks * 0.8F);
            wing_l.zRot = -0.3F + flap * 0.6F;
            wing_r.zRot = 0.3F - flap * 0.6F;
            wing_l.yRot = -flap * 0.2F;
            wing_r.yRot = flap * 0.2F;

            body.xRot = flap * 0.02F;

            tail.xRot = -0.3F + Mth.sin(ageInTicks * 0.15F) * 0.1F;
            tail.yRot = Mth.sin(ageInTicks * 0.06F) * 0.1F;

        } else {
            // === 地面行走 ===
            leg_fr_upper.xRot = walk1 * 0.6F * amp;
            leg_bl_upper.xRot = walk1 * 0.6F * amp;
            leg_fr_lower.xRot = walk1 * 0.4F * amp;
            leg_bl_lower.xRot = walk1 * 0.4F * amp;

            leg_fl_upper.xRot = walk2 * 0.6F * amp;
            leg_br_upper.xRot = walk2 * 0.6F * amp;
            leg_fl_lower.xRot = walk2 * 0.4F * amp;
            leg_br_lower.xRot = walk2 * 0.4F * amp;

            float wingFlap = Mth.sin(ageInTicks * 0.3F) * 0.12F;
            wing_l.zRot = -0.15F + wingFlap;
            wing_r.zRot = 0.15F - wingFlap;

            tail.xRot = Mth.cos(ageInTicks * 0.08F) * 0.15F;
            tail.yRot = Mth.sin(ageInTicks * 0.06F) * 0.1F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer buf,
                               int light, int overlay,
                               float r, float g, float b, float a) {
        body.render(stack, buf, light, overlay, r, g, b, a);
    }
}
