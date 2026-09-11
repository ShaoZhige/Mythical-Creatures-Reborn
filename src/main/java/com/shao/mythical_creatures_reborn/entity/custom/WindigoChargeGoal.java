package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.util.EntityHateFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * 雪魔冲锋：进入战斗后持续累积"仇恨计时"（AGGRO_BUILDUP），达标后再逐 tick 按 TRIGGER_ODDS
 * 随机抽选，掷中即向目标加速冲刺（与麋鹿同一套"随机起冲"手感，但多了仇恨门槛）。
 * 冲刺途中：
 *  · 对路径上的非友方实体造成高额单次伤害（每个实体一次，判定盒为雪魔中心的小盒，不隔空打人）；
 *  · 破坏途经的一切方块（无类型/硬度/工具限制——连"不可破坏"的方块也照拆），
 *    统一走原版 {@code Level#destroyBlock}（原版破坏 + 原版 2001 碎块粒子），
 *    掉落按 DROP_CHANCE 概率生成（避免刷出海量物品实体；尊重 mobGriefing，关掉则不作祟）；
 *  · 播放 attack 动画（同步到客户端，冲锋结束即停止）。
 *
 * 与麋鹿 MooseChargeGoal 的差异（按需求定制）：
 *  1. 触发条件是"吸引仇恨累积到阈值后、再按概率抽选"（混合式），而非麋鹿那样只靠随机抽选；
 *  2. 破坏方块不限于锄/斧可采，而是沿途所有方块；
 *  3. 雪魔是飞行单位，冲刺方向同时含垂直分量（朝目标的 3D 方向冲），而非纯水平。
 *
 * 旗标 MOVE + LOOK，优先级高于 WindigoSkyChaseGoal，冲锋期间接管移动；结束后交还追击。
 *
 * Windigo charge: builds up aggro while it has a target; once charged, dashes at the target.
 * Along the way it deals heavy single-hit damage and destroys every block in its path
 * (no type/hardness/tool restriction), still respecting the mobGriefing game rule.
 */
public class WindigoChargeGoal extends Goal {

    // ── 可调参数 ──────────────────────────────────────────────────────
    /** 仇恨累积时长（tick）：持续锁定目标约 1.5 秒后进入"仇恨已满、可起冲"状态 */
    private static final int AGGRO_BUILDUP = 30;
    /**
     * 仇恨满后每 tick 的随机起冲概率分母（1/N）。
     *
     * 与麋鹿 {@code MooseChargeGoal.TRIGGER_ODDS} 同一套设计：仇恨满后不是立刻必冲，
     * 而是每 tick 掷一次骰子，让冲刺时机自然、不呆板。12 ≈ 每 0.6 秒掷中一次。
     */
    private static final int TRIGGER_ODDS = 12;
    /**
     * 失去目标时仇恨的衰减间隔（tick）：每 20 tick（≈1 秒）才掉 1 点。
     *
     * 【为什么必须减速衰减】雪魔霰弹一轮 15~25 发 × 15 点伤害（单轮 225~375），
     * 任何目标都是**瞬间秒杀**，于是"目标死亡 → 重新索敌"的空窗期极频繁（每轮约 10~20 tick）。
     * 若照旧逐 tick 衰减，杀掉一个目标就掉 10~20 点、锁定期间只涨约 12 点，
     * 净增长为负 → 计时刻永远攒不满 → **冲锋永不触发**（这就是"几乎看不到它冲刺"的根因）。
     * 减速衰减后空窗期几乎不损失进度，仇恨能稳定积累。
     */
    private static final int AGGRO_DECAY_INTERVAL = 20;
    /** 单次冲锋持续 tick（约 0.9 秒 @20tps）。冲锋距离 = 此值 × CHARGE_SPEED，调小可减少沿途破坏量。 */
    private static final int CHARGE_DURATION = 18;
    /** 冲锋结束后的冷却（tick，与麋鹿 MooseChargeGoal.COOLDOWN 对齐，约 1.25 秒） */
    private static final int COOLDOWN = 25;
    /** 起冲所需最小距离：太近不冲，交给正常追击/射击 */
    private static final double MIN_CHARGE_DIST = 4.0D;
    /** 冲锋冲刺速度（方块/tick，远高于追击 move_speed） */
    private static final double CHARGE_SPEED = 1.8D;
    /** 冲锋伤害 = 实体攻击力 × 此倍率 */
    private static final double CHARGE_DAMAGE_MULT = 1.5D;
    /** 途经破坏方块的取样半边长（格）：以雪魔中心为原点，破坏 (2R+1)³ 的小体素盒。 */
    private static final int DESTROY_RADIUS = 2;
    /**
     * 命中判定半边长（格）：以雪魔中心为原点的小盒，与 DESTROY_RADIUS 口径一致。
     * 不能用物理碰撞箱（20×33×8）膨胀——那会得到 22×35×10 的判定体积，等于隔空打人。
     */
    private static final double HIT_RADIUS = 2.5D;
    /**
     * 掉落概率（0~1）：破坏方块时每个方块生成掉落物的概率。
     * 冲锋沿途破坏量很大，全掉落会刷出海量物品实体拖垮 TPS，故按概率掉落。
     *
     * 这是控制"破坏代价"的唯一旋钮——粒子保持原版 destroyBlock 的每格一次，不做削减。
     */
    private static final double DROP_CHANCE = 0.1D;

    private final WindigoEntity mob;
    private int aggroTimer;      // 仇恨累积计时
    private int chargeTime;      // 冲锋剩余 tick
    private int cooldown;
    private LivingEntity chargeTarget;
    private double chargeDamage;
    /** 本次冲锋已命中的实体，保证每个实体只受一次伤害 */
    private final Set<Integer> hitThisCharge = new HashSet<>();

    public WindigoChargeGoal(WindigoEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            // 失去目标：按 AGGRO_DECAY_INTERVAL 缓慢衰减（不是逐 tick）。
            // 雪魔秒杀目标后的空窗期很频繁，逐 tick 衰减会让仇恨计时永远攒不满 → 冲锋不触发。
            if (this.aggroTimer > 0 && this.mob.tickCount % AGGRO_DECAY_INTERVAL == 0) this.aggroTimer--;
            return false;
        }
        // 仇恨累积与距离无关：只要有存活目标就递增。
        // （若把距离判定放在递增之前，玩家一直贴着雪魔打时计时会永久冻结，冲锋永不触发。）
        if (this.aggroTimer < AGGRO_BUILDUP) {
            this.aggroTimer++;
            return false;
        }
        // 仇恨已满：太近不起冲（避免贴脸空撞），但保持满计时，一旦拉开距离继续掷骰。
        if (this.mob.distanceToSqr(target) < MIN_CHARGE_DIST * MIN_CHARGE_DIST) return false;
        // 随机起冲（对齐麋鹿 MooseChargeGoal 的设计）：每 tick 掷一次骰子，
        // 掷中即冲。实际节奏 ≈ 累积(30t) + 期望等待(12t) + 冲锋(18t) + 冷却(25t) ≈ 85 tick ≈ 4.3 秒一次。
        return this.mob.getRandom().nextInt(TRIGGER_ODDS) == 0;
    }

    @Override
    public void start() {
        this.aggroTimer = 0;
        this.chargeTime = CHARGE_DURATION;
        this.chargeTarget = this.mob.getTarget();
        this.chargeDamage = this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE) * CHARGE_DAMAGE_MULT;
        this.hitThisCharge.clear();
        // 冲锋期间播放 attack 动画（服务端置位 → 同步给客户端渲染；结束在 stop() 里清除）
        this.mob.setAttackAnimation(true);
    }

    @Override
    public boolean canContinueToUse() {
        return this.chargeTime > 0 && this.chargeTarget != null && this.chargeTarget.isAlive();
    }

    @Override
    public void stop() {
        this.cooldown = COOLDOWN;
        this.chargeTarget = null;
        this.hitThisCharge.clear();
        this.mob.setAttackAnimation(false);
    }

    @Override
    public void tick() {
        // 全程只用 start() 时锁定的同一个目标引用：否则"续用条件看旧目标、移动朝向实时新目标"
        // 会在目标切换时造成冲锋半途拐弯或骤停。
        LivingEntity target = this.chargeTarget;
        if (target == null || !target.isAlive()) return;

        this.chargeTime--;

        // 朝目标的 3D 方向加速冲刺（含垂直分量：雪魔在空中，直接朝目标冲）
        double dx = target.getX() - this.mob.getX();
        double dy = target.getY(0.5D) - this.mob.getY(0.5D);
        double dz = target.getZ() - this.mob.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist > 1.0E-4D) {
            this.mob.setDeltaMovement(dx / dist * CHARGE_SPEED,
                                      dy / dist * CHARGE_SPEED,
                                      dz / dist * CHARGE_SPEED);
        }
        // 冲锋期间保持无重力 & 不降落（框架 tickFlight 的 angryFlight 悬停在此处由本 Goal 覆盖）
        this.mob.setNoGravity(true);
        this.mob.fallDistance = 0;
        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        breakBlocksAlong();
        damageEntitiesAlong(this.chargeDamage);
    }

    /**
     * 破坏冲锋路径上的一切方块（无类型限制，连"不可破坏"方块也拆），尊重 mobGriefing。
     *
     * 统一调用原版 {@code Level#destroyBlock}：破坏、掉落、方块实体移除、邻居更新、
     * 原版 2001 碎块粒子全部与玩家挖方块一致，不自定义任何环节。
     *
     * 注意：雪魔碰撞箱极大（20×33×8），若直接用它取样会一口气拆掉巨大的截面。
     * 这里改用"雪魔中心的一个小体素盒"（DESTROY_RADIUS 半边长）沿路径取样，破坏范围合理。
     */
    private void breakBlocksAlong() {
        if (this.mob.level().isClientSide()) return;
        if (!this.mob.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return;
        if (!(this.mob.level() instanceof ServerLevel level)) return;

        // 沿"上一 tick 位置 → 当前位置"的连线取样，避免高速冲锋时穿过方块中间而漏拆。
        Vec3 cur = this.mob.position();
        Vec3 prev = new Vec3(this.mob.xo, this.mob.yo, this.mob.zo);
        Vec3 delta = cur.subtract(prev);
        int steps = Math.max(1, (int) Math.ceil(delta.length() / 0.5D)); // 每 0.5 格取一个采样点

        for (int s = 0; s <= steps; s++) {
            Vec3 p = prev.add(delta.scale((double) s / steps));
            BlockPos center = BlockPos.containing(p);
            int r = DESTROY_RADIUS;
            for (int x = center.getX() - r; x <= center.getX() + r; x++) {
                for (int y = center.getY() - r; y <= center.getY() + r; y++) {
                    for (int z = center.getZ() - r; z <= center.getZ() + r; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = level.getBlockState(pos);
                        if (state.isAir()) continue;
                        // 掉落按概率生成，避免刷出海量物品实体。
                        // 例外：含方块实体的方块（箱子/熔炉/漏斗……）必定掉落——内容物由原版
                        // onRemove（ChestBlock → Containers#dropContents）掉落，与 drop 无关；
                        // 这里保证容器本体也一起掉，不至于本体消失只剩一地散落的内容物。
                        boolean drop = state.hasBlockEntity()
                                || this.mob.getRandom().nextDouble() < DROP_CHANCE;
                        // 走原版破坏流程：无论 drop 真假，destroyBlock 都会广播 2001 碎块粒子。
                        level.destroyBlock(pos, drop, this.mob);
                    }
                }
            }
        }
    }

    /** 对冲锋路径上的非友方实体造成高额单次伤害（已命中者本次不再结算）。 */
    private void damageEntitiesAlong(double dmg) {
        // 命中体积 = 以"上一 tick → 当前 tick"连线为轴、半径 HIT_RADIUS 的小盒，与破坏取样同口径。
        // 不要用 getBoundingBox()：雪魔碰撞箱 20×33×8，膨胀后 22×35×10 会打到侧向 11 格、
        // 头顶 17 格外的东西（隔空打人）。
        Vec3 cur = this.mob.position().add(0.0D, this.mob.getBbHeight() * 0.5D, 0.0D);
        Vec3 prev = new Vec3(this.mob.xo, this.mob.yo + this.mob.getBbHeight() * 0.5D, this.mob.zo);
        AABB box = new AABB(prev, cur).inflate(HIT_RADIUS);
        for (Entity e : this.mob.level().getEntities(this.mob, box)) {
            if (!(e instanceof LivingEntity living)) continue;
            if (!isHostileTo(living)) continue;
            if (!this.hitThisCharge.add(e.getId())) continue; // 本次冲锋只命中一次
            living.hurt(this.mob.damageSources().mobAttack(this.mob), (float) dmg);
        }
    }

    /** 目标是否可被攻击（排除仇恨过滤实体、主人、同类友方）。 */
    private boolean isHostileTo(LivingEntity living) {
        if (!living.isAlive()) return false;
        if (EntityHateFilter.shouldIgnore(living)) return false;
        if (this.mob.isTame() && living == this.mob.getOwner()) return false;
        if (living.isAlliedTo(this.mob)) return false;
        return true;
    }
}
