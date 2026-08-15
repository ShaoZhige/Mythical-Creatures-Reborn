package com.shao.mythicalcreatures.network;

import com.shao.mythicalcreatures.util.KeyStateHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 客户端 → 服务端：同步「坐骑下降键（V）是否按下」。
 * 下降键是自定义按键，原版 ServerboundPlayerInputPacket 不会同步它，服务端读不到；
 * 客户端在按键状态变化时发本包，服务端存入 {@link KeyStateHelper} 供飞行坐骑垂直控制读取。
 */
public class MountDescendPacket {

    private final boolean descend;

    public MountDescendPacket(boolean descend) {
        this.descend = descend;
    }

    public static void encode(MountDescendPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.descend);
    }

    public static MountDescendPacket decode(FriendlyByteBuf buf) {
        return new MountDescendPacket(buf.readBoolean());
    }

    public static void handle(MountDescendPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                KeyStateHelper.setDescendState(sender.getUUID(), msg.descend);
            }
        });
        context.setPacketHandled(true);
    }
}
