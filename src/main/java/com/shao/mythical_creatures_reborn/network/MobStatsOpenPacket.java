package com.shao.mythical_creatures_reborn.network;

import com.shao.mythical_creatures_reborn.client.gui.MainConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：打开主配置界面（提供「服务端配置 / 客户端配置」入口）。
 * 快照不再由服务端下发——服务端配置可视化编辑器在客户端本地构建快照
 * （数据源为 COMMON 配置 + 同步注册表，两端一致）。
 */
public class MobStatsOpenPacket {

    public MobStatsOpenPacket() {}

    public static void encode(MobStatsOpenPacket msg, FriendlyByteBuf buf) {
        // 无数据
    }

    public static MobStatsOpenPacket decode(FriendlyByteBuf buf) {
        return new MobStatsOpenPacket();
    }

    public static void handle(MobStatsOpenPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> Minecraft.getInstance().setScreen(new MainConfigScreen(null)));
        context.setPacketHandled(true);
    }
}
