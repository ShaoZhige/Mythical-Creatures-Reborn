package com.shao.mythical_creatures_reborn.event;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.item.ExcavatorItem;
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
        if (!PROCESSING.add(center)) return;

        try {
            // 不取消事件 — 原版逻辑负责中心方块（已带附魔），循环只处理周围 26 个
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        BlockPos pos = center.offset(dx, dy, dz);

                        BlockState state = event.getLevel().getBlockState(pos);
                        if (state.isAir()) continue;
                        if (state.getDestroySpeed(event.getLevel(), pos) < 0) continue;
                        if (!player.getMainHandItem().isCorrectToolForDrops(state)) continue;

                        if (!PROCESSING.add(pos)) continue;

                        try {
                            // gameMode.destroyBlock 会正确应用时运/精准采集/效率并消耗耐久
                            player.gameMode.destroyBlock(pos);
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
