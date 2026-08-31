package com.shao.mythical_creatures_reborn.event;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.item.SetBonusManager;
import com.shao.mythical_creatures_reborn.util.KeyStateHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID)
public class ModEvents {

    private static final Map<Player, Integer> tickCounter = new WeakHashMap<>();

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            SetBonusManager.checkAllSets(player);
        }
    }

    /** 定期检查：每 20 tick 一次，覆盖登录/重生等未触发装备变更事件的边缘情况 */
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

    /** 退出时清理计数器与下降键状态，避免 WeakHashMap / Map 暂留 */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        tickCounter.remove(event.getEntity());
        KeyStateHelper.clearDescendState(event.getEntity().getUUID());
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
