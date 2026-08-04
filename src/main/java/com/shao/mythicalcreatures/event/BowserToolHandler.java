package com.shao.mythicalcreatures.event;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.util.KeyStateHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
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
        if (!KeyStateHelper.isAbilityKeyDown(player) && isInteractive(state)) return;

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

    // 有 GUI 的常见交互方块（潜行才替换）；有方块实体的（箱子/熔炉等）在 isInteractive 内单独判断
    private static final Set<Block> INTERACTIVE_BLOCKS = Set.of(
            Blocks.CRAFTING_TABLE, Blocks.ENCHANTING_TABLE, Blocks.ANVIL,
            Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.NOTE_BLOCK,
            Blocks.JUKEBOX, Blocks.BEACON, Blocks.LOOM, Blocks.CARTOGRAPHY_TABLE,
            Blocks.GRINDSTONE, Blocks.STONECUTTER, Blocks.SMITHING_TABLE,
            Blocks.FLETCHING_TABLE, Blocks.BELL
    );

    private static boolean isInteractive(BlockState state) {
        // 有方块实体的（箱子、熔炉等）或有 GUI 的
        if (state.hasBlockEntity()) return true;
        return INTERACTIVE_BLOCKS.contains(state.getBlock());
    }
}
