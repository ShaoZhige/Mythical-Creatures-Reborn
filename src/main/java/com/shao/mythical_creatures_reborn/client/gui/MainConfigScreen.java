package com.shao.mythical_creatures_reborn.client.gui;

import com.shao.mythical_creatures_reborn.config.MobStatsManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * 主配置界面 | Main config screen.
 * <p>
 * 提供两个入口：服务端配置（COMMON，进入可视化编辑器）与客户端配置（CLIENT，进入渲染调参）。
 * 命令 /mythical_creatures_reborn config_edit、配置按钮、按键绑定都进入这里。
 */
public class MainConfigScreen extends Screen {

    private static final int BTN_W = 200;

    private final Screen parent;

    public MainConfigScreen(Screen parent) {
        super(Component.translatable("gui.mythical_creatures_reborn.main_config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - BTN_W / 2;
        int y = this.height / 2 - 40;

        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.mythical_creatures_reborn.main_config.server"),
                        b -> openServer())
                .pos(x, y).size(BTN_W, 20).build());
        y += 26;
        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.mythical_creatures_reborn.main_config.client"),
                        b -> openClient())
                .pos(x, y).size(BTN_W, 20).build());
        y += 32;
        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.done"),
                        b -> this.onClose())
                .pos(x, y).size(BTN_W, 20).build());
    }

    private void openServer() {
        if (this.minecraft != null)
            this.minecraft.setScreen(new MobStatsScreen(MobStatsManager.buildSnapshot(), this));
    }

    private void openClient() {
        if (this.minecraft != null)
            this.minecraft.setScreen(new CutieMarkConfigScreen(this));
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, 16, 0xFFFFFF);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.parent);
    }
}
