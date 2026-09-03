package com.shao.mythical_creatures_reborn.util;

import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按键状态检测工具。客户端检查自定义按键映射，服务端用通过输入包/自定义网络包同步的等价状态。
 * Key state detection: client checks custom keymapping, server uses the packet-synced equivalent.
 */
public class KeyStateHelper {

    /**
     * 服务端持有的「坐骑下降键（V）」状态，按玩家 UUID 存。
     * 客户端在 V 键状态变化时通过 MountDescendPacket 发包，服务端写入这里，
     * 供飞行坐骑的服务端垂直控制读取（玩家退出时由 ModEvents 清理）。
     */
    private static final Map<UUID, Boolean> DESCEND_STATE = new ConcurrentHashMap<>();

    /** 服务端接收 MountDescendPacket 时写入下降键状态 */
    public static void setDescendState(UUID player, boolean descend) {
        DESCEND_STATE.put(player, descend);
    }

    /** 玩家退出时清理下降键状态，避免残留 */
    public static void clearDescendState(UUID player) {
        DESCEND_STATE.remove(player);
    }

    /**
     * 服务端持有的「坐骑跳跃键（空格）」状态，按玩家 UUID 存。
     * 客户端在空格键状态变化时通过 MountJumpPacket 发包，服务端写入这里，
     * 供飞行坐骑的服务端垂直控制读取（玩家退出时由 ModEvents 清理）。
     * 用自定义网络包同步而非 LivingEntityJumpAccessor 读 jumping 字段：
     * 服务端 ServerPlayer 的 jumping 不可靠同步，且在 Forge+Connector 环境下
     * accessor mixin 不会织入 ServerPlayer，强转会直接 ClassCastException。
     */
    private static final Map<UUID, Boolean> JUMP_STATE = new ConcurrentHashMap<>();

    /** 服务端接收 MountJumpPacket 时写入跳跃键状态 */
    public static void setJumpState(UUID player, boolean jump) {
        JUMP_STATE.put(player, jump);
    }

    /** 玩家退出时清理跳跃键状态，避免残留 */
    public static void clearJumpState(UUID player) {
        JUMP_STATE.remove(player);
    }

    /**
     * 检测"技能键"是否按下。武器副技能、工具特殊交互使用。
     * 服务端等效于 isShiftKeyDown()，客户端会优先检查自定义按键映射。
     */
    public static boolean isAbilityKeyDown(Player player) {
        if (player.level().isClientSide) {
            return isClientAbilityDown();
        }
        return player.isShiftKeyDown();
    }

    /**
     * 检测"坐骑下降键"（V）是否按下。
     * 客户端直接读 MOUNT_DESCEND 键映射；服务端读 MountDescendPacket 同步的状态
     * （自定义按键不经过原版输入包，需自定义网络包同步，否则服务端读不到）。
     */
    public static boolean isMountDescendDown(Player player) {
        if (player.level().isClientSide) {
            return isClientMountDescendDown();
        }
        return DESCEND_STATE.getOrDefault(player.getUUID(), false);
    }

    /**
     * 检测空格/跳跃键是否按下。
     * 客户端直接读 keyJump；服务端读 MountJumpPacket 同步的状态
     * （跳跃键是自定义按键语义、需自定义网络包同步，否则服务端读不到）。
     * 不再通过 LivingEntityJumpAccessor 读 jumping 字段：服务端 ServerPlayer 的 jumping
     * 不可靠同步，且在 Forge+Connector 环境下 accessor mixin 不会织入 ServerPlayer，强转必崩。
     */
    public static boolean isJumpKeyDown(Player player) {
        if (player.level().isClientSide) {
            return isClientJumpDown();
        }
        return JUMP_STATE.getOrDefault(player.getUUID(), false);
    }

    // ===== 客户端实现 =====

    private static boolean isClientAbilityDown() {
        try {
            return com.shao.mythical_creatures_reborn.client.ModKeyBindings.ABILITY.isDown();
        } catch (NullPointerException | IllegalStateException e) {
            return false;
        }
    }

    private static boolean isClientMountDescendDown() {
        try {
            return com.shao.mythical_creatures_reborn.client.ModKeyBindings.MOUNT_DESCEND.isDown();
        } catch (NullPointerException | IllegalStateException e) {
            return false;
        }
    }

    private static boolean isClientJumpDown() {
        try {
            return net.minecraft.client.Minecraft.getInstance().options.keyJump.isDown();
        } catch (NullPointerException | IllegalStateException e) {
            return false;
        }
    }
}

