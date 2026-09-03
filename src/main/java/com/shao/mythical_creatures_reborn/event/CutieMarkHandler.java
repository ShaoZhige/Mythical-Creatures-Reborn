package com.shao.mythical_creatures_reborn.event;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.effect.ModEffects;
import com.shao.mythical_creatures_reborn.item.ModItems;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 可爱标志 Buff：无限时长，1.5 秒检查一次，放入给予、取出移除。
 * 移除时只移除与可爱标志等级相同的效果，不影响外源高等 Buff。
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CutieMarkHandler {

    private record EffectInfo(Supplier<MobEffect> effect, int amplifier) {}

    /** 可爱标志 → 效果表 */
    private static final Map<RegistryObject<? extends Item>, EffectInfo[]> CUTIEMARK_EFFECTS = new LinkedHashMap<>();

    /** 玩家 UUID → 各可爱标志的连续「未找到」计数，用于防抖移除 buff。 */
    private static final Map<UUID, Map<Item, Integer>> MISS_COUNTERS = new HashMap<>();

    /** 连续多少次（每次间隔 30 tick ≈ 1.5 秒）未找到可爱标志才移除其 buff。 */
    private static final int REMOVE_AFTER_MISSES = 3;

    static {
        CUTIEMARK_EFFECTS.put(ModItems.APPLEJACK_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.DIG_SPEED, 1)});
        CUTIEMARK_EFFECTS.put(ModItems.PINKIE_PIE_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.JUMP, 1)});
        CUTIEMARK_EFFECTS.put(ModItems.FLUTTERSHY_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.REGENERATION, 0)});
        CUTIEMARK_EFFECTS.put(ModItems.RARITY_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(ModEffects.REPAIR, 0)});
        CUTIEMARK_EFFECTS.put(ModItems.RAINBOW_DASH_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.MOVEMENT_SPEED, 1)});
        CUTIEMARK_EFFECTS.put(ModItems.TWILIGHT_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.WATER_BREATHING, 0),
                new EffectInfo(() -> MobEffects.NIGHT_VISION, 0)});
        CUTIEMARK_EFFECTS.put(ModItems.HOLY_LIGHT_RADIANCE_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.GLOWING, 0)});
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        if (player.tickCount % 30 != 0) return;

        // 本次检查周期内每个可爱标志的「未找到」连续计数（按物品），
        // 只有连续多次未找到才移除 buff，避免 Curios 查询偶发抖动导致 buff 闪烁。
        Map<Item, Integer> missCounts = MISS_COUNTERS.computeIfAbsent(player.getUUID(), u -> new HashMap<>());

        for (var entry : CUTIEMARK_EFFECTS.entrySet()) {
            Item item = entry.getKey().get();
            boolean found = hasItem(player, item);

            // 未找到：累计连续未命中次数；只有连续 N 次都未找到才判定为真正脱下/取出
            int misses = found ? 0 : missCounts.getOrDefault(item, 0) + 1;
            missCounts.put(item, misses);
            if (!found && misses < REMOVE_AFTER_MISSES) continue;

            for (EffectInfo info : entry.getValue()) {
                MobEffect type = info.effect().get();
                int ourLevel = info.amplifier();

                if (found) {
                    // 已有 >= 自身的同种 buff：不做任何事（不刷新，避免重复 add 触发 HUD 抖动）
                    // 已有但等级更低：addEffect 会以更高等级替换；没有：直接给予。
                    MobEffectInstance existing = player.getEffect(type);
                    if (existing != null && existing.getAmplifier() >= ourLevel) continue;
                    player.addEffect(new MobEffectInstance(type, -1, ourLevel,
                            false, false, true));
                } else {
                    // 仅移除可爱标志自身授予的无限时长（duration < 0）、且等级相同的 buff，
                    // 不影响药水等外源同种 buff。
                    MobEffectInstance current = player.getEffect(type);
                    if (current != null && current.getAmplifier() == ourLevel
                            && current.getDuration() < 0) {
                        player.removeEffect(type);
                    }
                }
            }
        }
    }

    /** 玩家退出时清理其防抖计数，避免残留占用。 */
    public static void clearPlayerState(UUID player) {
        MISS_COUNTERS.remove(player);
    }

    private static boolean hasItem(Player player, Item item) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).is(item)) return true;
        }
        if (ModList.get().isLoaded("curios")) {
            try {
                var handler = CuriosApi.getCuriosInventory(player).resolve();
                if (handler.isPresent()) {
                    var curios = handler.get();
                    for (var slotEntry : curios.getCurios().entrySet()) {
                        for (int i = 0; i < slotEntry.getValue().getStacks().getSlots(); i++) {
                            if (slotEntry.getValue().getStacks().getStackInSlot(i).is(item)) return true;
                        }
                    }
                }
            } catch (Exception e) {
                // Curios 的 inventory future 异常完成时会抛 CompletionException；此处仅跳过本次检查，避免每 30 tick 崩游戏。
                org.apache.logging.log4j.LogManager.getLogger(CutieMarkHandler.class)
                        .warn("Curios 库存查询失败，已跳过本次可爱标志检查", e);
            }
        }
        return false;
    }
}
