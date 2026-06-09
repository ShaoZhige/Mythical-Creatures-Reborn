package com.shao.mythicalcreatures.event;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

/**
 * 右键方块替换为岩浆源（库巴之杆、爆炸剑/随机爆炸）
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BowserToolHandler {

    private static final Set<String> LAVA_TOOLS = Set.of(
            "mythicalcreatures:bowser_rod",
            "mythicalcreatures:explosive_sword"
    );

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        String id = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
        if (!LAVA_TOOLS.contains(id)) return;

        BlockState state = event.getLevel().getBlockState(event.getPos());

        // 不可破坏的方块（基岩、屏障等）跳过
        if (state.getDestroySpeed(event.getLevel(), event.getPos()) < 0) return;

        // 可互动的方块（箱子、工作台、门等），需要潜行才替换
        if (!player.isShiftKeyDown() && isInteractive(state)) return;

        // 1 秒冷却
        if (player.getCooldowns().isOnCooldown(stack.getItem())) return;
        player.getCooldowns().addCooldown(stack.getItem(), 20);

        if (!event.getLevel().isClientSide) {
            event.getLevel().setBlock(event.getPos(), Blocks.LAVA.defaultBlockState(), 3);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }

        player.swing(event.getHand());
        event.setCanceled(true);
    }

    private static boolean isInteractive(BlockState state) {
        // 有方块实体的（箱子、熔炉等）或有 GUI 的
        if (state.hasBlockEntity()) return true;
        // 常见无方块实体的交互方块
        return state.is(net.minecraft.world.level.block.Blocks.CRAFTING_TABLE)
                || state.is(net.minecraft.world.level.block.Blocks.ENCHANTING_TABLE)
                || state.is(net.minecraft.world.level.block.Blocks.ANVIL)
                || state.is(net.minecraft.world.level.block.Blocks.CHIPPED_ANVIL)
                || state.is(net.minecraft.world.level.block.Blocks.DAMAGED_ANVIL)
                || state.is(net.minecraft.world.level.block.Blocks.NOTE_BLOCK)
                || state.is(net.minecraft.world.level.block.Blocks.JUKEBOX)
                || state.is(net.minecraft.world.level.block.Blocks.BEACON)
                || state.is(net.minecraft.world.level.block.Blocks.LOOM)
                || state.is(net.minecraft.world.level.block.Blocks.CARTOGRAPHY_TABLE)
                || state.is(net.minecraft.world.level.block.Blocks.GRINDSTONE)
                || state.is(net.minecraft.world.level.block.Blocks.STONECUTTER)
                || state.is(net.minecraft.world.level.block.Blocks.SMITHING_TABLE)
                || state.is(net.minecraft.world.level.block.Blocks.FLETCHING_TABLE)
                || state.is(net.minecraft.world.level.block.Blocks.BELL);
    }
}
