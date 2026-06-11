package com.shao.mythicalcreatures.event;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.effect.ModEffects;
import com.shao.mythicalcreatures.item.ModItems;
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 可爱标志 Buff：无限时长，1.5 秒检查一次，放入给予、取出移除。
 * 移除时只移除与可爱标志等级相同的效果，不影响外源高等 Buff。
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CutieMarkHandler {

    private record EffectInfo(Supplier<MobEffect> effect, int amplifier) {}

    private static final Map<RegistryObject<? extends Item>, EffectInfo[]> CUTIEMARK_EFFECTS = new LinkedHashMap<>();

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
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        if (player.tickCount % 30 != 0) return;

        for (var entry : CUTIEMARK_EFFECTS.entrySet()) {
            Item item = entry.getKey().get();
            boolean found = hasItem(player, item);

            for (EffectInfo info : entry.getValue()) {
                MobEffect type = info.effect().get();
                int ourLevel = info.amplifier();

                if (found) {
                    MobEffectInstance existing = player.getEffect(type);
                    if (existing != null && existing.getAmplifier() >= ourLevel) continue;
                    player.addEffect(new MobEffectInstance(type, -1, ourLevel,
                            false, false, true));
                } else {
                    // 仅移除与我们等级相同的效果（不误删外源高等 Buff）
                    MobEffectInstance current = player.getEffect(type);
                    if (current != null && current.getAmplifier() == ourLevel
                            && current.getDuration() < 0) { // 无限时长 = 我们给的
                        player.removeEffect(type);
                    }
                }
            }
        }
    }

    private static boolean hasItem(Player player, Item item) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).is(item)) return true;
        }
        if (ModList.get().isLoaded("curios")) {
            var handler = CuriosApi.getCuriosInventory(player).resolve();
            if (handler.isPresent()) {
                var curios = handler.get();
                for (var slotEntry : curios.getCurios().entrySet()) {
                    for (int i = 0; i < slotEntry.getValue().getStacks().getSlots(); i++) {
                        if (slotEntry.getValue().getStacks().getStackInSlot(i).is(item)) return true;
                    }
                }
            }
        }
        return false;
    }
}
