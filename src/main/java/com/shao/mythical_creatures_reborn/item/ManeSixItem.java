package com.shao.mythical_creatures_reborn.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 友谊就是魔法 — 一击必杀。
 * 用 setHealth(0) 绕过护甲/抗性直接秒杀目标。
 */
public class ManeSixItem extends Item {

    public ManeSixItem(Properties props) {
        super(props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide) {
            target.setHealth(0);
            target.invulnerableTime = 0;
        }
        return true;
    }
}
