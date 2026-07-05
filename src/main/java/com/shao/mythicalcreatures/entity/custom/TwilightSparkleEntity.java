package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TwilightSparkleEntity extends TamableAnimal implements GeoEntity {

    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(TwilightSparkleEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Ingredient TEMPT_ITEM = Ingredient.of(ModItems.TWILIGHT_CUTIEMARK.get());

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int flyCooldown = 0;
    private int flyDuration = 0;
    private double flyStartY = 0;
    public float wingFlapTicks = 0;

    public TwilightSparkleEntity(EntityType<? extends TwilightSparkleEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, this::predicate));
    }

    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<TwilightSparkleEntity> state) {
        if (this.isFlying()) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.FLYING"));
        } else if (state.isMoving()) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.WALKING"));
        } else {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.BREATHING"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }

    public boolean isFlying() {
        return this.entityData.get(DATA_FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(DATA_FLYING, flying);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.8D, 60));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLYING, false);
    }

    /** 右键用可爱标志驯服 */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!this.isTame() && stack.is(ModItems.TWILIGHT_CUTIEMARK.get())) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.tame(player);
                this.navigation.stop();
                this.setTarget(null);
                this.level().broadcastEntityEvent(this, (byte) 7);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    /** 已被驯服时无视伤害 */
    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (this.isTame() && source.getEntity() instanceof Player player && player == this.getOwner()) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            if (this.isFlying()) {
                this.flyDuration--;
                this.wingFlapTicks += 0.5F;
                double maxY = this.flyStartY + 4.0;
                if (this.flyDuration <= 0) {
                    if (this.onGround()) {
                        this.setFlying(false);
                        this.flyCooldown = 200 + this.random.nextInt(400);
                    } else {
                        Vec3 motion = this.getDeltaMovement();
                        this.setDeltaMovement(motion.x, -0.08, motion.z);
                    }
                } else if (this.getY() < maxY) {
                    Vec3 motion = this.getDeltaMovement();
                    this.setDeltaMovement(motion.x, 0.15, motion.z);
                } else {
                    Vec3 motion = this.getDeltaMovement();
                    this.setDeltaMovement(motion.x, 0, motion.z);
                }
            } else {
                this.wingFlapTicks = 0;
                if (this.onGround() && this.flyCooldown > 0) {
                    this.flyCooldown--;
                }
                if (this.onGround() && this.flyCooldown <= 0 && this.random.nextInt(200) == 0) {
                    this.setFlying(true);
                    this.flyStartY = this.getY();
                    this.flyDuration = 160 + this.random.nextInt(200);
                    Vec3 motion = this.getDeltaMovement();
                    this.setDeltaMovement(motion.x, 0.5, motion.z);
                }
            }
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }
}
