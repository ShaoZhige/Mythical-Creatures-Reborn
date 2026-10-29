package com.shao.mythical_creatures_reborn.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * 模组统一按键绑定 —— 所有按键均可在"选项→控制"中修改。
 * 默认均为左 Shift，与原版蹲下键一致，不会冲突。
 * 
 * All keybindings configurable in Options → Controls.
 * Defaults to Left Shift (same as vanilla crouch), no conflicts.
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeyBindings {

    public static final String CATEGORY = "key.categories.mythical_creatures_reborn";

    /** 技能键 / Ability Key — 武器第二技能、工具特殊交互 / weapon alt-fire, tool special */
    public static final KeyMapping ABILITY = new KeyMapping(
            "key.mythical_creatures_reborn.ability",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            CATEGORY
    );

    /** 坐骑下降键 / Mount Descend — 飞行坐骑下降，默认 V */
    public static final KeyMapping MOUNT_DESCEND = new KeyMapping(
            "key.mythical_creatures_reborn.mount_descend",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    /** 配置编辑器 / Config Editor — 默认 Ctrl+H（需 OP 权限 2，单人主机默认满足；可在控制菜单改成单键或其他组合键） */
    public static final KeyMapping MOB_STATS = new KeyMapping(
            "key.mythical_creatures_reborn.stats",
            KeyConflictContext.UNIVERSAL,
            KeyModifier.CONTROL,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            CATEGORY
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(ABILITY);
        event.register(MOUNT_DESCEND);
        event.register(MOB_STATS);
    }
}
