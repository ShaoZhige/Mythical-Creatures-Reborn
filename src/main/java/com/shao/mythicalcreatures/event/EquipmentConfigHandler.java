package com.shao.mythicalcreatures.event;

import com.shao.mythicalcreatures.config.MythicalConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 按物品注册名单独覆盖属性。
 * 只会处理 overrides 里明确列出的物品，未列出的保持原版默认值。
 */
@Mod.EventBusSubscriber
public class EquipmentConfigHandler {

    private static final UUID BASE_ATTACK_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

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
        }
    }

    /** 护甲：仅当 overrides 中指定了此物品才生效 */
    private static void applyArmor(ItemAttributeModifierEvent event, String key, EquipmentSlot slot) {
        int armorVal  = (int) MythicalConfig.DATA.equipAttr(key, "armor");
        double tough  = MythicalConfig.DATA.equipAttr(key, "armor_toughness");
        double kb     = MythicalConfig.DATA.equipAttr(key, "armor_kb_resist");
        if (armorVal <= 0 && tough <= 0 && kb <= 0) return;

        UUID slotUUID = ARMOR_UUIDS[slot.getIndex()];

        event.removeAttribute(Attributes.ARMOR);
        event.addModifier(Attributes.ARMOR,
            new AttributeModifier(slotUUID, "config", armorVal,
                AttributeModifier.Operation.ADDITION));

        event.removeAttribute(Attributes.ARMOR_TOUGHNESS);
        event.addModifier(Attributes.ARMOR_TOUGHNESS,
            new AttributeModifier(UUID.randomUUID(), "config",
                tough, AttributeModifier.Operation.ADDITION));

        event.removeAttribute(Attributes.KNOCKBACK_RESISTANCE);
        event.addModifier(Attributes.KNOCKBACK_RESISTANCE,
            new AttributeModifier(UUID.randomUUID(), "config",
                kb, AttributeModifier.Operation.ADDITION));
    }

    /** 武器：仅当 overrides 中指定了此物品才生效 */
    private static void applyAttack(ItemAttributeModifierEvent event, String key) {
        double atk = MythicalConfig.DATA.equipAttr(key, "attack_damage");
        if (atk <= 0) return;

        event.removeAttribute(Attributes.ATTACK_DAMAGE);
        event.addModifier(Attributes.ATTACK_DAMAGE,
            new AttributeModifier(BASE_ATTACK_UUID, "config",
                atk, AttributeModifier.Operation.ADDITION));
    }
}
