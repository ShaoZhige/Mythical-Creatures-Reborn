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

@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID)
public class ModEvents {

    /** 套装加成的兜底检查周期（tick）。 */
    private static final int SET_BONUS_CHECK_INTERVAL = 20;

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            SetBonusManager.checkAllSets(player);
        }
    }

    /**
     * 定期检查：每 {@value #SET_BONUS_CHECK_INTERVAL} tick 一次，覆盖登录/重生等未触发装备变更事件的边缘情况。
     *
     * <p>直接用实体自带的 {@link Player#tickCount} 取模，不需要再自己维护一张
     * 「玩家 → 计数器」的 WeakHashMap（原先每 tick 对每个玩家做一次 map 读写，纯属多余状态）。</p>
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.tickCount % SET_BONUS_CHECK_INTERVAL == 0) {
            SetBonusManager.checkAllSets(event.player);
        }
    }

    /** 退出时清理坐骑按键等其他模块的玩家级状态，避免 WeakHashMap / Map 暂留 */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        KeyStateHelper.clearDescendState(event.getEntity().getUUID());
        KeyStateHelper.clearJumpState(event.getEntity().getUUID());
        CutieMarkHandler.clearPlayerState(event.getEntity().getUUID());
        SetBonusManager.clearPlayerState(event.getEntity().getUUID());
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
