package com.shao.mythicalcreatures.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 支持附加自定义属性和套装动态 tooltip 的自定义盔甲。
 */
public class ModArmorItem extends ArmorItem {

    private final Multimap<Attribute, AttributeModifier> extraModifiers;
    @Nullable
    private final String setId;

    public ModArmorItem(ArmorMaterial material, Type type, Properties properties,
                        Multimap<Attribute, AttributeModifier> extraModifiers,
                        @Nullable String setId) {
        super(material, type, properties);
        this.extraModifiers = extraModifiers;
        this.setId = setId;
    }

    public ModArmorItem(ArmorMaterial material, Type type, Properties properties,
                        Multimap<Attribute, AttributeModifier> extraModifiers) {
        this(material, type, properties, extraModifiers, null);
    }

    public ModArmorItem(ArmorMaterial material, Type type, Properties properties,
                        @Nullable String setId) {
        this(material, type, properties, HashMultimap.create(), setId);
    }

    public ModArmorItem(ArmorMaterial material, Type type, Properties properties) {
        this(material, type, properties, HashMultimap.create(), null);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create(super.getDefaultAttributeModifiers(slot));
        if (slot == this.type.getSlot()) {
            map.putAll(this.extraModifiers);
        }
        return map;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                 List<Component> tooltip, TooltipFlag flag) {
        // 先追加原版 tooltip，套装描述放最下面
        super.appendHoverText(stack, level, tooltip, flag);

        if (setId != null) {
            if (Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("tooltip.mythicalcreatures.set." + setId + ".detail")
                        .withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("tooltip.mythicalcreatures.set." + setId + ".hint")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}
