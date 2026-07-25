package com.shao.mythicalcreatures.item;

import com.shao.mythicalcreatures.entity.ModEntities;
import com.shao.mythicalcreatures.entity.custom.TwilightMagicEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TwilightMagicEggItem extends Item {

    public TwilightMagicEggItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (hit.getType() == HitResult.Type.BLOCK) {
            spawnMagic(level, player, stack, Vec3.atCenterOf(hit.getBlockPos().relative(hit.getDirection())));
        } else {
            spawnMagic(level, player, stack, player.position().add(0, 1.5, 0));
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private void spawnMagic(Level level, Player player, ItemStack stack, net.minecraft.world.phys.Vec3 pos) {
        if (!level.isClientSide) {
            TwilightMagicEntity magic = new TwilightMagicEntity(ModEntities.TWILIGHT_MAGIC.get(), level);
            magic.setPos(pos);
            magic.setOwner(player);
            level.addFreshEntity(magic);
        }
    }
}
