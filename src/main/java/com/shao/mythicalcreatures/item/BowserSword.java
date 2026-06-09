package com.shao.mythicalcreatures.item;

import com.shao.mythicalcreatures.entity.MeteorFireballEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

public class BowserSword extends SwordItem {

    public BowserSword(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 0.7F,
                0.6F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            MeteorFireballEntity fireball = new MeteorFireballEntity(level, player);
            fireball.setItem(stack);
            fireball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 0.3F);
            level.addFreshEntity(fireball);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
