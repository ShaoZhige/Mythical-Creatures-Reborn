package com.shao.mythical_creatures_reborn.network;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
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
        CHANNEL.registerMessage(
                id++,
                MountJumpPacket.class,
                MountJumpPacket::encode,
                MountJumpPacket::decode,
                MountJumpPacket::handle
        );
        CHANNEL.registerMessage(
                id++,
                MobStatsOpenPacket.class,
                MobStatsOpenPacket::encode,
                MobStatsOpenPacket::decode,
                MobStatsOpenPacket::handle
        );
        CHANNEL.registerMessage(
                id++,
                MobStatsEditPacket.class,
                MobStatsEditPacket::encode,
                MobStatsEditPacket::decode,
                MobStatsEditPacket::handle
        );
        CHANNEL.registerMessage(
                id++,
                MobStatsSavePacket.class,
                MobStatsSavePacket::encode,
                MobStatsSavePacket::decode,
                MobStatsSavePacket::handle
        );
    }
}
