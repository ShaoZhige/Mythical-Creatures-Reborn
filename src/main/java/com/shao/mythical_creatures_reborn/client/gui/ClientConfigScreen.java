package com.shao.mythical_creatures_reborn.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * 客户端配置目录 | Client config index screen.
 * <p>
 * 进入「客户端配置」后的第一级界面：列出可编辑的客户端配置分类，点进去才加载具体控件。
 * 这样单个界面永远只有少量控件，不会因配置变多而与底部按钮重叠。
 * 目前只有「饰品栏可爱标志渲染位置」一项；后续新增分类只需在 init() 里加一个入口按钮。
 */
public class ClientConfigScreen extends Screen {

    private static final int ENTRY_W = 240;
    private static final int ENTRY_H = 20;
    private static final int BTN_H = 20;

    private final Screen parent;

    public ClientConfigScreen(Screen parent) {
        super(Component.translatable("gui.mythical_creatures_reborn.client_config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = Math.min(ENTRY_W, Math.max(140, this.width - 40));
        int x = this.width / 2 - w / 2;

        // 分类入口：饰品栏可爱标志渲染位置
        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.mythical_creatures_reborn.client_config.category.cutie_mark"),
                        b -> openCutieMark())
                .pos(x, 44).size(w, ENTRY_H).build());

        // 返回上级
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"),
                        b -> this.onClose())
                .pos(this.width / 2 - 100, this.height - 30).size(200, BTN_H).build());
    }

    private void openCutieMark() {
        if (this.minecraft != null)
            this.minecraft.setScreen(new CutieMarkConfigScreen(this));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, 18, 0xFFFFFF);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.parent);
    }
}
