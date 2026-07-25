package com.shao.mythicalcreatures.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Map;
import java.util.WeakHashMap;

public class CrystalBlock extends Block {

    /** 冷却时间（毫秒） */
    private static final long COOLDOWN_MS = 5000;
    private static final Map<Player, Long> LAST_USE = new WeakHashMap<>();

    public CrystalBlock(Properties props) { super(props); }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            long now = System.currentTimeMillis();
            Long last = LAST_USE.get(player);
            if (last == null || now - last >= COOLDOWN_MS) {
                LAST_USE.put(player, now);
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 10));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
