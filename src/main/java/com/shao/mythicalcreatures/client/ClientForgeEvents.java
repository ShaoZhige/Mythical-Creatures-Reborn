package com.shao.mythicalcreatures.client;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.item.RainbowDashSword;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端 FORGE 总线事件处理（游戏事件，非 MOD 加载事件）
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        ItemStack stack = event.getEntity().getItemInHand(event.getHand());
        if (stack.getItem() instanceof RainbowDashSword) {
            // 手持云宝剑时取消实体交互，改用剑的技能
            event.setCanceled(true);
        }
    }
}
