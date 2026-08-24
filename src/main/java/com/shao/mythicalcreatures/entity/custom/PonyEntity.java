package com.shao.mythicalcreatures.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.level.block.state.BlockState;
import com.shao.mythicalcreatures.config.MythicalConfig;
import com.shao.mythicalcreatures.entity.BossBarManager;
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
    protected FlightPhase flyPhase = FlightPhase.ASCENT; // 自主飞行状态机阶段
    protected boolean angryFlight = false;
    public float wingFlapTicks = 0;

    public PonyEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        refreshConfigAttributes();
    }

    /* ================================================================
     * 骑乘调参缓存
     * 原骑乘 API 每 tick 都调 MythicalConfig.DATA.entityAttr(entityId, ...)（字符串拼接+多次
     * HashMap 查找），是项目中最热的路径。这里在实体创建/入场时按 entityId 缓存一次，
     * 骑乘 API 之后直接读字段，避免每 tick 反复查配置。重载配置后现存实体的缓存不变
     * （与 max_health 等属性重载语义一致：仅新建实体生效）。
     * Ride-tuning cache: populated once at spawn/add so the per-tick riding API reads fields
     * instead of hitting the config every tick.
     * ================================================================ */

    // 全局翅膀动画参数（所有小马共用，配置重载时刷新）
    // Global wing-animation params shared by all ponies; refreshed on config reload.
    // 包级可见：供同包 FlightRideAPI.tickRiddenFlight 直接读取缓存。
    static double GLOBAL_WING_FLAP_SPEED  = 0.4;
    static double GLOBAL_WING_DECAY_SPEED = 0.15;

    public static void refreshGlobalRideTuning() {
        GLOBAL_WING_FLAP_SPEED  = MythicalConfig.DATA.get("global_params", "wing_flap_speed", 0.4);
        GLOBAL_WING_DECAY_SPEED = MythicalConfig.DATA.get("global_params", "wing_decay_speed", 0.15);
    }

    // 每实体骑乘调参缓存（字段默认值即“无覆盖”手感，始终被 cacheRideTuning 覆盖）
    // Per-entity ride-tuning cache (defaults match the no-override feel; cacheRideTuning overwrites).
    protected double rideSpeedFactor     = 1.0;
    protected double rideVerticalUp      = 0.0;
    protected double rideVerticalDown    = 0.0;
    protected double rideVerticalHover   = 0.0;
    protected double rideHorizontalFactor = 1.0;
    protected double rideInertiaDecay    = 0.9;
    protected double rideJumpHeight      = 0.0;

    protected void cacheRideTuning(String entityId) {
        this.rideSpeedFactor     = MythicalConfig.DATA.entityAttr(entityId, "ridden_speed_factor");
        this.rideVerticalUp      = MythicalConfig.DATA.entityAttr(entityId, "vertical_up");
        this.rideVerticalDown    = MythicalConfig.DATA.entityAttr(entityId, "vertical_down");
        this.rideVerticalHover   = MythicalConfig.DATA.entityAttr(entityId, "vertical_hover");
        this.rideHorizontalFactor = MythicalConfig.DATA.entityAttr(entityId, "horizontal_factor");
        this.rideInertiaDecay    = MythicalConfig.DATA.entityAttr(entityId, "inertia_decay");
        this.rideJumpHeight      = MythicalConfig.DATA.entityAttr(entityId, "jump_height");
    }

    /** 从配置文件刷新属性值（子类重写调用各自的配置项） */
    protected void refreshConfigAttributes() {}

    /** 统一的实体注册名（如 mythicalcreatures:rainbow_dash），避免各子类硬编码字符串 */
    protected final String entityId() {
        return net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(this.getType()).toString();
    }

    /** 数据驱动掉落表：data/mythicalcreatures/loot_tables/entities/<实体id>.json。
     *  getLootTable() 是 final，实际生效的钩子是 getDefaultLootTable()。 */
    @Override
    protected ResourceLocation getDefaultLootTable() {
        String id = entityId();
        return new ResourceLocation("mythicalcreatures", "entities/" + id.substring(id.indexOf(':') + 1));
    }

    /** 应用核心属性：MAX_HEALTH / MOVEMENT_SPEED / FLYING_SPEED(仅飞行) / ATTACK_DAMAGE，并回满血 */
    protected final void applyCoreStats(String id, boolean flying) {
        var h = this.getAttribute(Attributes.MAX_HEALTH);
        if (h != null) h.setBaseValue((float) MythicalConfig.DATA.entityAttr(id, "max_health"));
        var s = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (s != null) s.setBaseValue((float) MythicalConfig.DATA.entityAttr(id, "move_speed"));
        if (flying) {
            var f = this.getAttribute(Attributes.FLYING_SPEED);
            if (f != null) f.setBaseValue((float) MythicalConfig.DATA.entityAttr(id, "fly_speed"));
        }
        var d = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (d != null) d.setBaseValue((float) MythicalConfig.DATA.entityAttr(id, "attack_damage"));
        // boss 近战击退增强：原版近战击退 = 基础 0.4（LivingEntity.hurt 内硬编码）
        // + ATTACK_KNOCKBACK × 0.5（Mob.doHurtTarget）。给 boss 设 1.0 → 额外 0.5，
        // 总击退约 0.9（≈原版 2 倍），让 boss 的挥击有明显「击飞」压迫感。非 boss 保持默认 0。
        var kb = this.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (kb != null && BossBarManager.isBoss(this.getClass())) {
            kb.setBaseValue(1.0F);
        }
        // 索敌范围下限：统一至少 32 格（config global_params.follow_range 可调）。
        // 原版 NearestAttackableTargetGoal 的索敌半径 = FOLLOW_RANGE 属性，小马默认 16 格太近，
        // 大体型敌人（大熊座/九头蛇等）稍远就锁不到；提高后远距离也能锁定（该 goal 无视线限制，
        // 与紫悦魔法用 AABB 扫实体的索敌一致）。Math.max 保留子类显式更大的值（如雪魔 48）。
        var fr = this.getAttribute(Attributes.FOLLOW_RANGE);
        if (fr != null) {
            double minFollow = MythicalConfig.DATA.get("global_params", "follow_range", 32.0);
            if (fr.getBaseValue() < minFollow) fr.setBaseValue(minFollow);
        }
        this.setHealth(this.getMaxHealth());
    }

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
        // 死亡时停止动画：让尸体交给游戏原生死亡动画处理，避免原地循环 idle 像“假死”。
        // On death, stop the animation so the corpse is handled by the vanilla death animation
        // instead of looping idle in place.
        if (!this.isAlive()) return PlayState.STOP;
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
    /* ── 小马不继承繁殖逻辑：禁止繁殖 ──
     * 原版继承链 TamableAnimal → Animal(AgeableMob) 自带繁殖体系（喂食进恋爱状态 → 配对 → 生幼崽）。
     * 小马没有繁殖设定，显式封死全部繁殖入口：不能繁殖、喂食不算食物、永不进恋爱状态、
     * 不产出幼崽（getBreedOffspring 返回 null 只是不产出，其余状态机入口亦全部封死）。
     * 驯服食物走 getTamingItem()，与 isFood 无关，不受影响。 */
    @Nullable @Override public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) { return null; }
    @Override public boolean canBreed() { return false; }
    @Override public boolean isFood(ItemStack stack) { return false; }
    @Override public boolean canFallInLove() { return false; }
    @Override public void setInLove(@Nullable Player player) { } // 永不进入恋爱状态，繁殖 tick 永不触发
    @Override public void spawnChildFromBreeding(ServerLevel level, net.minecraft.world.entity.animal.Animal mate) { } // 防御：即使误入也不产崽

    /** 主人/骑手保护 + 爆炸魔法火焰闪电免疫 */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isTame() && source.getEntity() instanceof Player p && p == this.getOwner()) return false;
        if (this.isVehicle() && source.getEntity() != null
            && this.getControllingPassenger() != null && source.getEntity() == this.getControllingPassenger())
            return false;
        // 防御魔法/火焰/爆炸/雷电免疫：仅保护“已驯服的友方”（坐骑/宠物），让它们不被友军魔法与
        // 环境火焰误伤。敌对生物与未驯服个体正常受伤 —— 否则末日颅骨（亡灵）无法像原版骷髅那样
        // 在白天被晒燃烧，且更符合“这些免疫是保护我方而非敌方”的语义。
        if (this.isTame() && (
            source.is(net.minecraft.world.damagesource.DamageTypes.EXPLOSION)
         || source.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_EXPLOSION)
         || source.is(net.minecraft.world.damagesource.DamageTypes.MAGIC)
         || source.is(net.minecraft.world.damagesource.DamageTypes.IN_FIRE)
         || source.is(net.minecraft.world.damagesource.DamageTypes.ON_FIRE)
         || source.is(net.minecraft.world.damagesource.DamageTypes.LAVA)
         || source.is(net.minecraft.world.damagesource.DamageTypes.HOT_FLOOR)
         || source.is(net.minecraft.world.damagesource.DamageTypes.FIREBALL)
         || source.is(net.minecraft.world.damagesource.DamageTypes.LIGHTNING_BOLT))) {
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
        // 注意：主动索敌“敌对目标”由子类决定 ——
        //   · 中立小马(NeutralPonyEntity) 会主动打模组敌对生物 + 原版 Enemy（防御）
        //   · 直接继承 PonyEntity 的中立生物（硬汉/梅菲斯）默认只反击、不主动狩猎（同原版中立）
        // 基类不再统一挂 NearestAttackableTargetGoal(Enemy)，避免中立生物行为比原版中立更激进。
    }

    /* ================================================================
     * 交互 & 驯服（所有小马共用）
     * ================================================================ */

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.isEmpty()) {
            if (player.isShiftKeyDown()) {
                // shift+右键：原地坐下 / 站起。
                // 注意：1.20.1 的 TamableAnimal 并不内置坐下切换（原版狼/猫/鹦鹉是各自在自己的
                // mobInteract 里实现的），所以这里参照 Wolf 的写法显式 setOrderedToSit + 停导航 + 清目标。
                if (!this.level().isClientSide) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                    // 若正在空中飞行/悬停，坐下时强制落地，避免“空中悬停”
                    if (canFly()) { this.setFlying(false); this.setHovering(false); }
                }
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
        Vec3 back = this.getLookAngle().scale(-getRiderBackOffset());
        float riderY = getRiderVerticalOffset();
        passenger.setPos(this.getX() + back.x, this.getY() + riderY, this.getZ() + back.z);
    }

    /* ── 骑手定位标准（所有小马共用，按坐骑类型分两套） ──
     * 陆地小马统一以苹果嘉儿为准；飞行小马（紫悦/云宝/柔柔）统一以紫悦为准，由各飞行实体覆写下方两个方法。
     * Rider seat offsets: ground mounts standardize on Applejack; flying mounts (Twilight/Dash/
     * Fluttershy) on Twilight Sparkle. Flight entities override the two methods below. */
    protected double getRiderBackOffset()    { return GROUND_RIDER_BACK; }
    protected float  getRiderVerticalOffset() { return GROUND_RIDER_Y; }

    // 陆地小马骑手标准（苹果嘉儿）
    public static final double GROUND_RIDER_BACK = 0.5D;
    public static final float  GROUND_RIDER_Y    = 0.6F;

    // 飞行小马骑手标准（紫悦）
    public static final double FLYING_RIDER_BACK = 0.5D;
    public static final float  FLYING_RIDER_Y    = 0.6F;

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
            if (entity instanceof TamableAnimal ta && owner != null && ta.isOwnedBy(owner)) return true;
            if (owner != null) return owner.isAlliedTo(entity);
        }
        return super.isAlliedTo(entity);
    }

    /* ================================================================
     * 杂项（所有小马共用）
     * ================================================================ */

    @Override public boolean causeFallDamage(float distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) { return false; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) {}
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
     *
     * Separate flying sync-data from the protected defineSynchedData so subclasses can't
     * accidentally skip the super call. Flying subclasses call defineFlyData() from
     * defineSynchedData().
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

    // ── 自主飞行参数（全部硬编码、非配置驱动；改飞行手感必须改这里）────────
    // 单位：速度=方块/tick；时长=tick（20 tick=1 秒）。属平衡基线，【意图未知·沿用原模组手感】，非代码派生。
    protected double getFlightAscentSpeed()     { return 0.05D; }  // 平静上升速度(方块/tick)
    protected double getFlightDescendSpeed()    { return -0.03D; } // 降落速度(负=下落)
    protected double getFlightMaxHeight()       { return 8.0D; }   // 单次观光起飞最大升限(方块)
    protected int    getFlightHoverDuration()   { return 80; }     // 平静悬停时长(tick,≈4s);被 tickFlight 调用
    protected int    getFlightChance()          { return 1000; }   // 平静起飞概率分母;被 tickFlight 调用(子类读配置覆盖)
    protected int    getFlightCooldownMin()     { return 200; }    // 落地后再起飞冷却下限(tick)
    protected int    getFlightCooldownMax()     { return 400; }    // 落地后再起飞冷却上限(tick)
    protected int    getFlightDurationMin()     { return 60; }     // 平静观光时长下限(tick)
    protected int    getFlightDurationMax()     { return 100; }    // 平静观光时长上限(tick)

    /** 自主飞行状态机阶段 */
    protected enum FlightPhase { ASCENT, HOVER, DESCENT }

    // ── 自主飞行魔法数字（单位/含义见各常量注释；全部硬编码、非配置驱动，改飞行手感改这里）──
    private static final int ANGRY_HOVER_DURATION = 300;            // 愤怒悬停时长(tick,≈15s)
    private static final int ANGRY_HOVER_REFRESH_THRESHOLD = 60;     // 愤怒悬停剩余≤此值时续命
    private static final int ANGRY_HOVER_REFRESH_DURATION = 200;     // 续命到的悬停时长(tick,≈10s)
    private static final int ANGRY_TAKEOFF_ASCENT = 70;              // 愤怒起飞初始上升时长(tick)，约 3.5 格升限
    private static final double TAKEOFF_IMPULSE = 0.45;             // 起飞瞬间向上初速度(方块/tick)
    private static final int ANGRY_FLIGHT_PROB_DENOM = 4;            // 有仇恨时起飞概率分母(≈25%/tick)

    protected void tickFlight() {
        if (!canFly()) return;
        // 死亡后立即停止驱动飞行：避免尸体继续跑状态机/翅膀动画，造成“假死抽搐”。
        if (!this.isAlive()) return;
        // 自主飞行状态机：ASCENT→HOVER→DESCENT；有仇恨时优先悬停追击，无仇恨按 flight_chance 偶尔起飞观光。

        // 翅膀动画（客户端 + 服务端）
        if ((this.isFlying() || this.isHovering()) && !this.isVehicle())
            this.wingFlapTicks = (float)((this.wingFlapTicks + GLOBAL_WING_FLAP_SPEED) % 360.0);
        else if (!this.isVehicle())
            this.wingFlapTicks = (float)Math.max(0, this.wingFlapTicks - GLOBAL_WING_DECAY_SPEED);

        if (this.level().isClientSide()) return;

        // 飞行/悬停状态绑定无重力：否则重力与悬停/水面逻辑互相拉扯（水面“蹦跶”、悬停下坠振荡）。
        // 走路/落地时 isFlying/isHovering=false → setNoGravity(false)，恢复重力。
        this.setNoGravity(this.isFlying() || this.isHovering());

        // —— 水面悬停：会飞的小马脚下是水（或正泡在水里）时，保持飞行/悬停在水面上方约 1 格，
        //    不落水；漂离水面（脚下变为实体地面）后走下方正常逻辑下降落地 → 恢复走路状态。 ——
        if (!this.isVehicle()) {
            boolean overWater = this.level().getFluidState(this.blockPosition().below()).is(FluidTags.WATER)
                    || this.level().getFluidState(this.blockPosition()).is(FluidTags.WATER);
            if (overWater) {
                // 进入水面悬停状态（保持飞行感，不下落；立即无重力避免本 tick 重力拉扯）
                if (!(this.isFlying() || this.isHovering())) {
                    this.setHovering(true);
                    this.setFlying(false);
                    this.flyPhase = FlightPhase.HOVER;
                    this.setNoGravity(true);
                }
                // 找脚下水顶，维持 y = 水面顶 + 1：太低抬升、太高缓降、到位稳住
                double waterTop = this.getY();
                BlockPos.MutableBlockPos bp = new BlockPos.MutableBlockPos();
                bp.set(this.blockPosition());
                for (int i = 0; i < 12 && bp.getY() >= this.level().getMinBuildHeight(); i++) {
                    if (this.level().getFluidState(bp).is(FluidTags.WATER)) {
                        waterTop = bp.getY() + 1.0D; // 水方块顶面（水面）
                        break;
                    }
                    bp.move(0, -1, 0);
                }
                double dy = (waterTop + 1.0D) - this.getY();
                if (dy > 0.05D) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.05D, 0));
                } else if (dy < -0.4D) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.03D, 0));
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.5D, 1.0D)); // 稳住垂直
                }
                return;
            }
        }

        boolean hasTarget = this.getTarget() != null && this.getTarget().isAlive();
        boolean aiBusy = this.getNavigation().isInProgress();
        if (this.isFlying() || this.isHovering()) {
            switch (this.flyPhase) {
                case ASCENT:
                    this.flyDuration--;
                    this.setDeltaMovement(this.getDeltaMovement().add(0, this.angryFlight ? 0.05D : getFlightAscentSpeed(), 0));
                    if (this.flyDuration <= 0 || this.getY() >= this.flyStartY + getFlightMaxHeight() || aiBusy) {
                        this.flyPhase = FlightPhase.HOVER;
                        this.flyDuration = this.angryFlight ? ANGRY_HOVER_DURATION : getFlightHoverDuration();
                        this.setDeltaMovement(Vec3.ZERO);
                    }
                    break;
                case HOVER:
                    this.flyDuration--;
                    // 抵消重力：y 速度清零，保持悬停（否则重力每 tick -0.08 会把实体拉回地面）
                    this.setDeltaMovement(new Vec3(this.getDeltaMovement().x * 0.5, 0.0D, this.getDeltaMovement().z * 0.5));
                    // 有仇恨时刷新悬停时间，基本不下落
                    if (this.angryFlight && hasTarget && this.flyDuration <= ANGRY_HOVER_REFRESH_THRESHOLD)
                        this.flyDuration = ANGRY_HOVER_REFRESH_DURATION;
                    if (this.flyDuration <= 0) this.flyPhase = FlightPhase.DESCENT;
                    break;
                case DESCENT:
                    // 有仇恨时不下落，回到悬停
                    if (this.angryFlight && hasTarget) {
                        this.flyPhase = FlightPhase.HOVER;
                        this.flyDuration = ANGRY_HOVER_REFRESH_DURATION;
                        this.setDeltaMovement(Vec3.ZERO);
                        break;
                    }
                    this.setDeltaMovement(this.getDeltaMovement().add(0, this.angryFlight ? -0.03D : getFlightDescendSpeed(), 0));
                    if (this.onGround()) {
                        this.setFlying(false); this.setHovering(false);
                        this.flyPhase = FlightPhase.ASCENT;
                        this.angryFlight = false;
                        this.flyCooldown = getFlightCooldownMin() + this.random.nextInt(Math.max(1, getFlightCooldownMax() - getFlightCooldownMin()));
                    }
                    break;
            }
        } else {
            if (this.onGround() && this.flyCooldown > 0) this.flyCooldown--;
            // 有仇恨时高概率起飞
            boolean isAngry = hasTarget && this.onGround() && this.flyCooldown <= 0 && this.random.nextInt(ANGRY_FLIGHT_PROB_DENOM) == 0;
            if (!aiBusy && this.onGround() && this.flyCooldown <= 0 && this.getPassengers().isEmpty() && !this.isOrderedToSit()
                && (isAngry || this.random.nextInt(Math.max(1, getFlightChance())) == 0)) {
                this.angryFlight = isAngry || (hasTarget && !isAngry && this.random.nextBoolean());
                this.setHovering(true);
                this.flyPhase = FlightPhase.ASCENT;
                flyStartY = this.getY();
                flyDuration = isAngry ? ANGRY_TAKEOFF_ASCENT
                    : getFlightDurationMin() + this.random.nextInt(Math.max(1, getFlightDurationMax() - getFlightDurationMin()));
                this.setDeltaMovement(this.getDeltaMovement().add(0, TAKEOFF_IMPULSE, 0));
            }
        }
    }
}
