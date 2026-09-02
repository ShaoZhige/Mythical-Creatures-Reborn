package com.shao.mythical_creatures_reborn.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;

// 杯子蛋糕：既是食物也是投掷物。普通右键进食，Shift+右键当作投掷物扔出。
// 普通右键直接走基类 Item.use 的食物进食逻辑，无需自行实现进食。
public class CupcakeItem extends Item {

    private final BiFunction<Level, net.minecraft.world.entity.LivingEntity, ThrowableItemProjectile> factory;

    public CupcakeItem(Properties properties,
                       BiFunction<Level, net.minecraft.world.entity.LivingEntity, ThrowableItemProjectile> factory) {
        super(properties);
        this.factory = factory;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            // Shift+右键：当作投掷物扔出
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EGG_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!level.isClientSide) {
                ThrowableItemProjectile projectile = factory.apply(level, player);
                projectile.setItem(stack);
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.2F, 0.8F);
                level.addFreshEntity(projectile);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        // 普通右键：按食物进食（基类已实现食物进食逻辑）
        return super.use(level, player, hand);
    }
}
