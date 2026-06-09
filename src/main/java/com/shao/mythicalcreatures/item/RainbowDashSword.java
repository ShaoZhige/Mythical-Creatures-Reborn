package com.shao.mythicalcreatures.item;

import com.shao.mythicalcreatures.entity.RainbowBeamEntity;
import com.shao.mythicalcreatures.entity.RainbowCloudEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Tier;

public class RainbowDashSword extends SwordItem {

    public RainbowDashSword(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // 手持此剑时，右键实体不触发实体交互（交易、骑乘等），改为触发光束/彩虹云
        use(player.level(), player, hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isShiftKeyDown()) {
            // 右键 → 发射彩虹云
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.8F);

            if (!level.isClientSide) {
                RainbowCloudEntity cloud = new RainbowCloudEntity(level, player);
                cloud.setItem(new ItemStack(ModItems.RAINBOW_CLOUD_ITEM.get()));
                cloud.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(cloud);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        // 潜行 + 右键按住 → 彩虹光束（持续跟随、松开消失）
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 0.6F, 1.0F);

        player.startUsingItem(hand);

        if (!level.isClientSide) {
            RainbowBeamEntity beam = new RainbowBeamEntity(level, player);
            level.addFreshEntity(beam);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        // 取消不需要音效
    }
}
