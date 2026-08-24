package com.shao.mythicalcreatures.effect;

import com.shao.mythicalcreatures.config.MythicalConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class RepairEffect extends MobEffect {

    public RepairEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x55FF55);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        int repairAmount = MythicalConfig.DATA.getInt("global_params", "repair_amount", 1) + amplifier;

        // 玩家额外修复背包 36 格（怪物没有背包，只有装备栏）
        if (entity instanceof Player player) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                repair(player.getInventory().getItem(i), repairAmount);
            }
        }

        // 通用：修复装备栏（护甲 + 主/副手），玩家与穿戴装备的怪物（僵尸/骷髅等）都适用。
        // getItemBySlot 是 LivingEntity 的通用方法，怪物（Mob）同样能读到自己的护甲/武器。
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR || slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
                repair(entity.getItemBySlot(slot), repairAmount);
            }
        }
    }

    private static void repair(ItemStack stack, int amount) {
        if (stack.isDamaged()) {
            stack.setDamageValue(Math.max(0, stack.getDamageValue() - amount));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        int interval = Math.max(1, MythicalConfig.DATA.getInt("global_params", "repair_interval", 60));
        return duration % interval == 0;
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }
}
