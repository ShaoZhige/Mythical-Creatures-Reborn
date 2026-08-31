package com.shao.mythical_creatures_reborn.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.shao.mythical_creatures_reborn.network.MobStatsOpenPacket;
import com.shao.mythical_creatures_reborn.network.ModNetwork;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.shao.mythical_creatures_reborn.MythicalCreaturesMod.MODID;

/**
 * 配置界面入口命令。
 * /mythical_creatures_reborn config_edit 打开主配置界面（服务端/客户端配置入口）；打开不需要任何权限。
 * 实际写回 common.toml 的权限由 MobStatsEditPacket / MobStatsSavePacket 的服务端处理器把关：
 * 非 OP 客户端发来的改动会被服务端忽略，服务端配置保持权威、只读取自己的配置文件。
 */
@Mod.EventBusSubscriber(modid = MODID)
public class MobStatsCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register((LiteralArgumentBuilder<CommandSourceStack>) Commands.literal("mythical_creatures_reborn")
                .then(Commands.literal("config_edit")
                        .executes(ctx -> open(ctx.getSource().getPlayer()))));
    }

    private static int open(ServerPlayer player) {
        if (player == null) return 0;
        ModNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new MobStatsOpenPacket());
        return 1;
    }
}
