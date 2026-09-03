package com.shao.mythical_creatures_reborn.network;

import com.shao.mythical_creatures_reborn.util.KeyStateHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 客户端 → 服务端：同步「坐骑跳跃键（空格）是否按下」。
 * 服务端 ServerPlayer 的 jumping 字段不可靠同步，且在 Forge+Connector 环境下
 * LivingEntityJumpAccessor 这类 accessor mixin 不会织入 ServerPlayer（强转必崩），
 * 因此改走与坐骑下降键（V）一致的自定义网络包：客户端在空格状态变化时发包，
 * 服务端存入 {@link KeyStateHelper} 供飞行坐骑的垂直控制（起飞/上升）读取。
 */
public class MountJumpPacket {

    private final boolean jump;

    public MountJumpPacket(boolean jump) {
        this.jump = jump;
    }

    public static void encode(MountJumpPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.jump);
    }

    public static MountJumpPacket decode(FriendlyByteBuf buf) {
        return new MountJumpPacket(buf.readBoolean());
    }

    public static void handle(MountJumpPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                KeyStateHelper.setJumpState(sender.getUUID(), msg.jump);
            }
        });
        context.setPacketHandled(true);
    }
}
