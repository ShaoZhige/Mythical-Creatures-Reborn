package com.shao.mythical_creatures_reborn.client.gui;

import com.shao.mythical_creatures_reborn.client.CutieMarkConfig;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Locale;

/**
 * 客户端配置界面 | Client config screen.
 * <p>
 * 编辑可爱标志饰品的渲染偏移（scale/x/y/z，左右腿各一组）。这些是 CLIENT 配置，
 * 改值即时生效（热重载），鼠标松开滑块时写回 client.toml。
 */
public class CutieMarkConfigScreen extends Screen {

    private static final int SLIDER_W = 220;
    private static final int SLIDER_H = 20;
    private static final int LEFT_X = 40;

    private final Screen parent;

    public CutieMarkConfigScreen(Screen parent) {
        super(Component.translatable("gui.mythical_creatures_reborn.client_config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = LEFT_X;
        int y = 32;

        // 左腿 | Left leg
        addLabel("gui.mythical_creatures_reborn.client_config.left_leg", y);
        y += 16;
        y = addSlider(x, y, "gui.mythical_creatures_reborn.client_config.scale", CutieMarkConfig.DATA.leftScale, 0.001, 1.0);
        y = addSlider(x, y, "gui.mythical_creatures_reborn.client_config.x", CutieMarkConfig.DATA.leftX, -2.0, 2.0);
        y = addSlider(x, y, "gui.mythical_creatures_reborn.client_config.y", CutieMarkConfig.DATA.leftY, -2.0, 2.0);
        y = addSlider(x, y, "gui.mythical_creatures_reborn.client_config.z", CutieMarkConfig.DATA.leftZ, -2.0, 2.0);

        // 右腿 | Right leg
        y += 6;
        addLabel("gui.mythical_creatures_reborn.client_config.right_leg", y);
        y += 16;
        y = addSlider(x, y, "gui.mythical_creatures_reborn.client_config.scale", CutieMarkConfig.DATA.rightScale, 0.001, 1.0);
        y = addSlider(x, y, "gui.mythical_creatures_reborn.client_config.x", CutieMarkConfig.DATA.rightX, -2.0, 2.0);
        y = addSlider(x, y, "gui.mythical_creatures_reborn.client_config.y", CutieMarkConfig.DATA.rightY, -2.0, 2.0);
        y = addSlider(x, y, "gui.mythical_creatures_reborn.client_config.z", CutieMarkConfig.DATA.rightZ, -2.0, 2.0);

        // 完成按钮 | Done button
        this.addRenderableWidget(Button.builder(Component.translatable("gui.mythical_creatures_reborn.client_config.done"),
                        b -> this.onClose())
                .pos(this.width / 2 - 100, this.height - 30).size(200, 20).build());
    }

    private void addLabel(String key, int y) {
        // 分组标题通过 render 里的 drawString 绘制（见 render 方法）
        this.groupLabels.add(new GroupLabel(y, Component.translatable(key)));
    }

    private int addSlider(int x, int y, String labelKey, ForgeConfigSpec.DoubleValue value, double min, double max) {
        this.addRenderableWidget(new ConfigSlider(x, y, SLIDER_W, SLIDER_H,
                Component.translatable(labelKey), value, min, max));
        return y + SLIDER_H + 4;
    }

    private final java.util.List<GroupLabel> groupLabels = new java.util.ArrayList<>();

    private record GroupLabel(int y, Component text) {}

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        g.drawString(this.font, this.title, LEFT_X, 8, 0xFFFFFF);
        for (GroupLabel gl : this.groupLabels) {
            g.drawString(this.font, gl.text, LEFT_X, gl.y, 0xFFFFFF);
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
