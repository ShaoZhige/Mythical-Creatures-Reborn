package com.shao.mythical_creatures_reborn.entity.custom;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import com.shao.mythical_creatures_reborn.config.MythicalConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.tags.BlockTags;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * 大麋鹿冲锋：在有仇恨目标时随机触发，朝目标高速冲刺一段固定时长。
 * 途中会破坏「锄类可采」(#minecraft:mineable/hoe) 与「斧类可采」(#minecraft:mineable/axe) 标签方块，
 * 并按方块战利品表正常掉落物品（尊重 mobGriefing 游戏规则）；对路径上的非麋鹿实体造成高额单次伤害。
 *
 * 设计要点：
 *  · 占用 MOVE + LOOK 旗标，优先级(2)高于近战(3)，冲锋期间接管移动、近战暂停，结束后交还近战逻辑。
 *  · 每次冲锋对同一实体只结算一次伤害，避免逐帧叠伤把目标秒杀。
 *  · 触发为随机抽选，且需与目标保持一定距离才会起冲，避免贴脸无意义地原地乱撞。
 */
public class MooseChargeGoal extends Goal {

    /* ── 数值来自配置（键 = 本实体注册名；可在编辑器的「生物」分类里改）──
       所有 cfg/cfgInt 都带原硬编码值作 fallback，配置缺失时行为与改动前完全一致。 */
    private String eid() {
        ResourceLocation rl = BuiltInRegistries.ENTITY_TYPE.getKey(this.moose.getType());
        return rl == null ? "" : rl.toString();
    }

    private double cfg(String key, double fallback) {
        return MythicalConfig.DATA.get(eid(), key, fallback);
    }

    private int cfgInt(String key, int fallback) {
        return (int) cfg(key, fallback);
    }


    private final AdultMooseEntity moose;
    private int chargeTime;
    private int cooldown;
    private double chargeDamage;
    /** 本次冲锋已命中的实体，保证每个实体只受一次伤害 */
    private final Set<Integer> hitThisCharge = new HashSet<>();

    public MooseChargeGoal(AdultMooseEntity moose) {
        this.moose = moose;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        LivingEntity target = this.moose.getTarget();
        if (target == null || !target.isAlive()) return false;
        // 太近则不起冲，避免原地空撞
        if (this.moose.distanceToSqr(target) < cfg("charge_min_dist", 1.8) * cfg("charge_min_dist", 1.8)) return false;
        return this.moose.getRandom().nextInt(cfgInt("trigger_odds", 25)) == 0;
    }

    @Override
    public void start() {
        this.chargeTime = cfgInt("charge_duration", 40);
        this.chargeDamage = this.moose.getAttributeValue(Attributes.ATTACK_DAMAGE) * cfg("charge_damage_mult", 2.5);
        this.hitThisCharge.clear();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.moose.getTarget();
        return target != null && target.isAlive() && this.chargeTime > 0;
    }

    @Override
    public void stop() {
        this.cooldown = cfgInt("charge_cooldown", 25);
    }

    @Override
    public void tick() {
        LivingEntity target = this.moose.getTarget();
        if (target == null || !target.isAlive()) return;

        this.chargeTime--;

        // 朝目标水平方向高速冲（保留竖直速度让重力/台阶照常作用）
        double dx = target.getX() - this.moose.getX();
        double dz = target.getZ() - this.moose.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist > 1e-4) {
            this.moose.setDeltaMovement(
                    dx / dist * cfg("charge_speed", 1.1),
                    this.moose.getDeltaMovement().y(),
                    dz / dist * cfg("charge_speed", 1.1));
        }
        this.moose.getLookControl().setLookAt(target, 30.0F, 30.0F);

        breakMineableAlong();
        damageEntitiesAlong(this.chargeDamage);
    }

    /** 破坏冲锋路径上的锄/斧可采方块，并按其战利品表正常掉落 */
    private void breakMineableAlong() {
        if (this.moose.level().isClientSide()) return;
        // 尊重 mobGriefing：关掉则不作祟，避免破坏玩家建筑
        if (!this.moose.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return;
        if (!(this.moose.level() instanceof ServerLevel level)) return;

        AABB box = this.moose.getBoundingBox().inflate(0.3D, 0.2D, 0.3D);
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir()) continue;
                    if (state.is(BlockTags.MINEABLE_WITH_HOE) || state.is(BlockTags.MINEABLE_WITH_AXE)) {
                        // 正常掉落战利品表物品（空工具 = 徒手破坏的默认掉落）
                        BlockEntity be = level.getBlockEntity(pos);
                        for (ItemStack drop : Block.getDrops(state, level, pos, be, null, ItemStack.EMPTY)) {
                            Block.popResource(level, pos, drop);
                        }
                        level.levelEvent(2001, pos, Block.getId(state)); // 破坏粒子
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    /** 对冲锋路径上的非麋鹿实体造成高额单次伤害（已命中者本次不再结算） */
    private void damageEntitiesAlong(double dmg) {
        AABB box = this.moose.getBoundingBox().inflate(0.6D, 0.4D, 0.6D);
        for (Entity e : this.moose.level().getEntities(this.moose, box)) {
            if (!(e instanceof LivingEntity living)) continue;
            if (MooseHerd.isMoose(e)) continue;                       // 不误伤族群
            if (this.moose.isTame() && e == this.moose.getOwner()) continue; // 不伤主人
            if (!this.hitThisCharge.add(e.getId())) continue;        // 本次冲锋只命中一次
            living.hurt(this.moose.damageSources().mobAttack(this.moose), (float) dmg);
        }
    }
}
