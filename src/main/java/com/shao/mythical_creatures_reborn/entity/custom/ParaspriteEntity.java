package com.shao.mythical_creatures_reborn.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 帕拉斯prite：小型飞行敌对生物，行为对齐原版蜜蜂 —— **常驻空中、永不落地**。
 *
 * <p>飞行刻意**不走** {@link PonyFlight} 的 ASCENT→HOVER→DESCENT 观光循环：那套是"偶尔起飞观光、
 * 落地冷却后再起飞"，而 parasprite 的动画文件里**没有 walk 片段**（只有 idle / fly / attack），
 * 一旦落地就会因为缺 walk 而僵在 idle。这里改为覆写 {@link #tickFlight()}：起飞爬升到巡航高度后
 * **永久悬停**，只抵消重力、叠加轻微上下浮动，并把水平速度留给 AI。</p>
 *
 * <p>被打落地、或是刚生成在地面上，都会自动重新起飞 —— 因此它任何时候都在天上。</p>
 */
public class ParaspriteEntity extends HostilePonyEntity {

    /** 起飞爬升的目标高度（格）：离地约 3.5 格转入悬停，接近蜜蜂的巡航高度。 */
    private static final double CRUISE_HEIGHT = 3.5D;
    /** 爬升速度（方块/tick）。 */
    private static final double ASCENT_SPEED = 0.06D;
    /** 起飞瞬间向上初速度（方块/tick），给一点"弹起来"的观感。 */
    private static final double TAKEOFF_IMPULSE = 0.30D;
    /** 悬停时的上下浮动：幅度（方块/tick）与角频率（弧度/tick）。 */
    private static final double BOB_AMPLITUDE = 0.012D;
    private static final double BOB_FREQUENCY = 0.12D;
    /** 追击速度相对 {@code FLYING_SPEED} 属性值的比例（直接用属性值会快得离谱）。 */
    private static final double CHASE_SPEED_SCALE = 0.30D;
    /** 追击时保持的水平距离（格）：近到这个程度就不再继续贴脸飞。 */
    private static final double CHASE_STOP_DISTANCE = 1.6D;

    public ParaspriteEntity(EntityType<ParaspriteEntity> type, Level level) {
        super(type, level);
    }

    /** 会飞：像蜜蜂一样常驻空中（canFly()==true 必须配套 {@link #defineFlyData()}）。 */
    @Override protected boolean canFly() { return true; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    /**
     * 飞行生物的属性表：在核心四项之上追加 {@code FLYING_SPEED}。
     * ⚠️ 该值取自配置键 {@code parasprite|fly_speed}，**必须在 MythicalConfig.ENTITY_DEFAULTS 登记**，
     * 否则静默取 0.0、实体飞不起来（已登记为 0.25）。
     */
    public static AttributeSupplier.Builder createAttributes() {
        return PonyAttributes.flying("mythical_creatures_reborn:parasprite");
    }

    /** 飞行同步数据（canFly()==true 必须调用，否则 isFlying/isHovering 读不到字段）。 */
    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        defineFlyData();
    }

    /**
     * 驱动飞行。基类**不会**自动调用 {@code tickFlight()}，会飞的子类必须自己在 tick 里调。
     */
    @Override public void tick() {
        super.tick();
        tickFlight();
    }

    /**
     * 蜜蜂式常驻飞行。
     *
     * <ul>
     *   <li>客户端：交给基类 —— 它会推进翅膀扇动然后直接返回（不跑服务端状态机）；</li>
     *   <li>不在飞行/悬停状态 → 立刻起飞（向上初速度 + 进入爬升阶段）；</li>
     *   <li>爬升：以 {@link #ASCENT_SPEED} 上升，到 {@link #CRUISE_HEIGHT} 转悬停；</li>
     *   <li>悬停：竖直速度归零抵消重力（否则每 tick −0.08 会把它拽回地面），叠加正弦浮动；</li>
     *   <li>水平速度**原样保留**并额外补一点朝目标的推力 —— MeleeAttackGoal 走地面寻路，
     *       在空中基本推不动它，不补这一下它会一直悬在原地够不到人。</li>
     * </ul>
     *
     * 全程 {@code setNoGravity(true)}：飞行期间无重力，避免重力与悬停互相拉扯（下落振荡 / 水面蹦跶）。
     */
    @Override
    protected void tickFlight() {
        if (this.level().isClientSide()) {
            super.tickFlight();
            return;
        }
        if (!this.isAlive()) return;   // 死亡后不驱动，避免尸体继续抽搐

        // 不在空中就起飞（刚生成 / 被打落地）
        if (!isFlying() && !isHovering()) {
            setFlying(false);
            setHovering(true);
            flight.flyPhase = PonyFlight.Phase.ASCENT;
            flight.flyStartY = this.getY();
            this.setDeltaMovement(this.getDeltaMovement().add(0, TAKEOFF_IMPULSE, 0));
        }

        this.setNoGravity(true);

        // 翅膀扇动（服务端也要推进：动画表现与骑乘都读它）
        flight.wingFlapTicks = (float) ((flight.wingFlapTicks + PonyEntity.GLOBAL_WING_FLAP_SPEED) % 360.0);

        Vec3 v = this.getDeltaMovement();
        double y;

        if (flight.flyPhase == PonyFlight.Phase.ASCENT) {
            // 爬升：到巡航高度转入悬停
            y = v.y + ASCENT_SPEED;
            if (this.getY() >= flight.flyStartY + CRUISE_HEIGHT) {
                flight.flyPhase = PonyFlight.Phase.HOVER;
                y = 0.0D;
            }
        } else {
            // 悬停：抵消重力 + 轻微上下浮动（蜜蜂感）
            flight.flyPhase = PonyFlight.Phase.HOVER;
            y = Math.sin(this.tickCount * BOB_FREQUENCY) * BOB_AMPLITUDE;
        }

        // 空中追击：朝目标补一点水平推力
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            Vec3 to = target.position().subtract(this.position());
            double horizontal = Math.sqrt(to.x * to.x + to.z * to.z);
            if (horizontal > CHASE_STOP_DISTANCE) {
                double speed = this.getAttributeValue(Attributes.FLYING_SPEED) * CHASE_SPEED_SCALE;
                v = v.add(new Vec3(to.x, 0.0D, to.z).normalize().scale(speed));
            }
        }

        this.setDeltaMovement(new Vec3(v.x, y, v.z));
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {
        // no ranged attack
    }
}
