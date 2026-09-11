package com.shao.mythical_creatures_reborn.entity.custom;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * 小马自主飞行组件：飞行状态机（ASCENT→HOVER→DESCENT）+ 飞行同步数据 + 翅膀扇动。
 *
 * 从 PonyEntity 抽出（原 136 行状态机 + 29 行同步数据）。
 * 所有飞行参数（getFlightAscentSpeed 等）与 canFly() 仍是 PonyEntity 的虚方法，子类照旧覆写；
 * 本组件通过 owner 回调读取，保证 RainbowDash / TwilightSparkle / SkullOfDoom 等零改动。
 *
 * 【重要】飞行同步数据的 EntityDataAccessor 必须静态注册，且 defineId 的 owner 参数
 * 保持 PonyEntity.class 不变，否则会改变数据 ID 分配（可能破坏存档兼容）。
 *
 * Pony autonomous-flight component: the ASCENT→HOVER→DESCENT state machine plus flight
 * sync-data and wing flapping, extracted from PonyEntity. Flight parameter methods stay
 * virtual on PonyEntity and are read back via the owner, so subclasses are untouched.
 */
public final class PonyFlight {

    /** 飞行同步数据（仅 canFly()==true 的小马使用）。owner 参数保持 PonyEntity.class，勿改。 */
    static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(PonyEntity.class, EntityDataSerializers.BOOLEAN);
    static final EntityDataAccessor<Boolean> DATA_HOVERING =
            SynchedEntityData.defineId(PonyEntity.class, EntityDataSerializers.BOOLEAN);

    /** 自主飞行状态机阶段 */
    enum Phase { ASCENT, HOVER, DESCENT }

    // ── 自主飞行魔法数字（单位/含义见各常量注释；全部硬编码、非配置驱动，改飞行手感改这里）──
    private static final int ANGRY_HOVER_DURATION = 300;            // 愤怒悬停时长(tick,≈15s)
    private static final int ANGRY_HOVER_REFRESH_THRESHOLD = 60;     // 愤怒悬停剩余≤此值时续命
    private static final int ANGRY_HOVER_REFRESH_DURATION = 200;     // 续命到的悬停时长(tick,≈10s)
    private static final int ANGRY_TAKEOFF_ASCENT = 70;              // 愤怒起飞初始上升时长(tick)，约 3.5 格升限
    private static final double TAKEOFF_IMPULSE = 0.45;             // 起飞瞬间向上初速度(方块/tick)
    private static final int ANGRY_FLIGHT_PROB_DENOM = 4;            // 有仇恨时起飞概率分母(≈25%/tick)

    private final PonyEntity owner;

    // 自主飞行状态
    int flyCooldown = 0;
    int flyDuration = 0;
    double flyStartY = 0;
    Phase flyPhase = Phase.ASCENT;
    boolean angryFlight = false;
    public float wingFlapTicks = 0;

    public PonyFlight(PonyEntity owner) {
        this.owner = owner;
    }

    /* ── 飞行同步数据 ── */

    /**
     * 注册飞行同步位（owner 参数固定 PonyEntity.class，勿改）。
     *
     * 【为什么必须是 static】{@code Entity} 的构造函数里会调用 {@code defineSynchedData()}，
     * 而 {@code PonyEntity} 的三个组件是**实例字段初始化器**——按 Java 语义，实例字段初始化器
     * 在 super() 返回之后才执行。所以 defineSynchedData 执行时 {@code owner.flight} 仍是 null，
     * 若走实例方法会抛 NPE，导致实体**根本构造不出来**（刷怪蛋右键无效、自然刷新失败）。
     * 故本方法只接收 SynchedEntityData，不触碰 owner。
     *
     * MUST be static: Entity's constructor calls defineSynchedData() before PonyEntity's
     * instance-field initializers run, so owner.flight is still null at that point.
     */
    public static void defineFlyData(SynchedEntityData data) {
        data.define(DATA_FLYING, false);
        data.define(DATA_HOVERING, false);
    }

    public boolean isFlying()   { return owner.canFly() && owner.entityDataRaw().get(DATA_FLYING); }
    public boolean isHovering() { return owner.canFly() && owner.entityDataRaw().get(DATA_HOVERING); }
    public void setFlying(boolean v)   { if (owner.canFly()) owner.entityDataRaw().set(DATA_FLYING, v); }
    public void setHovering(boolean v) { if (owner.canFly()) owner.entityDataRaw().set(DATA_HOVERING, v); }

    /* ── 翅膀扇动：供 FlightRideAPI 骑乘时与 tickFlight 自主飞行时调用 ── */

    /** 推进翅膀扇动角度（骑乘飞行时由 FlightRideAPI 调用，保持原视觉）。 */
    public void flapWhileRidden() {
        this.wingFlapTicks = (float)((this.wingFlapTicks + PonyEntity.GLOBAL_WING_FLAP_SPEED) % 360.0);
    }

    /* ── 自主飞行状态机（canFly() 子类使用，被子类 tick() 调用）── */

    public void tickFlight() {
        if (!owner.canFly()) return;
        // 死亡后立即停止驱动飞行：避免尸体继续跑状态机/翅膀动画，造成"假死抽搐"。
        if (!owner.isAlive()) return;
        // 自主飞行状态机：ASCENT→HOVER→DESCENT；有仇恨时优先悬停追击，无仇恨按 flight_chance 偶尔起飞观光。

        // 翅膀动画（客户端 + 服务端）
        if ((isFlying() || isHovering()) && !owner.isVehicle())
            this.wingFlapTicks = (float)((this.wingFlapTicks + PonyEntity.GLOBAL_WING_FLAP_SPEED) % 360.0);
        else if (!owner.isVehicle())
            this.wingFlapTicks = (float)Math.max(0, this.wingFlapTicks - PonyEntity.GLOBAL_WING_DECAY_SPEED);

        if (owner.level().isClientSide()) return;

        // 飞行/悬停状态绑定无重力：否则重力与悬停/水面逻辑互相拉扯（水面"蹦跶"、悬停下坠振荡）。
        // 走路/落地时 isFlying/isHovering=false → setNoGravity(false)，恢复重力。
        owner.setNoGravity(isFlying() || isHovering());

        // —— 水面悬停：会飞的小马脚下是水（或正泡在水里）时，保持飞行/悬停在水面上方约 1 格，
        //    不落水；漂离水面（脚下变为实体地面）后走下方正常逻辑下降落地 → 恢复走路状态。 ——
        if (!owner.isVehicle()) {
            boolean overWater = owner.level().getFluidState(owner.blockPosition().below()).is(FluidTags.WATER)
                    || owner.level().getFluidState(owner.blockPosition()).is(FluidTags.WATER);
            if (overWater) {
                // 进入水面悬停状态（保持飞行感，不下落；立即无重力避免本 tick 重力拉扯）
                if (!(isFlying() || isHovering())) {
                    setHovering(true);
                    setFlying(false);
                    this.flyPhase = Phase.HOVER;
                    owner.setNoGravity(true);
                }
                // 找脚下水顶，维持 y = 水面顶 + 1：太低抬升、太高缓降、到位稳住
                double waterTop = owner.getY();
                BlockPos.MutableBlockPos bp = new BlockPos.MutableBlockPos();
                bp.set(owner.blockPosition());
                for (int i = 0; i < 12 && bp.getY() >= owner.level().getMinBuildHeight(); i++) {
                    if (owner.level().getFluidState(bp).is(FluidTags.WATER)) {
                        waterTop = bp.getY() + 1.0D; // 水方块顶面（水面）
                        break;
                    }
                    bp.move(0, -1, 0);
                }
                double dy = (waterTop + 1.0D) - owner.getY();
                if (dy > 0.05D) {
                    owner.setDeltaMovement(owner.getDeltaMovement().add(0, 0.05D, 0));
                } else if (dy < -0.4D) {
                    owner.setDeltaMovement(owner.getDeltaMovement().add(0, -0.03D, 0));
                } else {
                    owner.setDeltaMovement(owner.getDeltaMovement().multiply(1.0D, 0.5D, 1.0D)); // 稳住垂直
                }
                return;
            }
        }

        boolean hasTarget = owner.getTarget() != null && owner.getTarget().isAlive();
        boolean aiBusy = owner.getNavigation().isInProgress();
        if (isFlying() || isHovering()) {
            switch (this.flyPhase) {
                case ASCENT:
                    this.flyDuration--;
                    owner.setDeltaMovement(owner.getDeltaMovement().add(0, this.angryFlight ? 0.05D : owner.getFlightAscentSpeed(), 0));
                    if (this.flyDuration <= 0 || owner.getY() >= this.flyStartY + owner.getFlightMaxHeight() || aiBusy) {
                        this.flyPhase = Phase.HOVER;
                        this.flyDuration = this.angryFlight ? ANGRY_HOVER_DURATION : owner.getFlightHoverDuration();
                        owner.setDeltaMovement(Vec3.ZERO);
                    }
                    break;
                case HOVER:
                    this.flyDuration--;
                    // 抵消重力：y 速度清零，保持悬停（否则重力每 tick -0.08 会把实体拉回地面）
                    owner.setDeltaMovement(new Vec3(owner.getDeltaMovement().x * 0.5, 0.0D, owner.getDeltaMovement().z * 0.5));
                    // 有仇恨时刷新悬停时间，基本不下落
                    if (this.angryFlight && hasTarget && this.flyDuration <= ANGRY_HOVER_REFRESH_THRESHOLD)
                        this.flyDuration = ANGRY_HOVER_REFRESH_DURATION;
                    if (this.flyDuration <= 0) this.flyPhase = Phase.DESCENT;
                    break;
                case DESCENT:
                    // 有仇恨时不下落，回到悬停
                    if (this.angryFlight && hasTarget) {
                        this.flyPhase = Phase.HOVER;
                        this.flyDuration = ANGRY_HOVER_REFRESH_DURATION;
                        owner.setDeltaMovement(Vec3.ZERO);
                        break;
                    }
                    owner.setDeltaMovement(owner.getDeltaMovement().add(0, this.angryFlight ? -0.03D : owner.getFlightDescendSpeed(), 0));
                    if (owner.onGround()) {
                        setFlying(false); setHovering(false);
                        this.flyPhase = Phase.ASCENT;
                        this.angryFlight = false;
                        this.flyCooldown = owner.getFlightCooldownMin() + owner.getRandom().nextInt(Math.max(1, owner.getFlightCooldownMax() - owner.getFlightCooldownMin()));
                    }
                    break;
            }
        } else {
            if (owner.onGround() && this.flyCooldown > 0) this.flyCooldown--;
            // 有仇恨时高概率起飞
            boolean isAngry = hasTarget && owner.onGround() && this.flyCooldown <= 0 && owner.getRandom().nextInt(ANGRY_FLIGHT_PROB_DENOM) == 0;
            if (!aiBusy && owner.onGround() && this.flyCooldown <= 0 && owner.getPassengers().isEmpty() && !owner.isOrderedToSit()
                && (isAngry || owner.getRandom().nextInt(Math.max(1, owner.getFlightChance())) == 0)) {
                this.angryFlight = isAngry || (hasTarget && !isAngry && owner.getRandom().nextBoolean());
                setHovering(true);
                this.flyPhase = Phase.ASCENT;
                this.flyStartY = owner.getY();
                this.flyDuration = isAngry ? ANGRY_TAKEOFF_ASCENT
                    : owner.getFlightDurationMin() + owner.getRandom().nextInt(Math.max(1, owner.getFlightDurationMax() - owner.getFlightDurationMin()));
                owner.setDeltaMovement(owner.getDeltaMovement().add(0, TAKEOFF_IMPULSE, 0));
            }
        }
    }
}
