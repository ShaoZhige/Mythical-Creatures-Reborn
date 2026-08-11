package com.shao.mythicalcreatures.event;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.config.MythicalConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.Locale;

/**
 * 按物品注册名单独覆盖属性。
 * 只会处理 overrides 里明确列出的物品，未列出的保持原版默认值。
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EquipmentConfigHandler {

    // 绿色数值面板的难点：原版 ItemStack.getTooltipLines 的 MODIFIERS 段用「引用相等 ==」
    // 判断 attrMod.getId() == Item.BASE_ATTACK_DAMAGE_UUID，只有命中才渲染成绿色「12 攻击伤害」；
    // 否则一律蓝字「+N」。但 Item.BASE_ATTACK_DAMAGE_UUID 在「生产构建」(SRG 重映射后) 字段名
    // 会变成 f_41374_，用反射按反混淆名 BASE_ATTACK_DAMAGE_UUID 取会在运行时 NoSuchFieldException
    // 退回到 UUID.fromString（值相等但非同一实例）→ == 失败 → 始终蓝字。
    //
    // 因此这里**不依赖 UUID 引用相等**，而是仿照参考模组 JETT：在 ItemTooltipEvent 里把本模组
    // 配置的武器攻击/攻速行重新渲染成 DARK_GREEN 绿字，直接覆盖原版蓝字。这样与 UV/字段名无关，
    // 在任何构建下都稳定生效。

    private static final UUID[] ARMOR_UUIDS = {
        UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), // FEET
        UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), // LEGS
        UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), // CHEST
        UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"), // HEAD
    };

    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        Item item = event.getItemStack().getItem();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null || !"mythicalcreatures".equals(id.getNamespace())) return;
        String key = id.toString();

        if (item instanceof ArmorItem armor) {
            applyArmor(event, key, armor.getType().getSlot());
        } else if (item instanceof TieredItem) {
            applyAttack(event, key);
            applyAttackSpeed(event, key);
        }
    }

    /** 护甲：仅覆盖配置中**真正指定**的属性；未指定的属性保持原版数值，绝不清零 */
    private static void applyArmor(ItemAttributeModifierEvent event, String key, EquipmentSlot slot) {
        if (event.getSlotType() != slot) return;

        int armorVal = (int) MythicalConfig.DATA.equipAttr(key, "armor");
        double tough = MythicalConfig.DATA.equipAttr(key, "armor_toughness");
        double kb    = MythicalConfig.DATA.equipAttr(key, "armor_kb_resist");

        // 每个属性独立判断：只有当该项在配置里被指定（>0）时才 remove + 重新 add，
        // 否则完全不动，原版自带的护甲韧性/击退抗性数值才能保留显示与生效。
        if (armorVal > 0) {
            UUID slotUUID = ARMOR_UUIDS[slot.getIndex()];
            event.removeAttribute(Attributes.ARMOR);
            event.addModifier(Attributes.ARMOR,
                new AttributeModifier(slotUUID, "config", armorVal,
                    AttributeModifier.Operation.ADDITION));
        }

        if (tough > 0) {
            event.removeAttribute(Attributes.ARMOR_TOUGHNESS);
            event.addModifier(Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(UUID.randomUUID(), "config",
                    tough, AttributeModifier.Operation.ADDITION));
        }

        if (kb > 0) {
            event.removeAttribute(Attributes.KNOCKBACK_RESISTANCE);
            event.addModifier(Attributes.KNOCKBACK_RESISTANCE,
                new AttributeModifier(UUID.randomUUID(), "config",
                    kb, AttributeModifier.Operation.ADDITION));
        }
    }

    /** 武器：仅当 overrides 中指定了此物品，且事件插槽为主手时才生效 */
    private static void applyAttack(ItemAttributeModifierEvent event, String key) {
        if (event.getSlotType() != EquipmentSlot.MAINHAND) return;

        double atk = MythicalConfig.DATA.equipAttr(key, "attack_damage");
        if (atk <= 0) return;

        // 移除物品自带的基础攻击修饰器后重新挂上配置值。
        // generic.attack_damage 的基准值是 1.0（空手），原版 sword 的修饰器加的是
        // (damage - 1.0)，因此这里也要加 (atk - 1.0)，否则显示/实际伤害会变成 atk + 1.0。
        // 修饰器用什么 UUID 不重要（绿色是 tooltip 事件重渲染负责），但用稳定的常量避免重复。
        event.removeAttribute(Attributes.ATTACK_DAMAGE);
        event.addModifier(Attributes.ATTACK_DAMAGE,
            new AttributeModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"), "config",
                atk - 1.0, AttributeModifier.Operation.ADDITION));
    }

    /** 武器攻速：仅当 overrides 中指定了 attack_speed，且事件插槽为主手时才生效 */
    private static void applyAttackSpeed(ItemAttributeModifierEvent event, String key) {
        if (event.getSlotType() != EquipmentSlot.MAINHAND) return;

        double speed = MythicalConfig.DATA.equipAttr(key, "attack_speed");
        if (speed <= 0) return;

        // generic.attack_speed 的基准值是 4.0（空手），原版 sword 的修饰器加的是
        // (speed - 4.0)，因此这里也要加 (speed - 4.0)，否则显示/实际攻速会变成 speed + 4.0。
        event.removeAttribute(Attributes.ATTACK_SPEED);
        event.addModifier(Attributes.ATTACK_SPEED,
            new AttributeModifier(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), "config",
                speed - 4.0, AttributeModifier.Operation.ADDITION));
    }

    // ================================================================
    //  Tooltip 重新渲染：仅把武器/工具（TieredItem）的「被配置覆盖」的
    //  攻击伤害·攻击速度行刷成绿色——因为原版本来就只有这两项走绿字数值面板，
    //  其它属性（护甲/韧性/击退等）原版就是蓝字「+N」，保持原版行为即可。
    //  只有配置里真正指定的属性才会被重渲染；未指定的保持原版显示，绝不丢失。
    //  显示用「总值」（如 attack_damage=12 显示 12，而非 +11），与实际生效值完全一致。
    //  （见上面关于 SRG 重映射导致 UUID== 不可靠的说明：这里不依赖 UUID，直接重渲染）
    // ================================================================

    // tooltip 里武器属性的固定展示顺序（与 vanilla 习惯一致）。
    private static final String[] TOOLTIP_ORDER = {
        "generic.attack_damage", "generic.attack_speed"
    };

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null || !"mythicalcreatures".equals(id.getNamespace())) return;

        // 只有武器 / 工具才做绿字重渲染（护甲等其他物品保持原版蓝字显示）。
        if (!(stack.getItem() instanceof TieredItem)) return;

        String key = id.toString();

        // 收集本武器「被配置覆盖」的属性 → 最终总值（用于绿字显示）。
        Map<String, Double> computed = new LinkedHashMap<>();
        // 武器 / 工具：攻击伤害、攻击速度（主手）。总值即配置值本身
        // （applyAttack/applyAttackSpeed 内部已把空手基准 1.0/4.0 折算进修饰器）。
        double atk   = MythicalConfig.DATA.equipAttr(key, "attack_damage");
        double speed = MythicalConfig.DATA.equipAttr(key, "attack_speed");
        if (atk   > 0) computed.put("generic.attack_damage", atk);
        if (speed > 0) computed.put("generic.attack_speed",  speed);

        if (computed.isEmpty()) return;

        List<Component> tip = event.getToolTip();

        // 找到每个属性原行的位置（取首个出现的下标），并把这些「蓝色 +N」行移除。
        Map<String, Integer> firstPos = new HashMap<>();
        Set<String> toReplace = computed.keySet();
        for (int i = 0; i < tip.size(); i++) {
            if (endsWithAnyAttr(tip.get(i), toReplace)) {
                String hit = matchAttr(tip.get(i), toReplace);
                if (hit != null && !firstPos.containsKey(hit)) firstPos.put(hit, i);
            }
        }
        tip.removeIf(line -> endsWithAnyAttr(line, toReplace));

        // 在原位置（取所有被删行的最小下标，找不到就放末尾）按固定顺序插回绿字。
        int insertAt = firstPos.isEmpty() ? tip.size() : Collections.min(firstPos.values());
        for (String path : TOOLTIP_ORDER) {
            if (!computed.containsKey(path)) continue;
            String name = Component.translatable("attribute.name." + path).getString();
            double value = computed.get(path);
            String num = formatDefault(value);
            tip.add(insertAt++, Component.literal(" " + ChatFormatting.DARK_GREEN + num + " " + name));
        }

        // 保底：原版排序是「攻击伤害」永远在「攻击速度」之上。
        // 若因任何边界情况导致二者顺序颠倒，此处强制交换，保证与原版一致。
        // （仿参考模组 JETT TooltipHandler.mergeAttributeModifiers 的同名逻辑）
        ensureAttackDamageAboveSpeed(tip);
    }

    private static void ensureAttackDamageAboveSpeed(List<Component> tip) {
        String dmgName = Component.translatable("attribute.name.generic.attack_damage").getString();
        String spdName = Component.translatable("attribute.name.generic.attack_speed").getString();
        int dmgIdx = -1, spdIdx = -1;
        for (int i = 0; i < tip.size(); i++) {
            String text = tip.get(i).getString().trim();
            if (dmgIdx < 0 && text.endsWith(dmgName)) dmgIdx = i;
            if (spdIdx < 0 && text.endsWith(spdName)) spdIdx = i;
        }
        // 只有本模组确实同时渲染了这两行（dmgIdx/spdIdx 都找到）且顺序颠倒时才交换。
        if (dmgIdx >= 0 && spdIdx >= 0 && dmgIdx > spdIdx) {
            Component spd = tip.remove(spdIdx);
            Component dmg = tip.remove(dmgIdx > spdIdx ? dmgIdx - 1 : dmgIdx);
            tip.add(spdIdx, dmg);
            tip.add(dmgIdx, spd);
        }
    }

    private static String matchAttr(Component line, Set<String> paths) {
        String text = line.getString().trim();
        for (String path : paths) {
            String name = Component.translatable("attribute.name." + path).getString();
            if (text.endsWith(name)) return path;
        }
        return null;
    }

    private static boolean endsWithAnyAttr(Component line, Set<String> paths) {
        return matchAttr(line, paths) != null;
    }

    private static String formatDefault(double value) {
        if (value == (long) value) return Long.toString((long) value);
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
