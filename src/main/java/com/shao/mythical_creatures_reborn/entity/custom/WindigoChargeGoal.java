package com.shao.mythical_creatures_reborn.entity.custom;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import com.shao.mythical_creatures_reborn.config.MythicalConfig;

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
 * 雪魔冲锋：进入战斗后持续累积"仇恨计时"（cfgInt("aggro_buildup", 30)），达标后再逐 tick 按 cfgInt("trigger_odds", 12)
 * 随机抽选，掷中即向目标加速冲刺（与麋鹿同一套"随机起冲"手感，但多了仇恨门槛）。
 * 冲刺途中：
 *  · 对路径上的非友方实体造成高额单次伤害（每个实体一次，判定盒为雪魔中心的小盒，不隔空打人）；
 *  · 破坏途经的一切方块（无类型/硬度/工具限制——连"不可破坏"的方块也照拆），
 *    统一走原版 {@code Level#destroyBlock}（原版破坏 + 原版 2001 碎块粒子），
 *    掉落按 cfg("drop_chance", 0.1) 概率生成（避免刷出海量物品实体；尊重 mobGriefing，关掉则不作祟）；
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

    /* ── 数值来自配置（键 = 本实体注册名；可在编辑器的「生物」分类里改）──
       所有 cfg/cfgInt 都带原硬编码值作 fallback，配置缺失时行为与改动前完全一致。 */
    private String eid() {
        ResourceLocation rl = BuiltInRegistries.ENTITY_TYPE.getKey(this.mob.getType());
        return rl == null ? "" : rl.toString();
    }

    private double cfg(String key, double fallback) {
        return MythicalConfig.DATA.get(eid(), key, fallback);
    }

    private int cfgInt(String key, int fallback) {
        return (int) cfg(key, fallback);
    }


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
            // 失去目标：按 cfgInt("aggro_decay_interval", 20) 缓慢衰减（不是逐 tick）。
            // 雪魔秒杀目标后的空窗期很频繁，逐 tick 衰减会让仇恨计时永远攒不满 → 冲锋不触发。
            if (this.aggroTimer > 0 && this.mob.tickCount % cfgInt("aggro_decay_interval", 20) == 0) this.aggroTimer--;
            return false;
        }
        // 仇恨累积与距离无关：只要有存活目标就递增。
        // （若把距离判定放在递增之前，玩家一直贴着雪魔打时计时会永久冻结，冲锋永不触发。）
        if (this.aggroTimer < cfgInt("aggro_buildup", 30)) {
            this.aggroTimer++;
            return false;
        }
        // 仇恨已满：太近不起冲（避免贴脸空撞），但保持满计时，一旦拉开距离继续掷骰。
        if (this.mob.distanceToSqr(target) < cfg("charge_min_dist", 4.0) * cfg("charge_min_dist", 4.0)) return false;
        // 随机起冲（对齐麋鹿 MooseChargeGoal 的设计）：每 tick 掷一次骰子，
        // 掷中即冲。实际节奏 ≈ 累积(30t) + 期望等待(12t) + 冲锋(18t) + 冷却(25t) ≈ 85 tick ≈ 4.3 秒一次。
        return this.mob.getRandom().nextInt(cfgInt("trigger_odds", 12)) == 0;
    }

    @Override
    public void start() {
        this.aggroTimer = 0;
        this.chargeTime = cfgInt("charge_duration", 18);
        this.chargeTarget = this.mob.getTarget();
        this.chargeDamage = this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE) * cfg("charge_damage_mult", 1.5);
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
        this.cooldown = cfgInt("charge_cooldown", 25);
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
            this.mob.setDeltaMovement(dx / dist * cfg("charge_speed", 1.8),
                                      dy / dist * cfg("charge_speed", 1.8),
                                      dz / dist * cfg("charge_speed", 1.8));
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
     * 这里改用"雪魔中心的一个小体素盒"（cfgInt("destroy_radius", 2) 半边长）沿路径取样，破坏范围合理。
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
            int r = cfgInt("destroy_radius", 2);
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
                                || this.mob.getRandom().nextDouble() < cfg("drop_chance", 0.1);
                        // 走原版破坏流程：无论 drop 真假，destroyBlock 都会广播 2001 碎块粒子。
                        level.destroyBlock(pos, drop, this.mob);
                    }
                }
            }
        }
    }

    /** 对冲锋路径上的非友方实体造成高额单次伤害（已命中者本次不再结算）。 */
    private void damageEntitiesAlong(double dmg) {
        // 命中体积 = 以"上一 tick → 当前 tick"连线为轴、半径 cfg("hit_radius", 2.5) 的小盒，与破坏取样同口径。
        // 不要用 getBoundingBox()：雪魔碰撞箱 20×33×8，膨胀后 22×35×10 会打到侧向 11 格、
        // 头顶 17 格外的东西（隔空打人）。
        Vec3 cur = this.mob.position().add(0.0D, this.mob.getBbHeight() * 0.5D, 0.0D);
        Vec3 prev = new Vec3(this.mob.xo, this.mob.yo + this.mob.getBbHeight() * 0.5D, this.mob.zo);
        AABB box = new AABB(prev, cur).inflate(cfg("hit_radius", 2.5));
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
