package com.shao.mythicalcreatures.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 小马实体基类 — 统一动画、AI、骑乘、交互逻辑。
 * 子类只需提供属性、驯服道具、音效和各自的飞行/地面移动实现。
 */
public abstract class PonyEntity extends TamableAnimal implements GeoEntity, RangedAttackMob {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // 自主飞行状态
    protected int flyCooldown = 0;
    protected int flyDuration = 0;
    protected double flyStartY = 0;
    protected byte flyPhase = 0; // 0=上升 1=悬停 2=降落
    protected boolean angryFlight = false;
    public float wingFlapTicks = 0;

    public PonyEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        refreshConfigAttributes();
    }

    /** 从配置文件刷新属性值（子类重写调用各自的配置项） */
    protected void refreshConfigAttributes() {}

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        refreshConfigAttributes();
    }

    /* ================================================================
     * 动画（所有小马共用）
     * ================================================================ */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, this::predicate));
    }

    private <T extends PonyEntity> PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<T> state) {
        if (this.isInSittingPose()) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
        } else if (canFly() && (this.isFlying() || this.isHovering()) && !this.onGround()) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("fly"));
        } else if (state.isMoving()) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop(
                    this.isVehicle() ? "run" : "walk"));
        } else {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }
    @Nullable @Override public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) { return null; }

    /** 主人/骑手保护 + 爆炸魔法火焰闪电免疫 */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isTame() && source.getEntity() instanceof Player p && p == this.getOwner()) return false;
        if (this.isVehicle() && source.getEntity() != null
            && this.getControllingPassenger() != null && source.getEntity() == this.getControllingPassenger())
            return false;
        if (source.is(net.minecraft.world.damagesource.DamageTypes.EXPLOSION)
         || source.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_EXPLOSION)
         || source.is(net.minecraft.world.damagesource.DamageTypes.MAGIC)
         || source.is(net.minecraft.world.damagesource.DamageTypes.IN_FIRE)
         || source.is(net.minecraft.world.damagesource.DamageTypes.ON_FIRE)
         || source.is(net.minecraft.world.damagesource.DamageTypes.LAVA)
         || source.is(net.minecraft.world.damagesource.DamageTypes.HOT_FLOOR)
         || source.is(net.minecraft.world.damagesource.DamageTypes.FIREBALL)
         || source.is(net.minecraft.world.damagesource.DamageTypes.LIGHTNING_BOLT)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    /* ================================================================
     * 子类必须实现
     * ================================================================ */

    protected abstract boolean canFly();
    protected abstract Item getTamingItem();

    /** 从配置字符串解析物品，失败时退回默认值 */
    protected static Item resolveTamingItem(String configId, Item fallback) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configId));
        return item != null ? item : fallback;
    }
    @Nullable protected abstract SoundEvent getAmbientSoundEvent();
    @Nullable protected abstract SoundEvent getHurtSoundEvent();

    /* ================================================================
     * AI（所有小马共用）
     * ================================================================ */

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.2D, 10.0F, 3.0F, false));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(getTamingItem()), false));
        this.goalSelector.addGoal(4, new RangedAttackGoal(this, 1.0D, 20, 16.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                e -> e instanceof net.minecraft.world.entity.monster.Enemy));
    }

    /* ================================================================
     * 交互 & 驯服（所有小马共用）
     * ================================================================ */

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.isEmpty()) {
            if (player.isShiftKeyDown()) {
                super.mobInteract(player, hand);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (!this.isVehicle() && !this.isOrderedToSit()) {
                if (!this.level().isClientSide) player.startRiding(this);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        if (!this.isTame() && stack.is(getTamingItem())) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.tame(player);
                this.navigation.stop();
                this.setTarget(null);
                this.level().broadcastEntityEvent(this, (byte) 7);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    /* ================================================================
     * 骑手定位（所有小马共用）
     * ================================================================ */

    @Override
    protected void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (this.hasPassenger(passenger)) {
            this.yBodyRot = this.getYRot();
            this.setYHeadRot(passenger.getYHeadRot());
            this.setYBodyRot(passenger.getYRot());
        }
        Vec3 back = this.getLookAngle().scale(-0.5);
        float riderY = 0.6F;
        passenger.setPos(this.getX() + back.x, this.getY() + riderY, this.getZ() + back.z);
    }

    @Nullable @Override
    public LivingEntity getControllingPassenger() {
        for (Entity p : this.getPassengers())
            if (p instanceof Player player && this.isOwnedBy(player))
                return player;
        return null;
    }

    @Override public boolean isAlliedTo(@NotNull Entity entity) {
        if (this.isTame()) {
            LivingEntity owner = this.getOwner();
            if (entity == owner) return true;
            if (entity instanceof TamableAnimal ta && ta.isOwnedBy(owner)) return true;
            if (owner != null) return owner.isAlliedTo(entity);
        }
        return super.isAlliedTo(entity);
    }

    /* ================================================================
     * 杂项（所有小马共用）
     * ================================================================ */

    @Override public boolean causeFallDamage(float distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) { return false; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) {}
    @Nullable @Override protected SoundEvent getAmbientSound() { return getAmbientSoundEvent(); }
    @Nullable @Override protected SoundEvent getHurtSound(@NotNull net.minecraft.world.damagesource.DamageSource s) { return getHurtSoundEvent(); }
    @Override public int getAmbientSoundInterval() { return 200; }

    /* ================================================================
     * 飞行同步数据（仅 canFly()==true 的小马使用）
     * ================================================================ */

    protected void defineFlyData() {
        this.entityData.define(Flying.DATA_FLYING, false);
        this.entityData.define(Flying.DATA_HOVERING, false);
    }

    public boolean isFlying()  { return canFly() && this.entityData.get(Flying.DATA_FLYING); }
    public void setFlying(boolean v)  { if (canFly()) this.entityData.set(Flying.DATA_FLYING, v); }
    public boolean isHovering() { return canFly() && this.entityData.get(Flying.DATA_HOVERING); }
    public void setHovering(boolean v) { if (canFly()) this.entityData.set(Flying.DATA_HOVERING, v); }

    /**
     * 把飞行同步数据和受保护的 defineSynchedData 分离，避免子类遗漏 super 调用。
     * 需要飞行的子类：在 defineSynchedData 中调用 defineFlyData()。
     */
    protected static final class Flying {
        static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> DATA_FLYING =
                SynchedEntityData.defineId(PonyEntity.class, EntityDataSerializers.BOOLEAN);
        static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> DATA_HOVERING =
                SynchedEntityData.defineId(PonyEntity.class, EntityDataSerializers.BOOLEAN);
    }

    /* ================================================================
     * 自主飞行状态机（canFly() 子类使用，被子类 tick() 调用）
     * ================================================================ */

    protected double getFlightAscentSpeed()     { return 0.05D; }
    protected double getFlightDescendSpeed()    { return -0.03D; }
    protected double getFlightMaxHeight()       { return 3.0D; }
    protected int    getFlightHoverDuration()   { return 80; }
    protected int    getFlightChance()          { return 1000; }
    protected int    getFlightCooldownMin()     { return 200; }
    protected int    getFlightCooldownMax()     { return 400; }
    protected int    getFlightDurationMin()     { return 60; }
    protected int    getFlightDurationMax()     { return 100; }
    protected int    getAngryFlightChance()     { return 30; }
    protected int    getAngryFlightAscentDuration() { return 40; }

    protected void tickFlight() {
        if (!canFly()) return;

        // 翅膀动画（客户端 + 服务端）
        if ((this.isFlying() || this.isHovering()) && !this.isVehicle())
            this.wingFlapTicks = (float)((this.wingFlapTicks + com.shao.mythicalcreatures.config.MythicalConfig.DATA.get("global_params", "wing_flap_speed", 0.4)) % 360.0);
        else if (!this.isVehicle())
            this.wingFlapTicks = (float)Math.max(0, this.wingFlapTicks - com.shao.mythicalcreatures.config.MythicalConfig.DATA.get("global_params", "wing_decay_speed", 0.15));

        if (this.level().isClientSide()) return;

        boolean hasTarget = this.getTarget() != null && this.getTarget().isAlive();
        boolean aiBusy = this.getNavigation().isInProgress();
        if (this.isFlying() || this.isHovering()) {
            switch (flyPhase) {
                case 0: // 上升
                    this.flyDuration--;
                    this.setDeltaMovement(this.getDeltaMovement().add(0, this.angryFlight ? 0.05D : getFlightAscentSpeed(), 0));
                    if (this.flyDuration <= 0 || this.getY() >= this.flyStartY + getFlightMaxHeight() || aiBusy) {
                        flyPhase = 1;
                        this.flyDuration = this.angryFlight ? 300 : getFlightHoverDuration();
                        this.setDeltaMovement(Vec3.ZERO);
                    }
                    break;
                case 1: // 悬停
                    this.flyDuration--;
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 1.0, 0.5));
                    // 有仇恨时刷新悬停时间，基本不下落
                    if (this.angryFlight && hasTarget && this.flyDuration <= 60)
                        this.flyDuration = 200;
                    if (this.flyDuration <= 0) flyPhase = 2;
                    break;
                case 2: // 降落
                    // 有仇恨时不下落，回到悬停
                    if (this.angryFlight && hasTarget) {
                        flyPhase = 1;
                        this.flyDuration = 200;
                        this.setDeltaMovement(Vec3.ZERO);
                        break;
                    }
                    this.setDeltaMovement(this.getDeltaMovement().add(0, this.angryFlight ? -0.03D : getFlightDescendSpeed(), 0));
                    if (this.onGround()) {
                        this.setFlying(false); this.setHovering(false);
                        flyPhase = 0;
                        this.angryFlight = false;
                        this.flyCooldown = getFlightCooldownMin() + this.random.nextInt(Math.max(1, getFlightCooldownMax() - getFlightCooldownMin()));
                    }
                    break;
            }
        } else {
            if (this.onGround() && this.flyCooldown > 0) this.flyCooldown--;
            // 有仇恨时高概率起飞
            boolean isAngry = hasTarget && this.onGround() && this.flyCooldown <= 0 && this.random.nextInt(4) == 0;
            if (!aiBusy && this.onGround() && this.flyCooldown <= 0 && this.getPassengers().isEmpty() && !this.isOrderedToSit()
                && (isAngry || this.random.nextInt(getFlightChance()) == 0)) {
                this.angryFlight = isAngry || (hasTarget && !isAngry && this.random.nextBoolean());
                this.setHovering(true);
                flyPhase = 0;
                flyStartY = this.getY();
                flyDuration = isAngry ? 30
                    : getFlightDurationMin() + this.random.nextInt(Math.max(1, getFlightDurationMax() - getFlightDurationMin()));
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.45, 0));
            }
        }
    }
}
