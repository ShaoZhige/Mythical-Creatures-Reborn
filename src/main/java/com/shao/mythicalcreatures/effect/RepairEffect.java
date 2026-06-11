package com.shao.mythicalcreatures.effect;

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
        if (!(entity instanceof Player player)) return;

        int repairAmount = 1 + amplifier; // 每 3 秒回复 (1 + 等级) 点

        // 遍历背包 36 格
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            repair(player.getInventory().getItem(i), repairAmount);
        }
        // 装备栏
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR || slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
                repair(player.getItemBySlot(slot), repairAmount);
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
        // 每 3 秒（60 tick）触发一次
        return duration % 60 == 0;
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }
}
