package com.shao.mythical_creatures_reborn.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class TwilightMagicEntity extends Mob implements GeoEntity {

    private static final int MAX_LIFE = 600;
    private static final double SEARCH_RANGE = 16.0;
    private static final double CHASE_SPEED = 0.45;
    private static final int HIT_COOLDOWN = 8;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private LivingEntity target;
    private int lastHitTick;
    private UUID ownerUUID = UUID.randomUUID();

    public TwilightMagicEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 15, true);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    /* ── GeckoLib 动画 ── */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, this::predicate));
    }

    private <T extends TwilightMagicEntity> PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<T> state) {
        state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
        return PlayState.CONTINUE;
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    /* ── 基础 ── */
    @Override protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false); nav.setCanFloat(true); nav.setCanPassDoors(true);
        return nav;
    }

    @Override public void travel(Vec3 v) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            this.moveRelative(this.getSpeed(), v);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.91));
        } else { this.calculateEntityAnimation(false); }
    }

    @Override public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean canBeCollidedWith() { return false; }
    @Override public boolean isNoGravity() { return true; }

    /** 免疫来自主人（含主人投掷物及其爆炸等附加效果）的所有伤害，避免友伤。 */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            Entity trueSource = source.getEntity();        // 伤害最终来源（投掷者 / 攻击者）
            Entity directSource = source.getDirectEntity(); // 直接实体（投掷物 / 爆炸物 / 攻击者本体）
            if (trueSource == owner || directSource == owner) return false;
            // 主人投掷的弹射物（星光弹 / 不稳定物品 / 流星火球等）：直接实体是该投掷物、其 owner 是主人；
            // 其爆炸伤害的 source 实体 = 爆炸物本体，同样被这条拦截。
            if (directSource instanceof Projectile pj && pj.getOwner() == owner) return false;
        }
        return super.hurt(source, amount);
    }

    /** 免疫着火：主人投掷物的点燃附加效果（凤凰羽毛 / 流星火球）对魔法团不生效。 */
    @Override
    public void setRemainingFireTicks(int ticks) { /* 免疫着火 */ }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:twilight_magic", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:twilight_magic", "move_speed"))
                .add(Attributes.FLYING_SPEED, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:twilight_magic", "fly_speed"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("mythical_creatures_reborn:twilight_magic", "follow_range", 20.0));
    }

    /* ── Owner ── */
    public void setOwner(LivingEntity owner) { this.ownerUUID = owner.getUUID(); }
    @Nullable public LivingEntity getOwner() {
        if (this.level() instanceof ServerLevel sl) {
            Entity e = sl.getEntity(this.ownerUUID);
            return e instanceof LivingEntity ? (LivingEntity) e : null;
        }
        return null;
    }

    /* ── AI ── */
    @Override protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ChaseTargetGoal(this));
        this.goalSelector.addGoal(2, new FindTargetGoal(this));
        this.goalSelector.addGoal(3, new OrbitOwnerGoal(this));
    }

    /* ── Tick ── */
    @Override public void tick() {
        super.tick();
        this.setNoGravity(true);
        if (!this.level().isClientSide && this.tickCount >= MAX_LIFE) { this.discard(); return; }

        if (this.level().isClientSide) {
            for (int i = 0; i < 1; i++)
                this.level().addParticle(ParticleTypes.ENCHANT,
                        this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0, 0, 0);
        }

        // 撞击伤害
        if (!this.level().isClientSide && this.target != null && this.target.isAlive()
                && this.tickCount - lastHitTick >= HIT_COOLDOWN) {
            if (this.getBoundingBox().inflate(0.6).intersects(this.target.getBoundingBox())) {
                this.target.hurt(this.damageSources().magic(),
                        (float) MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:twilight_magic", "attack_damage"));
                this.target.invulnerableTime = 0;
                lastHitTick = this.tickCount;
                Vec3 away = this.position().subtract(this.target.position()).normalize().scale(0.8);
                this.setDeltaMovement(away);
            }
        }
    }

    /* ================================================================
     * FindTargetGoal — 主动找敌对生物 + 主人正在攻击的目标
     * ================================================================ */
    static class FindTargetGoal extends Goal {
        private final TwilightMagicEntity e;
        FindTargetGoal(TwilightMagicEntity e) { this.e = e; setFlags(EnumSet.of(Flag.TARGET)); }

        @Override public boolean canUse() { return e.target == null || !e.target.isAlive(); }

        @Override public void tick() {
            if (e.tickCount % 10 != 0) return;

            // 1. 优先：主人（若为玩家）正在攻击的生物
            LivingEntity owner = e.getOwner();
            if (owner instanceof Player p && p.getLastHurtMob() != null
                    && p.getLastHurtMob().isAlive()
                    && p.getLastHurtMob() != e) {
                e.target = p.getLastHurtMob();
                return;
            }

            // 2. 其次：范围内敌对生物（排除主人/同类）
            List<LivingEntity> mobs = e.level().getEntitiesOfClass(LivingEntity.class,
                    new AABB(e.blockPosition()).inflate(SEARCH_RANGE),
                    m -> (m instanceof Enemy)
                         && m.isAlive()
                         && m != owner
                         && !(m instanceof TwilightMagicEntity));
            if (!mobs.isEmpty()) {
                LivingEntity closest = null;
                double best = Double.MAX_VALUE;
                for (LivingEntity m : mobs) {
                    double d = e.distanceToSqr(m);
                    if (d < best) { best = d; closest = m; }
                }
                e.target = closest;
            }
        }
    }

    /* ================================================================
     * ChaseTargetGoal — 追踪撞击
     * ================================================================ */
    static class ChaseTargetGoal extends Goal {
        private final TwilightMagicEntity e;
        ChaseTargetGoal(TwilightMagicEntity e) { this.e = e; setFlags(EnumSet.of(Flag.MOVE)); }
        @Override public boolean canUse() { return e.target != null && e.target.isAlive(); }

        @Override public void tick() {
            if (e.target == null) return;
            Vec3 tp = e.target.position().add(0, e.target.getBbHeight() / 2, 0);
            Vec3 dir = tp.subtract(e.position()).normalize().scale(CHASE_SPEED);
            e.setDeltaMovement(e.getDeltaMovement().add(dir).scale(0.94));
            e.getLookControl().setLookAt(e.target);
            e.setDeltaMovement(e.getDeltaMovement().add(
                    (e.random.nextDouble() - 0.5) * 0.15,
                    (e.random.nextDouble() - 0.5) * 0.15,
                    (e.random.nextDouble() - 0.5) * 0.15));
        }
    }

    /* ================================================================
     * OrbitOwnerGoal — 绕着主人转圈圈
     * ================================================================ */
    static class OrbitOwnerGoal extends Goal {
        private final TwilightMagicEntity e;
        private double angle = 0;

        OrbitOwnerGoal(TwilightMagicEntity e) { this.e = e; setFlags(EnumSet.of(Flag.MOVE)); }

        @Override public boolean canUse() { return e.target == null || !e.target.isAlive(); }

        @Override public void tick() {
            LivingEntity owner = e.getOwner();
            if (owner == null) {
                // 没主人就随机飘
                if (e.tickCount % 15 == 0) randomMove();
                return;
            }

            // 防止掉世界底部
            if (e.position().y < e.level().getMinBuildHeight() + 3)
                e.setDeltaMovement(e.getDeltaMovement().add(0, 0.15, 0));

            // 绕主人旋转
            angle += 0.08;
            double radius = 3.0 + Math.sin(angle * 0.7) * 1.5; // 半径在 1.5~4.5 之间波动
            double h = 2.0 + Math.sin(angle * 0.5) * 1.0;      // 高度波动

            Vec3 center = owner.position().add(
                    Math.cos(angle) * radius,
                    h,
                    Math.sin(angle) * radius);

            Vec3 to = center.subtract(e.position()).normalize().scale(0.2);
            e.setDeltaMovement(e.getDeltaMovement().add(to).scale(0.95));
            e.getLookControl().setLookAt(owner);
        }

        private void randomMove() {
            if (e.position().y < e.level().getMinBuildHeight() + 3)
                e.setDeltaMovement(e.getDeltaMovement().add(0, 0.15, 0));
            if (e.tickCount % 15 == 0) {
                double ty = Math.max(e.position().y + (e.random.nextDouble() - 0.3) * 6,
                        e.level().getMinBuildHeight() + 3);
                e.getMoveControl().setWantedPosition(
                        e.position().x + (e.random.nextDouble() - 0.5) * 10, ty,
                        e.position().z + (e.random.nextDouble() - 0.5) * 12, 0.6);
            }
            e.setDeltaMovement(e.getDeltaMovement().add(
                    (e.random.nextDouble() - 0.5) * 0.08,
                    (e.random.nextDouble() - 0.5) * 0.06,
                    (e.random.nextDouble() - 0.5) * 0.08));
        }
    }
}
