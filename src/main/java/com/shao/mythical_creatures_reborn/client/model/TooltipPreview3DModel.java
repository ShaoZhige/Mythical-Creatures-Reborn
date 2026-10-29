package com.shao.mythical_creatures_reborn.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.joml.Matrix4f;

/**
 * 让「鼠标悬停看 3D 模型、物品栏里仍是 2D 图标」同时成立。
 *
 * <h2>为什么需要这一层</h2>
 * 天角兽之剑用 {@code forge:separate_transforms} 声明了「GUI 用 2D 图标、其它视角用 3D 模型」：
 * <pre>
 *   alicorn_sword.json
 *     base          -> alicorn_sword_3d（真 3D 模型）
 *     perspectives.gui -> alicorn_sword_2d（16x16 平面贴图）
 * </pre>
 * 麻烦在于 <b>Tooltip Overhaul 的「3D 预览面板」和物品栏用的是同一个 {@link ItemDisplayContext#GUI}</b>
 * （它渲染时硬编码 {@code ItemDisplayContext.GUI}，见其 {@code RenderUtils#renderItem}）。
 * 所以单看显示上下文，模型层根本分不清「现在是物品栏在画」还是「现在是悬停预览面板在画」，
 * {@code perspectives.gui} 只能二选一。
 *
 * <h2>怎么区分</h2>
 * 两者唯一可观测的差异是 <b>姿势矩阵（{@link PoseStack}）</b>：
 * <ul>
 *   <li><b>物品栏</b>：{@code translate(x+8, y+8, 150)} + {@code scale(16, -16, 16)}
 *       —— 3x3 部分是对角阵，<b>非对角项恒为 0</b>；</li>
 *   <li><b>悬停预览面板</b>：Tooltip Overhaul 在画之前额外压了自转
 *       {@code multiply(Axis.YP, θ)} 和俯仰 {@code multiply(Axis.ZP, -45°)}，
 *       再叠加均匀缩放 —— 3x3 部分<b>存在明显的非对角项</b>（量级 1~30）。</li>
 * </ul>
 * 于是「GUI 上下文 + 姿势带旋转」就等价于「这是悬停预览面板」。物品栏的姿势永远不带旋转，不会误判。
 *
 * <h2>行为</h2>
 * <ul>
 *   <li>物品栏 / 创造栏 / JEI 列表图标（姿势无旋转）→ 原样走 2D 图标，观感零变化；</li>
 *   <li>悬停预览面板 → 换成 3D 模型，并应用该模型自己的 {@code display.gui} 变换
 *       （旋转/缩放就写在 {@code alicorn_sword_3d.json} 里，方便实机微调）。</li>
 * </ul>
 *
 * <h2>为什么不改 Tooltip Overhaul</h2>
 * 这是第三方模组，改它的源码会随它更新而失效、也不该由本模组打包分发。
 * 所以兼容层完全落在本模组内：只换「本模组自己那个物品模型」在特定姿势下返回什么，
 * 不触碰、也不依赖 Tooltip Overhaul 的任何类名或方法名。没装它时，本层除了
 * 「带旋转的 GUI 渲染也走 3D」之外不会产生任何影响。
 *
 * @see com.shao.mythical_creatures_reborn.client.ClientSetup#onModifyBakingResult
 */
public class TooltipPreview3DModel extends BakedModelWrapper<BakedModel> {

    /**
     * 判定「姿势矩阵里是否有旋转」的阈值。
     * 物品栏的对角阵非对角项恒为 0（浮点乘法出来的也是 0），而带旋转的姿势非对角项量级在 1~30，
     * 所以 0.5 既足够宽松，又不会把纯缩放/平移误判成旋转。
     */
    private static final float ROTATION_EPSILON = 0.5F;

    /** 悬停预览时改用这个模型（即 {@code separate_transforms} 的 base，真 3D）。 */
    private final BakedModel previewModel;

    public TooltipPreview3DModel(BakedModel guiModel, BakedModel previewModel) {
        super(guiModel);
        this.previewModel = previewModel;
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext ctx, PoseStack pose, boolean leftHand) {
        if (ctx == ItemDisplayContext.GUI && isRotated(pose)) {
            // 悬停预览面板：交给 3D 模型，并由它自己的 display.gui 负责旋转与缩放标定。
            return this.previewModel.applyTransform(ctx, pose, leftHand);
        }
        // 其余一律保持原样（默认即 super -> originalModel.applyTransform -> 2D 图标）。
        return super.applyTransform(ctx, pose, leftHand);
    }

    /**
     * 姿势矩阵的 3x3 部分是否含旋转。
     * <p>
     * 只读 6 个非对角元素。JOML 的 {@code mCR()} 命名（列优先）到底哪一个是行、哪一个是列不影响结论 ——
     * 六个都查了，两种约定下覆盖的集合相同。纯平移 + 对角缩放时这六项全是 0。
     * </p>
     */
    private static boolean isRotated(PoseStack pose) {
        Matrix4f m = pose.last().pose();
        return Math.abs(m.m01()) > ROTATION_EPSILON || Math.abs(m.m10()) > ROTATION_EPSILON
                || Math.abs(m.m02()) > ROTATION_EPSILON || Math.abs(m.m20()) > ROTATION_EPSILON
                || Math.abs(m.m12()) > ROTATION_EPSILON || Math.abs(m.m21()) > ROTATION_EPSILON;
    }
}
