package com.shao.mythicalcreatures.util;

import net.minecraft.world.entity.player.Player;

/**
 * 按键状态检测工具。客户端检查自定义按键映射，服务端回退到 isShiftKeyDown()。
 * Key state detection: client checks custom keymapping, server falls back to isShiftKeyDown().
 */
public class KeyStateHelper {

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
     * 检测"坐骑下降键"是否按下（默认 V，仅客户端可检测）。
     * 服务端无法检测自定义按键，直接返回 false 由客户端驱动移动。
     */
    public static boolean isMountDescendDown(Player player) {
        if (player.level().isClientSide) {
            return isClientMountDescendDown();
        }
        return false;
    }

    /**
     * 检测空格/跳跃键是否按下（仅客户端可检测）。
     */
    public static boolean isJumpKeyDown(Player player) {
        if (player.level().isClientSide) {
            try {
                return net.minecraft.client.Minecraft.getInstance().options.keyJump.isDown();
            } catch (NullPointerException | IllegalStateException e) {
                return false;
            }
        }
        return false;
    }

    // ===== 客户端实现 =====

    private static boolean isClientAbilityDown() {
        try {
            return com.shao.mythicalcreatures.client.ModKeyBindings.ABILITY.isDown();
        } catch (NullPointerException | IllegalStateException e) {
            return false;
        }
    }

    private static boolean isClientMountDescendDown() {
        try {
            return com.shao.mythicalcreatures.client.ModKeyBindings.MOUNT_DESCEND.isDown();
        } catch (NullPointerException | IllegalStateException e) {
            return false;
        }
    }
}
