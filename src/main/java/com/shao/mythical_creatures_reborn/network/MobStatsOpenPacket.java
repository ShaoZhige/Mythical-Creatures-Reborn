package com.shao.mythical_creatures_reborn.network;

import com.shao.mythical_creatures_reborn.client.gui.MainConfigScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：打开主配置界面（提供「服务端配置 / 客户端配置」入口）。
 * 快照不再由服务端下发——服务端配置可视化编辑器在客户端本地构建快照
 * （数据源为 COMMON 配置 + 同步注册表，两端一致）。
 *
 * <p>⚠ 本类在专用服务器上也会被加载（ModNetwork.register() 注册网络通道时）。
 * 包里**不能**出现任何客户端类型：不能 {@code new MainConfigScreen(...)}（那会让服务端
 * 校验字节码时被迫加载 Screen → dist 检查失败、启动即崩），只能留一条无参 void 的
 * 静态调用，交给客户端侧自己判断。</p>
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
        context.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                MainConfigScreen.openMain();
            }
        });
        context.setPacketHandled(true);
    }
}
