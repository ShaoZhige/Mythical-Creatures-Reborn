package com.shao.mythicalcreatures.event;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.item.SetBonusManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID)
public class ModEvents {

    private static final Map<Player, Integer> tickCounter = new HashMap<>();

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            SetBonusManager.checkAllSets(player);
        }
    }

    /** 兜底：每20tick检查一次，处理登录/重生等边缘情况 */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        int count = tickCounter.getOrDefault(player, 0) + 1;
        tickCounter.put(player, count);
        if (count % 20 == 0) {
            SetBonusManager.checkAllSets(player);
        }
    }

    /** 云宝套装：免疫摔落伤害 */
    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player
                && SetBonusManager.isWearingFullSet(player, "rainbow_dash")) {
            event.setCanceled(true);
        }
    }
}
