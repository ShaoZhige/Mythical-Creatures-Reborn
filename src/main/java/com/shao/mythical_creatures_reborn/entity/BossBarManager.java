package com.shao.mythical_creatures_reborn.entity;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.entity.custom.*;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 为指定的「大型 boss」实体显示屏幕顶部的 boss 血条。
 * 用 Forge 事件总线集中管理：实体加入世界时建血条、每 tick 刷新进度与可见玩家、
 * 死亡/离场时移除。新增 boss 只需往 BOSS_CLASSES 集合加一个类，无需改动任何实体代码。
 *
 * Shows a top-screen boss health bar for the large "boss" entities. Entirely event-driven
 * via the Forge bus: create the bar on world-join, refresh progress and visibility each
 * tick, remove on death/leave. Adding a boss is just appending its class to BOSS_CLASSES —
 * no entity code changes required.
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BossBarManager {

    private static final Set<Class<? extends LivingEntity>> BOSS_CLASSES = new HashSet<>(Arrays.asList(
            SpikezillaEntity.class, HydraEntity.class,
            WindigoEntity.class, ManticoreEntity.class, UrsamajorEntity.class,
            ChiefThunderhoovesEntity.class,
            GarbleEntity.class,
            KingbowserEntity.class
    ));

    /**
     * 判断一个实体类是否属于「boss」（显示血条的大型 boss）。
     * 供实体侧复用同一份 boss 清单（如近战击退增强），避免两处各自维护一份名单。
     */
    public static boolean isBoss(Class<? extends LivingEntity> cls) {
        return BOSS_CLASSES.contains(cls);
    }

    private static final Map<LivingEntity, ServerBossEvent> BARS = new WeakHashMap<>();

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        Entity e = event.getEntity();
        if (e instanceof LivingEntity living && BOSS_CLASSES.contains(e.getClass())) {
            ServerBossEvent bar = new ServerBossEvent(living.getDisplayName(),
                    BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
            bar.setProgress(living.getHealth() / living.getMaxHealth());
            BARS.put(living, bar);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        ServerBossEvent bar = BARS.remove(event.getEntity());
        if (bar != null) bar.removeAllPlayers();
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        ServerBossEvent bar = BARS.remove(event.getEntity());
        if (bar != null) bar.removeAllPlayers();
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide()) return;
        // 注意：LevelTickEvent 没有 getLevel()，直接读公共字段 event.level。
        // Note: LevelTickEvent exposes the level via the public field `event.level`, not getLevel().
        ServerLevel level = (ServerLevel) event.level;

        Iterator<Map.Entry<LivingEntity, ServerBossEvent>> it = BARS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<LivingEntity, ServerBossEvent> entry = it.next();
            LivingEntity ent = entry.getKey();
            ServerBossEvent bar = entry.getValue();
            if (!ent.isAlive()) {
                bar.removeAllPlayers();
                it.remove();
                continue;
            }
            if (ent.level() != level) continue; // 由其所在维度自行处理（每个维度各自 tick 一遍）
            // Skip entities in other dimensions; each dimension ticks its own boss bars separately.
            bar.setProgress(ent.getHealth() / ent.getMaxHealth());
            for (ServerPlayer player : level.players()) {
                if (player.distanceTo(ent) < 200.0D) bar.addPlayer(player);
                else bar.removePlayer(player);
            }
        }
    }
}
