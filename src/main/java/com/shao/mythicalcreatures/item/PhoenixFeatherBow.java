package com.shao.mythicalcreatures.item;

import com.shao.mythicalcreatures.entity.PhoenixFeatherEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class PhoenixFeatherBow extends BowItem {

    public PhoenixFeatherBow(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack bow, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        int charge = this.getUseDuration(bow) - timeLeft;
        float power = BowItem.getPowerForTime(charge);
        if (power < 0.1F) return;

        boolean infinity = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) > 0;

        if (!infinity && !consumeBlazePowder(player)) {
            return; // 没有烈焰粉且没有无限附魔
        }

        if (!level.isClientSide) {
            PhoenixFeatherEntity feather = new PhoenixFeatherEntity(level, player);
            // 速度随蓄力增加：1.5 ~ 4.5，散布 0.5
            feather.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.5F, power * 3.5F, 1.0F);
            level.addFreshEntity(feather);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F,
                1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F));

        bow.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bow = player.getItemInHand(hand);

        boolean infinity = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) > 0;
        // 开弓前先检查是否有烈焰粉（无限附魔跳过检查）
        if (!infinity && !hasBlazePowder(player)) {
            return InteractionResultHolder.fail(bow);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(bow);
    }

    /** 消耗背包中的 1 个烈焰粉 */
    private static boolean consumeBlazePowder(Player player) {
        if (player.getAbilities().instabuild) return true;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.BLAZE_POWDER)) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    /** 检查背包是否有烈焰粉 */
    private static boolean hasBlazePowder(Player player) {
        if (player.getAbilities().instabuild) return true;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(Items.BLAZE_POWDER)) {
                return true;
            }
        }
        return false;
    }
}
