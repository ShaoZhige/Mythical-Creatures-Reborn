package com.shao.mythicalcreatures.item;

import com.shao.mythicalcreatures.entity.TwilightStarEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class TwilightSparkleBow extends BowItem {

    private static final int FAST_DRAW = 36000; // 原版 72000，缩短一半

    public TwilightSparkleBow(Properties properties) {
        super(properties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(ModItems.TWILIGHT_STAR_ITEM.get());
    }

    @Override
    public int getDefaultProjectileRange() {
        return 20;
    }

    /** 拉弓速度加倍 */
    @Override
    public int getUseDuration(ItemStack stack) {
        return FAST_DRAW;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        // 创造模式不需要弹药
        ItemStack ammo;
        if (player.getAbilities().instabuild) {
            ammo = new ItemStack(ModItems.TWILIGHT_STAR_ITEM.get());
        } else {
            ammo = player.getProjectile(stack);
            if (ammo.isEmpty() || !ammo.is(ModItems.TWILIGHT_STAR_ITEM.get())) {
                return;
            }
        }

        int charge = this.getUseDuration(stack) - timeLeft;
        float power = BowItem.getPowerForTime(charge);
        if (power < 0.1F) return;

        if (!level.isClientSide) {
            TwilightStarEntity star = new TwilightStarEntity(level, player);
            star.setItem(ammo);
            star.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
            level.addFreshEntity(star);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F,
                1.0F / (level.random.nextFloat() * 0.4F + 1.2F) + power * 0.5F);

        // 不消耗弹药
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        player.awardStat(Stats.ITEM_USED.get(this));
    }
}
