package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.util.KeyStateHelper;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * 飞行坐骑的骑乘逻辑统一 API。
 *
 * 紫悦 / 云宝 / 柔柔等会飞的坐骑实体直接 override 对应的 Forge 骑乘方法，
 * 但方法体只做一行委托调用本类——飞行物理、起飞/悬停状态机、翅膀扇动等全部集中在此，
 * 避免在三处实体里复制同一套代码。
 *
 * 骑乘调参（ridden_speed_factor / vertical_* / horizontal_factor / inertia_decay / wing_*）已缓存在
 * 实体字段（见 PonyEntity.cacheRideTuning），本类直接读取缓存，不再每 tick 查配置。
 *
 * Unified riding API for flying mounts. Flying mounts (Twilight Sparkle / Rainbow Dash /
 * Fluttershy) override the relevant Forge riding hooks and delegate here, so all flight
 * physics and the takeoff/hover state machine live in one place instead of being copied
 * into three entities. Ride-tuning is read from cached entity fields (PonyEntity.cacheRideTuning),
 * so this class no longer hits the config every tick.
 */
public final class FlightRideAPI {

    /** 在地面按跳跃键起飞时的向上初速度 */
    private static final double TAKEOFF_VELOCITY = 0.5;

    private FlightRideAPI() {}

    /** 对应 PlayerRideable#getRiddenInput —— 把玩家按键映射为骑乘输入向量 */
    public static @NotNull Vec3 getRiddenInput(PonyEntity self, @NotNull Player player, @NotNull Vec3 v) {
        float fwd = player.zza;
        float str = (float)(player.xxa * self.rideSpeedFactor);
        if (fwd <= 0) fwd *= 0.25F; // 倒车比前进慢（×0.25）| Reversing is slower than forward (×0.25)
        return new Vec3(str, v.y, fwd);
    }

    /** 对应 PlayerRideable#getRiddenSpeed
     *  空中飞行/悬停时返回 FLYING_SPEED（飞行速度，本就该比走路快）；
     *  地面骑乘时返回 移动速度 × ridden_speed_factor（与陆地小马同一套公式，
     *  避免把飞行速度属性当地面速度用，导致地面也飞快）。 */
    public static float getRiddenSpeed(PonyEntity self) {
        if (self.isFlying() || self.isHovering()) {
            // 飞行/悬停时用 FLYING_SPEED（本就该比走路快）
            // Use FLYING_SPEED in the air — it should naturally exceed ground speed.
            return (float) self.getAttributeValue(Attributes.FLYING_SPEED);
        }
        // 地面：移动速度 × ridden_speed_factor，避免误用飞行速度导致地面飞快
        // On the ground: MOVEMENT_SPEED × ridden_speed_factor (never reuse the faster flying speed).
        return (float)(self.getAttributeValue(Attributes.MOVEMENT_SPEED)
                * self.rideSpeedFactor);
    }

    /** 对应 PlayerRideable#tickRidden —— 飞行/悬停时的垂直控制（空格升、V 降、无输入缓降） */
    public static void tickRidden(PonyEntity self, @NotNull Player player, @NotNull Vec3 v) {
        self.setYRot(player.getYRot());
        self.yRotO = self.yBodyRot = self.yHeadRot = self.getYRot();
        if (self.isFlying() || self.isHovering()) {
            float vert = KeyStateHelper.isJumpKeyDown(player)
                    ? (float) self.rideVerticalUp
                    : KeyStateHelper.isMountDescendDown(player)
                    ? (float) self.rideVerticalDown
                    : (float) self.rideVerticalHover;
            self.setDeltaMovement(self.getDeltaMovement().add(0, vert, 0));
        }
    }

    /**
     * 对应 Mob#travel 的飞行分支。
     * @return true 表示已处理飞行物理（调用方不要再走 super.travel）；
     *         false 表示当前非飞行状态，调用方应自行执行 super.travel（普通地面移动 + 重力）。
     */
    public static boolean flyingRideTravel(PonyEntity self, @NotNull Vec3 v) {
        if (self.isVehicle() && self.getControllingPassenger() instanceof Player) {
            if (self.isFlying() || self.isHovering()) {
                self.setNoGravity(true);
                self.fallDistance = 0;
                // 只在权威端（服务端 AI）或本地控制端移动，避免旁观客户端本地移动坐骑造成抖动
                if (self.isEffectiveAi() || self.isControlledByLocalInstance()) {
                    float s = (float)(getRiddenSpeed(self) * self.rideHorizontalFactor);
                    self.moveRelative(s, new Vec3(v.x, 0, v.z));
                    self.move(MoverType.SELF, self.getDeltaMovement());
                }
                self.setDeltaMovement(self.getDeltaMovement().scale((float) self.rideInertiaDecay));
                if (self.getY() > self.level().getMaxBuildHeight() + 4)
                    self.setDeltaMovement(self.getDeltaMovement().add(0, -0.5, 0));
                return true;
            }
        }
        return false;
    }

    /**
     * 对应 tick() 的骑乘分支：地面正常行走、按跳跃键起飞；滞空进入飞行/悬停控制；并持续扇动翅膀。
     * @return true 表示当前处于被骑乘状态（已处理）；false 表示未被骑乘，调用方应走自主飞行 tickFlight()。
     */
    public static boolean tickRiddenFlight(PonyEntity self) {
        if (!(self.isVehicle() && self.getControllingPassenger() instanceof Player player)) return false;

        boolean inFlight = self.isFlying() || self.isHovering();
        if (inFlight) {
            if (KeyStateHelper.isJumpKeyDown(player)) {
                // 按跳跃键：持续上升（起飞后第一帧即使仍贴地也不退出，避免误判为落地）
                // Keep ascending while jump held; don't drop out on the first takeoff frame just
                // because we're still touching the ground (avoids a false "landed" reset).
                self.setFlying(true);
                self.setHovering(false);
            } else if (self.onGround()) {
                // 已落回地面：退出飞行/悬停状态，恢复正常地面物理与重力
                self.setFlying(false);
                self.setHovering(false);
            } else {
                // 空中松开跳跃：进入悬停缓降
                self.setFlying(false);
                self.setHovering(true);
            }
        } else {
            // 地面模式：强制维持地面状态，绝不因 onGround() 抖动而误入悬停（避免骑乘抽搐）
            self.setFlying(false);
            self.setHovering(false);
            if (KeyStateHelper.isJumpKeyDown(player)) {
                // 地面按跳跃键 = 主动起飞
                self.setFlying(true);
                self.setDeltaMovement(self.getDeltaMovement().x, TAKEOFF_VELOCITY, self.getDeltaMovement().z);
            }
        }

        // 骑乘时持续扇动翅膀（保持原视觉）
        self.wingFlapTicks = (float)((self.wingFlapTicks + PonyEntity.GLOBAL_WING_FLAP_SPEED) % 360.0);
        return true;
    }
}
