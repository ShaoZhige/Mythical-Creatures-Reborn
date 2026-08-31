package com.shao.mythical_creatures_reborn.client;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 按键轮询 —— 挂在 FORGE 总线（游戏事件），不能用 MOD 总线（IModBusEvent）。
 * ClientTickEvent 属于游戏运行时事件，与 RegisterKeyMappingsEvent（MOD 总线）分属不同总线，
 * 因此单独成类，避免加载期 IllegalArgumentException。
 *
 * Key tick polling — registered on the FORGE bus (runtime game events), not the MOD bus.
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModKeyBindingTicker {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (ModKeyBindings.MOB_STATS.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.getConnection() != null)
                mc.getConnection().sendCommand("mythical_creatures_reborn config_edit");
        }
    }
}
