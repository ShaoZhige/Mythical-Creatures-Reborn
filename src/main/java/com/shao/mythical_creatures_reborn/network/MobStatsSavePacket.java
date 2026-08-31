package com.shao.mythical_creatures_reborn.network;

import com.shao.mythical_creatures_reborn.config.MobStatsManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 客户端 → 服务端：持久化。resetAll=true 时先清空全部 override 再写盘。
 */
public class MobStatsSavePacket {

    private final boolean resetAll;

    public MobStatsSavePacket(boolean resetAll) {
        this.resetAll = resetAll;
    }

    public static void encode(MobStatsSavePacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.resetAll);
    }

    public static MobStatsSavePacket decode(FriendlyByteBuf buf) {
        return new MobStatsSavePacket(buf.readBoolean());
    }

    public static void handle(MobStatsSavePacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null || !sender.hasPermissions(2)) return;
            if (msg.resetAll) MobStatsManager.resetAll();
            MobStatsManager.save();
            // 配置重启后生效，无需实时 apply。
        });
        context.setPacketHandled(true);
    }
}
