package com.shao.mythical_creatures_reborn.client.gui;

import com.shao.mythical_creatures_reborn.client.CutieMarkConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 可爱标志饰品渲染位置 | Cutie Mark rendering offsets.
 * <p>
 * 左右分栏：左栏「左腿渲染」、右栏「右腿渲染」，各 4 个滑块（缩放 / 前后 / 上下 / 内外）。
 * 分栏后每栏只占 4 个控件的高度，不会再顶到底部按钮。
 * 改值即时生效（热重载），鼠标松开滑块时写回 client.toml。
 */
public class CutieMarkConfigScreen extends Screen {

    private static final int SLIDER_H = 20;
    private static final int BTN_H = 20;
    private static final int PAD = 20;        // 屏幕左右留白
    private static final int COL_GAP = 24;    // 两栏间距
    private static final int COL_MAX = 240;   // 单栏最大宽度
    private static final int BODY_TOP = 42;   // 分组标题行 y
    private static final int VALUES = 4;

    /** 每组 4 项的顺序：缩放 / 前后 / 上下 / 内外 */
    private static final String[] VALUE_KEYS = {"scale", "x", "y", "z"};
    private static final double[][] VALUE_RANGES = {
            {0.001, 1.0}, {-2.0, 2.0}, {-2.0, 2.0}, {-2.0, 2.0}
    };

    private final Screen parent;
    private final List<GroupLabel> groupLabels = new ArrayList<>();

    public CutieMarkConfigScreen(Screen parent) {
        super(Component.translatable("gui.mythical_creatures_reborn.client_config.category.cutie_mark"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // 两栏等宽、整体居中；窗口过窄时按最小宽度兜底
        int colW = Math.max(120, Math.min(COL_MAX, (this.width - PAD * 2 - COL_GAP) / 2));
        int totalW = colW * 2 + COL_GAP;
        int leftX = Math.max(4, this.width / 2 - totalW / 2);
        int rightX = leftX + colW + COL_GAP;

        int sliderTop = BODY_TOP + 18;
        int btnY = this.height - 30;

        // 控件间距自适应：窗口偏矮时收紧，保证最后一项不压到完成按钮
        int gap = 6;
        int avail = btnY - 12 - sliderTop;
        if (avail < SLIDER_H * VALUES + gap * (VALUES - 1)) {
            gap = Math.max(2, (avail - SLIDER_H * VALUES) / (VALUES - 1));
        }

        this.groupLabels.clear();
        addColumn(leftX, colW, sliderTop, gap, true);
        addColumn(rightX, colW, sliderTop, gap, false);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"),
                        b -> this.onClose())
                .pos(this.width / 2 - 100, btnY).size(200, BTN_H).build());
    }

    /** 添加一栏：分组标题 + 4 个滑块 */
    private void addColumn(int x, int colW, int sliderTop, int gap, boolean left) {
        Component title = Component.translatable(left
                ? "gui.mythical_creatures_reborn.client_config.left_leg_render"
                : "gui.mythical_creatures_reborn.client_config.right_leg_render");
        this.groupLabels.add(new GroupLabel(x + colW / 2, BODY_TOP, title));

        ForgeConfigSpec.DoubleValue[] vals = left
                ? new ForgeConfigSpec.DoubleValue[]{
                        CutieMarkConfig.DATA.leftScale, CutieMarkConfig.DATA.leftX,
                        CutieMarkConfig.DATA.leftY, CutieMarkConfig.DATA.leftZ}
                : new ForgeConfigSpec.DoubleValue[]{
                        CutieMarkConfig.DATA.rightScale, CutieMarkConfig.DATA.rightX,
                        CutieMarkConfig.DATA.rightY, CutieMarkConfig.DATA.rightZ};

        for (int i = 0; i < VALUES; i++) {
            int y = sliderTop + i * (SLIDER_H + gap);
            this.addRenderableWidget(new ConfigSlider(x, y, colW, SLIDER_H,
                    Component.translatable("gui.mythical_creatures_reborn.client_config." + VALUE_KEYS[i]),
                    vals[i], VALUE_RANGES[i][0], VALUE_RANGES[i][1]));
        }
    }

    private record GroupLabel(int centerX, int y, Component text) {}

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, 16, 0xFFFFFF);
        for (GroupLabel gl : this.groupLabels) {
            g.drawCenteredString(this.font, gl.text, gl.centerX, gl.y, 0xFFFFFF);
        }
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.parent);
    }

    /** 归一化滑块 → 实际范围值，改值即时生效、松开时写回 client.toml */
    private static class ConfigSlider extends AbstractSliderButton {
        private final ForgeConfigSpec.DoubleValue configValue;
        private final double min;
        private final double max;
        private final Component label;

        ConfigSlider(int x, int y, int width, int height, Component label, ForgeConfigSpec.DoubleValue configValue, double min, double max) {
            super(x, y, width, height, label, (configValue.get() - min) / (max - min));
            this.label = label;
            this.configValue = configValue;
            this.min = min;
            this.max = max;
            this.updateMessage();
        }

        private double actual() {
            return this.min + (this.max - this.min) * this.value;
        }

        @Override
        protected void updateMessage() {
            double v = actual();
            this.configValue.set(v); // 即时生效（热重载配置）
            this.setMessage(Component.literal(this.label.getString() + ": " + String.format(Locale.ROOT, "%.3f", v)));
        }

        @Override
        protected void applyValue() {
            // 值已在 updateMessage 中写入，无需额外处理
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
            super.onRelease(mouseX, mouseY);
            if (CutieMarkConfig.CLIENT_CONFIG != null) CutieMarkConfig.CLIENT_CONFIG.save();
        }
    }
}
