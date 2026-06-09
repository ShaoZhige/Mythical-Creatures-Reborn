package com.shao.mythicalcreatures.event;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.item.ExcavatorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 挖掘者 3×3×3 范围挖掘逻辑。
 * 监听 BlockEvent.BreakEvent，在玩家持有挖掘者时同步挖掘周围方块。
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExcavatorHandler {

    /** 防止递归：正在处理的方块位置集合 */
    private static final Set<BlockPos> PROCESSING = new HashSet<>();

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!(player.getMainHandItem().getItem() instanceof ExcavatorItem)) return;

        BlockPos center = event.getPos();
        // 如果已经由本处理器触发，跳过（防递归）
        if (!PROCESSING.add(center)) return;

        try {
            // 挖掘以 center 为中心的 3×3×3 区域
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        if (pos.equals(center)) continue; // 跳过中心方块（玩家已在挖）

                        BlockState state = event.getLevel().getBlockState(pos);
                        if (state.isAir()) continue;

                        // 跳过不可破坏的方块（基岩、屏障等）
                        if (state.getDestroySpeed(event.getLevel(), pos) < 0) continue;

                        // 跳过工具无法采集的方块
                        if (!player.getMainHandItem().isCorrectToolForDrops(state)) continue;

                        // 标记并在标记清除前防止递归
                        if (!PROCESSING.add(pos)) continue;

                        try {
                            event.getLevel().destroyBlock(pos, !player.isCreative(), player);
                        } finally {
                            PROCESSING.remove(pos);
                        }
                    }
                }
            }
        } finally {
            PROCESSING.remove(center);
        }
    }
}
