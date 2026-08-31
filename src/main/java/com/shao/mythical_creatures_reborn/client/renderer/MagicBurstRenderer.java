package com.shao.mythical_creatures_reborn.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.client.compat.IrisCompat;
import com.shao.mythical_creatures_reborn.client.compat.MagicBurstLateRenderQueue;
import com.shao.mythical_creatures_reborn.entity.MagicBurstEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11C;

/**
 * 紫悦魔法爆发渲染：用纯 POSITION_COLOR 顶点画两层 ——
 *   1) 水平扩散的冲击波环（像水面涟漪，从脚下向外扩张并淡出）
 *   2) 朝向相机的发光圆盘（施法瞬间的魔法闪光）
 * 使用加色（additive）混合 + 关闭深度写入，得到发光魔法质感，无需任何贴图 / 着色器文件。
 *
 * 光影兼容：当 Oculus/Iris 启用时，正常渲染阶段只把数据入队、不实际绘制，
 * 改由 MagicBurstRenderEvents 在 AFTER_LEVEL 阶段用保存的矩阵重画（见 MagicBurstLateRenderQueue）。
 */
public class MagicBurstRenderer extends EntityRenderer<MagicBurstEntity> {

    private static final float TWO_PI = (float) (Math.PI * 2.0);

    // RenderStateShard 的内置常量在 1.20.1 是 protected，无法从模组包直接引用，
    // 这里用匿名子类（子类可访问父类 protected 构造）自己构造所需的渲染状态。
    private static final RenderStateShard.TransparencyStateShard ADDITIVE = new RenderStateShard.TransparencyStateShard(
            "magic_additive",
            () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            },
            RenderSystem::disableBlend) { };

    private static final RenderStateShard.DepthTestStateShard ALWAYS_DEPTH =
            new RenderStateShard.DepthTestStateShard("magic_always_depth", GL11C.GL_ALWAYS) { };

    private static final RenderStateShard.WriteMaskStateShard COLOR_ONLY =
            new RenderStateShard.WriteMaskStateShard(true, false) { };

    private static final RenderStateShard.CullStateShard NO_CULL =
            new RenderStateShard.CullStateShard(false) { };

    private static final RenderStateShard.LightmapStateShard NO_LIGHT =
            new RenderStateShard.LightmapStateShard(false) { };

    // POSITION_COLOR_SHADER 常量同样是 protected，用匿名子类取 GameRenderer 的 positionColor shader。
    private static final RenderStateShard.ShaderStateShard POS_COLOR =
            new RenderStateShard.ShaderStateShard(
                    () -> GameRenderer.getPositionColorShader()) { };

    public static final RenderType MAGIC_TYPE = RenderType.create(
            "magic_burst",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLE_STRIP,
            512,
            false, false,
            RenderType.CompositeState.builder()
                    .setTransparencyState(ADDITIVE)
                    .setDepthTestState(ALWAYS_DEPTH)
                    .setWriteMaskState(COLOR_ONLY)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHT)
                    .setShaderState(POS_COLOR)
                    .createCompositeState(false));

    // 紫悦主题色：紫罗兰 / 薰衣草
    private static final int RING_R = 178, RING_G = 102, RING_B = 255;
    private static final int DISC_R = 205, DISC_G = 150, DISC_B = 255;

    private static final float MAX_RADIUS = 2.4F;   // 冲击波最终半径（方块）
    private static final float RING_THICK = 0.32F;  // 环厚度

    public MagicBurstRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(MagicBurstEntity entity, float entityYaw, float partialTicks,
                       PoseStack stack, MultiBufferSource buffer, int packedLight) {
        float t = Math.min(1.0F, (entity.getAge() + partialTicks) / (float) MagicBurstEntity.LIFE);
        if (t <= 0.0F || t >= 1.0F) return;

        if (IrisCompat.isShaderPackActive()) {
            // 光影延迟渲染兼容：常规阶段不画，把实体矩阵/投影矩阵入队，待 AFTER_LEVEL 再用保存矩阵重画
            MagicBurstLateRenderQueue.enqueue(entity, partialTicks, stack);
            return;
        }
        drawBurst(stack, buffer, entity, partialTicks);
    }

    /** 实际绘制魔法爆发。供常规渲染与 Oculus 延迟渲染两种路径共用。 */
    public static void drawBurst(PoseStack stack, MultiBufferSource buffer, MagicBurstEntity entity, float partialTicks) {
        float t = Math.min(1.0F, (entity.getAge() + partialTicks) / (float) MagicBurstEntity.LIFE);
        if (t <= 0.0F || t >= 1.0F) return;

        Matrix4f m = stack.last().pose();
        VertexConsumer v = buffer.getBuffer(MAGIC_TYPE);

        drawShockwaveRing(v, m, t);
        drawGlowDisc(v, m, t, entity);
    }

    /** 水平扩散的冲击波环（XZ 平面） */
    private static void drawShockwaveRing(VertexConsumer v, Matrix4f m, float t) {
        float inner = MAX_RADIUS * t;
        float outer = inner + RING_THICK;
        int a = (int) ((1.0F - t) * 0.75F * 255.0F);
        if (a <= 0) return;

        int seg = 48;
        for (int i = 0; i <= seg; i++) {
            float ang = (float) i / seg * TWO_PI;
            float cx = (float) Math.cos(ang);
            float cz = (float) Math.sin(ang);
            v.vertex(m, cx * inner, 0, cz * inner).color(RING_R, RING_G, RING_B, a).endVertex();
            v.vertex(m, cx * outer, 0, cz * outer).color(RING_R, RING_G, RING_B, a).endVertex();
        }
    }

    /** 朝向相机的发光圆盘（施法闪光） */
    private static void drawGlowDisc(VertexConsumer v, Matrix4f m, float t, MagicBurstEntity entity) {
        int a = (int) ((1.0F - t) * (1.0F - t) * 0.55F * 255.0F);
        if (a <= 0) return;

        float rad = 0.5F + 1.1F * t;

        // 以相机为基准构建 billboard 的 right / up 向量
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 c = entity.position();
        Vec3 toCam = new Vec3(c.x - cam.x, c.y - cam.y, c.z - cam.z).normalize();
        Vec3 right = toCam.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 up = right.cross(toCam).normalize();

        v.vertex(m, 0, 0, 0).color(DISC_R, DISC_G, DISC_B, a).endVertex();
        int dseg = 24;
        for (int i = 0; i <= dseg; i++) {
            float ang = (float) i / dseg * TWO_PI;
            float dx = (float) Math.cos(ang);
            float dz = (float) Math.sin(ang);
            float px = (float) (right.x * dx + up.x * dz) * rad;
            float py = (float) (right.y * dx + up.y * dz) * rad;
            float pz = (float) (right.z * dx + up.z * dz) * rad;
            v.vertex(m, px, py, pz).color(DISC_R, DISC_G, DISC_B, a).endVertex();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(MagicBurstEntity entity) {
        // 纯顶点着色，不依赖任何贴图
        return new ResourceLocation(MythicalCreaturesMod.MODID, "magic_burst");
    }
}
