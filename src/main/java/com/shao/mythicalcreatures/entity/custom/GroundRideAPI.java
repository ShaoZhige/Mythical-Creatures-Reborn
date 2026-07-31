package com.shao.mythicalcreatures.entity.custom;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.WeakHashMap;

/**
 * 地面小马骑乘（含跳跃）的通用实现，统一以苹果嘉儿为标准。
 *
 * 苹果嘉儿 / 圣光辉辉 / 碧琪 / 珍奇等地面坐骑实体直接 override 对应的 Forge 骑乘方法，
 * 但方法体只做一行委托调用本类——速度、按键映射、转向同步、跳跃执行等全部集中在此，
 * 避免在多处实体里复制同一套代码。
 *
 * 移动速度与跳跃高度已缓存在实体字段（见 PonyEntity.cacheRideTuning），本类直接读取，
 * 不再每 tick 查配置。
 *
 * Common ground-riding implementation (incl. jumping), standardized on Applejack.
 * Ground mounts (Applejack / Holy Light / Pinkie Pie / Rarity) override the relevant Forge
 * riding hooks and delegate here, centralizing speed, input mapping, rotation sync and jump
 * execution. Speed and jump height are read from cached entity fields (PonyEntity.cacheRideTuning),
 * so this class no longer hits the config every tick.
 */
public final class GroundRideAPI {

    /** 本帧跳跃蓄力（跨 onPlayerJump → tickRidden 的临时状态，按骑乘实体实例存） */
    // Jump charge is transient state shared across onPlayerJump → tickRidden, so it is kept
    // per-riding-instance in a WeakHashMap keyed by the PonyEntity (not in the base class).
    private static final WeakHashMap<PonyEntity, Float> JUMP_SCALE = new WeakHashMap<>();

    private GroundRideAPI() {}

    private static float getJumpScale(@NotNull PonyEntity self) {
        return JUMP_SCALE.getOrDefault(self, 0.0F);
    }

    private static void setJumpScaleValue(@NotNull PonyEntity self, float v) {
        JUMP_SCALE.put(self, v);
    }

    /** 对应 PlayerRideable#getRiddenSpeed —— 骑乘速度 = 实体移动速度 × ridden_speed_factor */
    public static float getRiddenSpeed(@NotNull PonyEntity self) {
        return (float)(self.getAttributeValue(Attributes.MOVEMENT_SPEED) * self.rideSpeedFactor);
    }

    /** 对应 PlayerRideable#getRiddenInput —— 把玩家按键映射为骑乘输入向量 */
    public static @NotNull Vec3 getRiddenInput(@NotNull PonyEntity self, @NotNull Player player, @NotNull Vec3 v) {
        float fwd = player.zza;
        float str = player.xxa * 0.5F;
        if (fwd <= 0) fwd *= 0.25F;
        return new Vec3(str, v.y, fwd);
    }

    /**
     * 对应 PlayerRideable#tickRidden 的旋转同步 + 跳跃执行部分。
     * 调用方需保留 super.tickRidden(player, v) 的基础逻辑，再调本方法。
     */
    public static void tickRidden(@NotNull PonyEntity self, @NotNull Player player, @NotNull Vec3 travelVector) {
        Vec2 rot = new Vec2(player.getXRot() * 0.5F, player.getYRot());
        self.setYRot(rot.y);
        self.setXRot(rot.x);
        self.yRotO = self.yBodyRot = self.yHeadRot = self.getYRot();

        if (self.isControlledByLocalInstance() && self.onGround()) {
            self.setJumping(false);
            float scale = getJumpScale(self);
            if (scale > 0.0F) {
                executeRidersJump(self, scale, travelVector);
            }
            setJumpScaleValue(self, 0.0F);
        }
    }

    private static void executeRidersJump(@NotNull PonyEntity self, float scale, @NotNull Vec3 travelVector) {
        double jumpY = self.rideJumpHeight * scale + (double)self.getJumpBoostPower();
        // jumpY = 配置基础跳跃高度 × 蓄力比例 + 跳跃提升附魔加成 | base jump height × charge + Jump Boost
        Vec3 delta = self.getDeltaMovement();
        self.setDeltaMovement(delta.x, jumpY, delta.z);
        self.setJumping(true);
        self.hasImpulse = true;
        if (travelVector.z > 0.0D) {
            float f = Mth.sin(self.getYRot() * Mth.DEG_TO_RAD);
            float f1 = Mth.cos(self.getYRot() * Mth.DEG_TO_RAD);
            self.setDeltaMovement(self.getDeltaMovement().add(-0.4F * f * scale, 0.0D, 0.4F * f1 * scale));
        }
    }

    /** 对应 PlayerRideableJumping#canJump —— 仅驯服后可跳 */
    public static boolean canJump(@NotNull PonyEntity self) {
        return self.isTame();
    }

    /** 对应 PlayerRideableJumping#onPlayerJump —— 客户端蓄力回调 */
    public static void onPlayerJump(@NotNull PonyEntity self, int jumpPower) {
        if (jumpPower > 10) setJumpScale(self, jumpPower);
    }

    /** 对应 PlayerRideableJumping#handleStartJump —— 服务端不重复设跳跃（由客户端 onPlayerJump→tickRidden 执行并通过数据包同步） */
    public static void handleStartJump(@NotNull PonyEntity self, int jumpPower) {}

    /** 对应 PlayerRideableJumping#handleStopJump */
    public static void handleStopJump(@NotNull PonyEntity self) {}

    private static void setJumpScale(@NotNull PonyEntity self, int jumpPower) {
        int p = jumpPower < 0 ? 0 : jumpPower;
        if (p >= 90) setJumpScaleValue(self, 1.0F);
        else         setJumpScaleValue(self, 0.4F + 0.4F * (float)p / 90.0F);
    }
}
