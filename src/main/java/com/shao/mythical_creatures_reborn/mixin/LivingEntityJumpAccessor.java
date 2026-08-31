package com.shao.mythical_creatures_reborn.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 暴露 LivingEntity.jumping（protected 字段）的读取。
 * <p>
 * 服务端（ServerPlayer）的 jumping 字段会通过 ServerboundPlayerInputPacket 同步为「玩家是否按跳跃键」，
 * 供飞行坐骑的服务端垂直控制使用——客户端直接读键盘 keyJump，服务端通过本 accessor 读同一语义的同步值，
 * 避免服务端读不到跳跃键导致飞行状态失效。
 * </p>
 */
@Mixin(LivingEntity.class)
public interface LivingEntityJumpAccessor {
    @Accessor("jumping")
    boolean mythical_creatures_reborn$getJumping();
}
