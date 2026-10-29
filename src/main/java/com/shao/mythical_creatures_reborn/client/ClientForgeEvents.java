package com.shao.mythical_creatures_reborn.client;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.item.RainbowDashSword;
import com.shao.mythical_creatures_reborn.network.ModNetwork;
import com.shao.mythical_creatures_reborn.network.MountDescendPacket;
import com.shao.mythical_creatures_reborn.network.MountJumpPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端 FORGE 总线事件处理（游戏事件，非 MOD 加载事件）
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

    /** 上次下降键（V）状态，用于只在状态变化时发包 */
    private static boolean lastDescendDown = false;

    /** 上次跳跃键（空格）状态，用于只在状态变化时发包 */
    private static boolean lastJumpDown = false;

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        ItemStack stack = event.getEntity().getItemInHand(event.getHand());
        if (stack.getItem() instanceof RainbowDashSword) {
            // 手持云宝剑时取消实体交互，改用剑的技能
            event.setCanceled(true);
        }
    }

    /** 下降键（V）/ 跳跃键（空格）状态变化时同步给服务端（服务端读不到自定义按键，需网络包） */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        boolean descend = ModKeyBindings.MOUNT_DESCEND.isDown();
        if (descend != lastDescendDown) {
            lastDescendDown = descend;
            ModNetwork.CHANNEL.sendToServer(new MountDescendPacket(descend));
        }
        // 跳跃键（空格）状态变化时同步；服务端不再用 accessor 读 jumping（Forge+Connector 下 ServerPlayer 未织入会崩）
        boolean jump = Minecraft.getInstance().options.keyJump.isDown();
        if (jump != lastJumpDown) {
            lastJumpDown = jump;
            ModNetwork.CHANNEL.sendToServer(new MountJumpPacket(jump));
        }
    }
}
