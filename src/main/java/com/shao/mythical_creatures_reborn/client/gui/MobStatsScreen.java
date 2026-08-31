package com.shao.mythical_creatures_reborn.client.gui;

import com.shao.mythical_creatures_reborn.config.MobStatsManager;
import com.shao.mythical_creatures_reborn.config.MobStatsManager.Category;
import com.shao.mythical_creatures_reborn.config.MobStatsManager.Row;
import com.shao.mythical_creatures_reborn.config.MobStatsManager.Target;
import com.shao.mythical_creatures_reborn.network.MobStatsEditPacket;
import com.shao.mythical_creatures_reborn.network.MobStatsSavePacket;
import com.shao.mythical_creatures_reborn.network.ModNetwork;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 通用配置编辑器界面 | Generic config editor screen.
 * <p>
 * 交互：左侧为「已加入对象」列表（初始为当前已覆盖的对象），顶部「新建」按分类
 * （生物/物品/全局）挑选对象加入；点选某对象后右侧展开它真正支持的属性
 * （数值 + 重置× + 可选注释）。底部三按钮：保存 / 重置全部 / 关闭。
 * 所有改动「重启后生效」——点保存写入 common.toml 的 overrides。
 */
public class MobStatsScreen extends Screen {

    private static final int LEFT_W = 176;
    private static final int ROW_H = 18;
    private static final int STAT_ROW_H = 54;     // 含注释框：数值行(18) + 注释行(14+间距)
    private static final int BTN_H = 20;
    private static final int RESET_W = 20;
    private static final int BG = -871362544;
    private static final int SEL = -13672545;
    private static final int HOVER = -2010161249;
    private static final int TEXT = 14737632;
    private static final int DIM = 9474192;
    private static final Pattern NUM = Pattern.compile("-?\\d*\\.?\\d*");

    /** 原版属性名（这些 key 直接复用原版本地化描述，其余走 stat.mythical_creatures_reborn.*） */
    private static final Map<String, Attribute> VANILLA_ATTR = new LinkedHashMap<>();
    static {
        VANILLA_ATTR.put("max_health", Attributes.MAX_HEALTH);
        VANILLA_ATTR.put("move_speed", Attributes.MOVEMENT_SPEED);
        VANILLA_ATTR.put("attack_damage", Attributes.ATTACK_DAMAGE);
        VANILLA_ATTR.put("fly_speed", Attributes.FLYING_SPEED);
        VANILLA_ATTR.put("attack_speed", Attributes.ATTACK_SPEED);
        VANILLA_ATTR.put("armor", Attributes.ARMOR);
        VANILLA_ATTR.put("armor_toughness", Attributes.ARMOR_TOUGHNESS);
    }

    private final List<Target> all;       // 服务端下发的全量快照（所有候选对象）
    private final List<Target> added;     // 已加入列表（引用 all 中的对象）
    private int selected = -1;            // added 中选中的下标

    // 新建选择界面状态
    private boolean picking = false;
    private Category pickCat = Category.ENTITY;
    private EditBox pickSearch;
    private final List<Target> pickList = new ArrayList<>();
    private int pickScroll = 0;

    private int listScroll = 0;
    private int detailScroll = 0;

    // 当前选中对象的编辑控件
    private final List<EditBox> valueBoxes = new ArrayList<>();
    private final List<Button> resetButtons = new ArrayList<>();
    private final List<EditBox> commentBoxes = new ArrayList<>();

    private final Screen parent;

    public MobStatsScreen(List<Target> all, Screen parent) {
        super(Component.translatable("gui.mythical_creatures_reborn.editor.title"));
        this.all = all;
        this.parent = parent;
        this.added = new ArrayList<>();
        for (Target t : all) if (t.hasOverrides()) this.added.add(t);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.parent);
    }

    /* ============================================================
     * 布局辅助 | Layout helpers
     * ============================================================ */

    private int rightX() { return LEFT_W + 16; }
    private int rightW() { return Math.max(120, this.width - rightX() - 8); }
    private int listTop() { return 24; }
    private int listBottom() { return this.height - 8 - BTN_H - 8; }
    private int boxW() { return Math.max(64, Math.min(120, rightW() - RESET_W - 6 - 12)); }
    private int boxX() { return rightX() + rightW() - RESET_W - 6 - boxW(); }
    private int resetX() { return rightX() + rightW() - RESET_W - 4; }

    private boolean isOnServer() {
        return this.minecraft != null && this.minecraft.player != null && this.minecraft.getConnection() != null;
    }

    private boolean canEdit() {
        if (this.minecraft == null) return false;
        if (!isOnServer()) return true; // 主菜单/未进入世界：本地即权威，允许编辑
        return this.minecraft.player.hasPermissions(2);
    }

    private void notifyNoPermission() {
        if (this.minecraft != null && this.minecraft.player != null)
            this.minecraft.player.sendSystemMessage(
                    Component.translatable("gui.mythical_creatures_reborn.editor.no_permission"));
    }

    /* ============================================================
     * 控件重建 | Widget rebuilding（主界面 / 新建界面）
     * ============================================================ */

    @Override
    protected void init() {
        if (picking) initPickWidgets();
        else initMainWidgets();
    }

    private void initMainWidgets() {
        // 底部四按钮：新建 / 保存 / 重置全部 / 关闭
        int bw = 80;
        int gap = 6;
        int total = bw * 4 + gap * 3;
        int bx = (this.width - total) / 2;
        int by = this.height - 8 - BTN_H;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.mythical_creatures_reborn.editor.new"),
                b -> enterPick()).pos(bx, by).size(bw, BTN_H).build());
        bx += bw + gap;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.mythical_creatures_reborn.editor.save"),
                b -> save()).pos(bx, by).size(bw, BTN_H).build());
        bx += bw + gap;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.mythical_creatures_reborn.editor.reset_all"),
                b -> resetAll()).pos(bx, by).size(bw, BTN_H).build());
        bx += bw + gap;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.mythical_creatures_reborn.editor.close"),
                b -> this.onClose()).pos(bx, by).size(bw, BTN_H).build());

        rebuildDetailRows();
    }

    private void enterPick() {
        flushCurrent();
        this.picking = true;
        this.pickCat = Category.ENTITY;
        this.pickScroll = 0;
        this.clearWidgets();
        initPickWidgets();
    }

    private void initPickWidgets() {
        // 返回按钮
        this.addRenderableWidget(Button.builder(Component.translatable("gui.mythical_creatures_reborn.editor.back"),
                b -> exitPick()).pos(8, 4).size(50, 20).build());

        // 分类 tab 按钮
        int tx = 66;
        int tw = 64;
        for (Category c : Category.values()) {
            Category cc = c;
            Button tab = Button.builder(categoryName(c),
                    b -> { this.pickCat = cc; this.pickScroll = 0; refreshPickList(); })
                    .pos(tx, 4).size(tw, 20).build();
            this.addRenderableWidget(tab);
            tx += tw + 4;
        }

        // 搜索框
        this.pickSearch = new EditBox(this.font, this.width - 160, 4, 152, 20, Component.literal(""));
        this.pickSearch.setMaxLength(64);
        this.pickSearch.setHint(Component.translatable("gui.mythical_creatures_reborn.editor.search"));
        this.pickSearch.setResponder(s -> { this.pickScroll = 0; refreshPickList(); });
        this.addRenderableWidget(this.pickSearch);

        refreshPickList();
    }

    private void exitPick() {
        this.picking = false;
        this.clearWidgets();
        initMainWidgets();
    }

    private void refreshPickList() {
        String q = (this.pickSearch == null) ? "" : this.pickSearch.getValue().trim().toLowerCase(Locale.ROOT);
        this.pickList.clear();
        for (Target t : this.all) {
            if (t.category != this.pickCat) continue;
            if (!q.isEmpty()
                    && !labelOf(t).getString().toLowerCase(Locale.ROOT).contains(q)
                    && !t.id.toLowerCase(Locale.ROOT).contains(q)) continue;
            this.pickList.add(t);
        }
    }

    /* ============================================================
     * 详情（右栏属性）控件
     * ============================================================ */

    private void rebuildDetailRows() {
        for (EditBox b : this.valueBoxes) this.removeWidget(b);
        for (Button b : this.resetButtons) this.removeWidget(b);
        for (EditBox b : this.commentBoxes) this.removeWidget(b);
        this.valueBoxes.clear();
        this.resetButtons.clear();
        this.commentBoxes.clear();
        this.detailScroll = 0;
        if (this.selected < 0 || this.selected >= this.added.size()) return;

        Target t = this.added.get(this.selected);
        for (Row row : t.rows) {
            EditBox box = new EditBox(this.font, boxX(), 0, boxW(), 16, Component.literal(row.key));
            box.setMaxLength(20);
            box.setValue(displayValue(t, row));
            box.setFilter(s -> s.isEmpty() || NUM.matcher(s).matches());
            if (t.category == Category.ITEM)
                box.setHint(Component.translatable("gui.mythical_creatures_reborn.editor.vanilla_default"));
            this.valueBoxes.add(box);
            this.addRenderableWidget(box);

            Button reset = Button.builder(Component.literal("×"), b -> resetRow(t, row))
                    .pos(resetX(), 0).size(RESET_W, 18).build();
            this.resetButtons.add(reset);
            this.addRenderableWidget(reset);

            EditBox cmt = new EditBox(this.font, rightX() + 6, 0, rightW() - 12, 14, Component.literal("comment"));
            cmt.setMaxLength(120);
            cmt.setValue(row.comment);
            cmt.setHint(Component.translatable("gui.mythical_creatures_reborn.editor.comment_hint"));
            this.commentBoxes.add(cmt);
            this.addRenderableWidget(cmt);
        }
    }

    private void layoutDetailRows() {
        int top = listTop();
        int bottom = listBottom();
        int y = top + 2 - this.detailScroll;
        int bx = boxX();
        int rx = resetX();
        for (int i = 0; i < this.valueBoxes.size(); i++) {
            boolean vis = (y >= top && y + STAT_ROW_H <= bottom);
            EditBox box = this.valueBoxes.get(i);
            Button btn = this.resetButtons.get(i);
            EditBox cmt = this.commentBoxes.get(i);
            box.setPosition(bx, y + 2);
            box.visible = vis;
            btn.setPosition(rx, y + 2);
            btn.visible = vis;
            cmt.setPosition(rightX() + 6, y + 34);
            cmt.visible = vis;
            y += STAT_ROW_H;
        }
    }

    /* ============================================================
     * 编辑状态同步 | Flush edit boxes -> row state
     * ============================================================ */

    private void flushCurrent() {
        if (this.selected < 0 || this.selected >= this.added.size()) return;
        Target t = this.added.get(this.selected);
        for (int i = 0; i < t.rows.size() && i < this.valueBoxes.size(); i++) {
            Row row = t.rows.get(i);
            String raw = this.valueBoxes.get(i).getValue().trim();
            String comment = this.commentBoxes.get(i).getValue();
            row.comment = comment;
            boolean valid = !raw.isEmpty() && NUM.matcher(raw).matches();
            if (t.category == Category.ITEM) {
                // 物品：空=未覆盖(用原版)，非空数值=覆盖值
                if (valid) { row.cur = Double.parseDouble(raw); row.overridden = row.cur > 0; }
                else { row.cur = 0.0; row.overridden = false; }
            } else {
                row.cur = valid ? Double.parseDouble(raw) : row.def;
            }
        }
    }

    private void resetRow(Target t, Row row) {
        int idx = this.added.indexOf(t);
        if (idx < 0) return;
        // 仅本地改状态；是否真正落盘由「保存」统一处理
        if (t.category == Category.ITEM) {
            row.cur = 0.0; row.overridden = false;
        } else {
            row.cur = row.def; row.overridden = false;
        }
        row.comment = "";
        rebuildDetailRows();
    }

    /* ============================================================
     * 保存 / 重置全部
     * ============================================================ */

    private void save() {
        if (!canEdit()) { notifyNoPermission(); return; }
        flushCurrent();
        for (Target t : this.added) {
            for (Row row : t.rows) {
                boolean item = (t.category == Category.ITEM);
                boolean active;
                if (item) active = row.overridden || !row.comment.isEmpty();
                else active = (Math.abs(row.cur - row.def) >= 1.0E-6D) || !row.comment.isEmpty();
                if (active) {
                    if (isOnServer()) {
                        ModNetwork.CHANNEL.sendToServer(new MobStatsEditPacket(t.id, row.key, row.cur, false, row.comment));
                    } else {
                        MobStatsManager.set(t.id, row.key, row.cur, row.comment);
                    }
                    row.overridden = true;
                } else if (row.overridden) {
                    if (isOnServer()) {
                        ModNetwork.CHANNEL.sendToServer(new MobStatsEditPacket(t.id, row.key, 0.0D, true, ""));
                    } else {
                        MobStatsManager.reset(t.id, row.key);
                    }
                    row.overridden = false;
                    row.comment = "";
                }
            }
        }
        if (isOnServer()) {
            ModNetwork.CHANNEL.sendToServer(new MobStatsSavePacket(false));
        } else {
            MobStatsManager.save();
        }
    }

    private void resetAll() {
        if (!canEdit()) { notifyNoPermission(); return; }
        if (isOnServer()) {
            ModNetwork.CHANNEL.sendToServer(new MobStatsSavePacket(true));
        } else {
            MobStatsManager.resetAll();
            MobStatsManager.save();
        }
        for (Target t : this.all) {
            for (Row row : t.rows) {
                if (t.category == Category.ITEM) { row.cur = 0.0; row.overridden = false; }
                else { row.cur = row.def; row.overridden = false; }
                row.comment = "";
            }
        }
        this.added.clear();
        this.selected = -1;
        rebuildDetailRows();
    }

    /* ============================================================
     * 渲染 | Render
     * ============================================================ */

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        if (picking) { renderPick(g, mouseX, mouseY); }
        else { renderMain(g, mouseX, mouseY); }
        super.render(g, mouseX, mouseY, partialTick);
    }

    private void renderMain(GuiGraphics g, int mouseX, int mouseY) {
        int rightX = rightX();
        int rightW = rightW();
        int bottom = listBottom();

        g.drawString(this.font, this.title, 8, 6, 0xFFFFFF);
        Component hint = Component.translatable("gui.mythical_creatures_reborn.editor.hint");
        String hintText = trim(this.font, hint, this.width - 100);
        g.drawString(this.font, hintText, this.width - 8 - this.font.width(hintText), 6, DIM);

        // 左栏：已加入对象列表
        g.fill(8, listTop(), LEFT_W + 8, bottom, BG);
        if (this.added.isEmpty()) {
            g.drawString(this.font, trim(this.font,
                    Component.translatable("gui.mythical_creatures_reborn.editor.empty"), LEFT_W - 8),
                    12, listTop() + 6, DIM);
        } else {
            int maxScroll = Math.max(0, this.added.size() * ROW_H - (bottom - listTop()));
            this.listScroll = Math.min(this.listScroll, maxScroll);
            for (int i = 0; i < this.added.size(); i++) {
                int k = listTop() + i * ROW_H - this.listScroll;
                if (k >= listTop() && k + ROW_H <= bottom) {
                    boolean sel = (i == this.selected);
                    boolean hover = (mouseX >= 8 && mouseX <= LEFT_W + 8 && mouseY >= k && mouseY < k + ROW_H);
                    if (sel) g.fill(8, k, LEFT_W + 8, k + ROW_H, SEL);
                    else if (hover) g.fill(8, k, LEFT_W + 8, k + ROW_H, HOVER);
                    int n = overriddenCount(this.added.get(i));
                    String label = categoryShort(this.added.get(i).category) + " " + labelOf(this.added.get(i)).getString();
                    if (n > 0) label += "  (" + n + ")";
                    g.drawString(this.font, trim(this.font, Component.literal(label), LEFT_W - 8),
                            12, k + 5, sel ? 0xFFFFFF : TEXT);
                }
            }
        }

        // 右栏：选中对象详情
        g.fill(rightX, listTop(), rightX + rightW, bottom, BG);
        if (this.selected < 0 || this.selected >= this.added.size()) {
            g.drawString(this.font, trim(this.font,
                    Component.translatable("gui.mythical_creatures_reborn.editor.no_selection"), rightW - 12),
                    rightX + 6, listTop() + 6, DIM);
        } else {
            Target cur = this.added.get(this.selected);
            int y = listTop() + 2 - this.detailScroll;
            for (int j = 0; j < cur.rows.size(); j++) {
                Row row = cur.rows.get(j);
                if (y >= listTop() + 2 && y + STAT_ROW_H <= bottom) {
                    g.drawString(this.font, trim(this.font, statName(row.key), boxX() - rightX - 12),
                            rightX + 6, y + 6, TEXT);
                    g.drawString(this.font, Component.translatable("gui.mythical_creatures_reborn.editor.default", fmt(row.def)),
                            rightX + 6, y + 20, DIM);
                }
                y += STAT_ROW_H;
            }
            layoutDetailRows();
        }
    }

    private void renderPick(GuiGraphics g, int mouseX, int mouseY) {
        int bottom = listBottom();
        g.drawString(this.font, trim(this.font,
                Component.translatable("gui.mythical_creatures_reborn.editor.pick_title"), this.width - 16),
                8, 28, 0xFFFFFF);
        g.fill(8, 48, this.width - 8, bottom, BG);

        int top = 50;
        int maxScroll = Math.max(0, this.pickList.size() * ROW_H - (bottom - top));
        this.pickScroll = Math.min(this.pickScroll, maxScroll);
        for (int i = 0; i < this.pickList.size(); i++) {
            int k = top + i * ROW_H - this.pickScroll;
            if (k >= top && k + ROW_H <= bottom) {
                boolean hover = (mouseX >= 8 && mouseX <= this.width - 8 && mouseY >= k && mouseY < k + ROW_H);
                if (hover) g.fill(8, k, this.width - 8, k + ROW_H, HOVER);
                String label = categoryShort(this.pickList.get(i).category) + " " + labelOf(this.pickList.get(i)).getString();
                g.drawString(this.font, trim(this.font, Component.literal(label), this.width - 24),
                        12, k + 5, TEXT);
            }
        }
        if (this.pickList.isEmpty())
            g.drawString(this.font, trim(this.font,
                    Component.translatable("gui.mythical_creatures_reborn.editor.no_candidate"), this.width - 24),
                    12, top + 6, DIM);
    }

    /* ============================================================
     * 鼠标 / 滚动 | Mouse & scroll
     * ============================================================ */

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        if (picking) return clickPick(mouseX, mouseY);
        return clickMain(mouseX, mouseY);
    }

    private boolean clickMain(double mouseX, double mouseY) {
        int bottom = listBottom();
        // 左栏列表点击
        if (mouseX >= 8.0D && mouseX <= LEFT_W + 8.0D && mouseY >= listTop() && mouseY < bottom) {
            int i = (int) ((mouseY - listTop() + this.listScroll) / ROW_H);
            if (i >= 0 && i < this.added.size()) {
                flushCurrent();
                this.selected = i;
                rebuildDetailRows();
                return true;
            }
        }
        return false;
    }

    private boolean clickPick(double mouseX, double mouseY) {
        int bottom = listBottom();
        int top = 50;
        if (mouseY >= top && mouseY < bottom && mouseX >= 8.0D && mouseX <= this.width - 8.0D) {
            int i = (int) ((mouseY - top + this.pickScroll) / ROW_H);
            if (i >= 0 && i < this.pickList.size()) {
                Target t = this.pickList.get(i);
                if (!this.added.contains(t)) this.added.add(t);
                this.selected = this.added.indexOf(t);
                this.picking = false;
                this.clearWidgets();
                initMainWidgets();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (super.mouseScrolled(mouseX, mouseY, delta)) return true;
        int d = (int) (-delta * 18.0D);
        if (picking) {
            int bottom = listBottom();
            int maxScroll = Math.max(0, this.pickList.size() * ROW_H - (bottom - 50));
            this.pickScroll = Math.max(0, Math.min(maxScroll, this.pickScroll + d));
            return true;
        }
        if (mouseX <= LEFT_W + 8.0D) {
            this.listScroll = Math.max(0, this.listScroll + d);
        } else {
            Target cur = (this.selected >= 0 && this.selected < this.added.size()) ? this.added.get(this.selected) : null;
            if (cur != null) {
                int bottom = listBottom();
                int maxScroll = Math.max(0, cur.rows.size() * STAT_ROW_H - (bottom - (listTop() + 2)));
                this.detailScroll = Math.max(0, Math.min(maxScroll, this.detailScroll + d));
            }
        }
        return true;
    }

    /* ============================================================
     * 名称 / 显示辅助 | Name & display helpers
     * ============================================================ */

    private static String displayValue(Target t, Row row) {
        if (t.category == Category.ITEM && !row.overridden) return "";
        return fmt(row.cur);
    }

    private static int overriddenCount(Target t) {
        int n = 0;
        for (Row r : t.rows) if (r.overridden) n++;
        return n;
    }

    private static Component categoryName(Category c) {
        return Component.translatable("gui.mythical_creatures_reborn.editor.category." + c.name().toLowerCase(Locale.ROOT));
    }

    private static String categoryShort(Category c) {
        return Component.translatable("gui.mythical_creatures_reborn.editor.category." + c.name().toLowerCase(Locale.ROOT)).getString();
    }

    private Component labelOf(Target t) {
        if (t.category == Category.GLOBAL)
            return Component.translatable("gui.mythical_creatures_reborn.editor.global");
        ResourceLocation rl = parseRl(t.id);
        if (t.category == Category.ENTITY && rl != null) {
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(rl);
            if (type != null) return type.getDescription();
        }
        if (t.category == Category.ITEM && rl != null) {
            Item item = BuiltInRegistries.ITEM.get(rl);
            if (item != null) return item.getDescription();
        }
        return Component.literal(shortName(t.id));
    }

    private static ResourceLocation parseRl(String id) {
        int i = id.indexOf(':');
        if (i > 0) return new ResourceLocation(id.substring(0, i), id.substring(i + 1));
        return new ResourceLocation(id);
    }

    private static String shortName(String id) {
        int i = id.indexOf(':');
        return (i > 0) ? id.substring(i + 1) : id;
    }

    private static Component statName(String key) {
        Attribute attr = VANILLA_ATTR.get(key);
        if (attr != null) return Component.translatable(attr.getDescriptionId());
        return Component.translatable("stat.mythical_creatures_reborn." + key);
    }

    private static String fmt(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "0";
        if (v == Math.rint(v) && Math.abs(v) < 1.0E10D) return String.valueOf((long) v);
        String s = String.format(Locale.US, "%.4f", v);
        while (s.endsWith("0")) s = s.substring(0, s.length() - 1);
        return s.endsWith(".") ? (s + "0") : s;
    }

    private static String trim(Font font, Component text, int maxWidth) {
        String s = text.getString();
        if (maxWidth <= 8) return "";
        if (font.width(s) <= maxWidth) return s;
        while (s.length() > 1 && font.width(s + "...") > maxWidth) s = s.substring(0, s.length() - 1);
        return s + "...";
    }
}
