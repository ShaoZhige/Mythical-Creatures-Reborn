package com.shao.mythical_creatures_reborn.network;

import com.shao.mythical_creatures_reborn.config.MobStatsManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 客户端 → 服务端：单条属性改动（或重置）。重置时 value 忽略、comment 清空。
 */
public class MobStatsEditPacket {

    private final String target;
    private final String key;
    private final double value;
    private final boolean reset;
    private final String comment;

    public MobStatsEditPacket(String target, String key, double value, boolean reset, String comment) {
        this.target = target;
        this.key = key;
        this.value = value;
        this.reset = reset;
        this.comment = comment == null ? "" : comment;
    }

    public static void encode(MobStatsEditPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.target, 128);
        buf.writeUtf(msg.key, 64);
        buf.writeDouble(msg.value);
        buf.writeBoolean(msg.reset);
        buf.writeUtf(msg.comment, 256);
    }

    public static MobStatsEditPacket decode(FriendlyByteBuf buf) {
        return new MobStatsEditPacket(
                buf.readUtf(128), buf.readUtf(64),
                buf.readDouble(), buf.readBoolean(), buf.readUtf(256));
    }

    public static void handle(MobStatsEditPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null || !sender.hasPermissions(2)) return;
            if (msg.reset) {
                MobStatsManager.reset(msg.target, msg.key);
            } else {
                MobStatsManager.set(msg.target, msg.key, msg.value, msg.comment);
            }
            // 改动仅在内存，落盘由 MobStatsSavePacket 触发；配置重启后生效，无需实时 apply。
        });
        context.setPacketHandled(true);
    }
}
