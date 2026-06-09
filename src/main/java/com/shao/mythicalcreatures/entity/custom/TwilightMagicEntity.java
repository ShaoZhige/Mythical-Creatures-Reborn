package com.shao.mythicalcreatures.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class TwilightMagicEntity extends Mob {

    private static final int MAX_LIFE = 600;
    private static final double ATTACK_RANGE = 16.0;
    private static final float DAMAGE = 8.0F;
    private static final double CHASE_SPEED = 0.45;
    private static final double RANDOM_SPEED = 0.6;
    private static final double RETURN_RANGE = 25.0;
    private static final int HIT_COOLDOWN = 8;

    @Nullable
    private LivingEntity target;
    private int lastHitTick;

    public TwilightMagicEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 15, true);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.91));
        } else {
            this.calculateEntityAnimation(false);
        }
    }

    @Override
    public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FLYING_SPEED, 0.55)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ChaseTargetGoal(this));
        this.goalSelector.addGoal(2, new FindTargetGoal(this));
        this.goalSelector.addGoal(3, new RandomFlyGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);

        if (!this.level().isClientSide && this.tickCount >= MAX_LIFE) {
            this.discard();
        }
        if (this.level().isClientSide) {
            for (int i = 0; i < 3; i++) {
                this.level().addParticle(ParticleTypes.ENCHANT,
                        this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0, 0, 0);
            }
        }
        // 撞击伤害
        if (!this.level().isClientSide && this.target != null && this.target.isAlive()
                && this.tickCount - lastHitTick >= HIT_COOLDOWN) {
            if (this.getBoundingBox().inflate(0.6).intersects(this.target.getBoundingBox())) {
                this.target.hurt(this.damageSources().magic(), DAMAGE);
                this.target.invulnerableTime = 0;
                lastHitTick = this.tickCount;
                Vec3 away = this.position().subtract(this.target.position()).normalize().scale(0.8);
                this.setDeltaMovement(away);
            }
        }
    }

    @Override public boolean isPushable() { return false; }
    @Override public boolean canBeCollidedWith() { return false; }
    @Override public boolean isNoGravity() { return true; }

    // === AI ===

    static class FindTargetGoal extends Goal {
        private final TwilightMagicEntity e;
        FindTargetGoal(TwilightMagicEntity e) { this.e = e; setFlags(EnumSet.of(Flag.TARGET)); }
        @Override public boolean canUse() { return e.target == null || !e.target.isAlive(); }
        @Override public void tick() {
            if (e.tickCount % 10 != 0) return;
            Entity owner = e.getOwner();
            List<LivingEntity> mobs = e.level().getEntitiesOfClass(LivingEntity.class,
                    e.getBoundingBox().inflate(ATTACK_RANGE),
                    m -> m instanceof Enemy && m.isAlive() && m != owner);
            if (!mobs.isEmpty()) e.target = mobs.get(e.random.nextInt(mobs.size()));
        }
    }

    static class ChaseTargetGoal extends Goal {
        private final TwilightMagicEntity e;
        ChaseTargetGoal(TwilightMagicEntity e) { this.e = e; setFlags(EnumSet.of(Flag.MOVE)); }
        @Override public boolean canUse() { return e.target != null && e.target.isAlive(); }
        @Override public void tick() {
            if (e.target == null) return;
            Vec3 tp = e.target.position().add(0, e.target.getBbHeight() / 2, 0);
            Vec3 dir = tp.subtract(e.position()).normalize().scale(CHASE_SPEED);
            // 追踪时阻尼小，更激进
            e.setDeltaMovement(e.getDeltaMovement().add(dir).scale(0.94));
            e.getLookControl().setLookAt(e.target);
            // 小幅随机偏移让轨迹更摇摆
            e.setDeltaMovement(e.getDeltaMovement().add(
                    (e.random.nextDouble() - 0.5) * 0.15,
                    (e.random.nextDouble() - 0.5) * 0.15,
                    (e.random.nextDouble() - 0.5) * 0.15));
        }
    }

    static class RandomFlyGoal extends Goal {
        private final TwilightMagicEntity e;
        RandomFlyGoal(TwilightMagicEntity e) { this.e = e; setFlags(EnumSet.of(Flag.MOVE)); }
        @Override public boolean canUse() { return e.target == null || !e.target.isAlive(); }
        @Override public void tick() {
            if (e.position().y < e.level().getMinBuildHeight() + 3) {
                e.setDeltaMovement(e.getDeltaMovement().add(0, 0.15, 0));
            }
            // 更频繁换方向 — 每 15 tick
            if (e.tickCount % 15 == 0) {
                double ty = e.position().y + (e.random.nextDouble() - 0.3) * 6;
                ty = Math.max(ty, e.level().getMinBuildHeight() + 3);
                e.getMoveControl().setWantedPosition(
                        e.position().x + (e.random.nextDouble() - 0.5) * 12,
                        ty,
                        e.position().z + (e.random.nextDouble() - 0.5) * 12,
                        RANDOM_SPEED);
            }
            // 随机微扰动
            e.setDeltaMovement(e.getDeltaMovement().add(
                    (e.random.nextDouble() - 0.5) * 0.08,
                    (e.random.nextDouble() - 0.5) * 0.06,
                    (e.random.nextDouble() - 0.5) * 0.08));
            // 离主人太远返回
            Entity owner = e.getOwner();
            if (owner != null && owner.distanceToSqr(e) > RETURN_RANGE * RETURN_RANGE) {
                Vec3 home = owner.position().add(0, 2, 0);
                Vec3 dir = home.subtract(e.position()).normalize().scale(0.3);
                e.setDeltaMovement(e.getDeltaMovement().add(dir).scale(0.93));
            }
        }
    }

    // === Owner ===
    private java.util.UUID ownerUUID = java.util.UUID.randomUUID();

    public void setOwner(Player player) { this.ownerUUID = player.getUUID(); }

    @Nullable public Entity getOwner() {
        return this.level().getPlayerByUUID(this.ownerUUID);
    }
}
