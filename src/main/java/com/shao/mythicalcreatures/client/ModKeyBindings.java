package com.shao.mythicalcreatures.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.shao.mythicalcreatures.MythicalCreaturesMod;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
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

    public static final String CATEGORY = "key.categories.mythicalcreatures";

    /** 技能键 / Ability Key — 武器第二技能、工具特殊交互 / weapon alt-fire, tool special */
    public static final KeyMapping ABILITY = new KeyMapping(
            "key.mythicalcreatures.ability",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            CATEGORY
    );

    /** 坐骑下降键 / Mount Descend — 飞行坐骑下降，默认 V */
    public static final KeyMapping MOUNT_DESCEND = new KeyMapping(
            "key.mythicalcreatures.mount_descend",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(ABILITY);
        event.register(MOUNT_DESCEND);
    }
}
