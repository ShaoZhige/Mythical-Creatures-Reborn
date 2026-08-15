package com.shao.mythicalcreatures.network;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * 模组网络通道（SimpleChannel）。目前仅用于同步「坐骑下降键（V）」等自定义按键状态。
 * 参考同组参考模组 DiexvSword 的 SimpleChannel 骨架。
 */
public final class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";
    private static int id = 0;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MythicalCreaturesMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private ModNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(
                id++,
                MountDescendPacket.class,
                MountDescendPacket::encode,
                MountDescendPacket::decode,
                MountDescendPacket::handle
        );
    }
}
