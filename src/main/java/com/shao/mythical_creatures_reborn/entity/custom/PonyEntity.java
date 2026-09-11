package com.shao.mythical_creatures_reborn.entity.custom;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;
import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import com.shao.mythical_creatures_reborn.util.EntityHateFilter;
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
 * 小马实体基类 — 骨架 + 委托层。
 *
 * 具体实现拆到三个组件：{@link PonyFlight}（飞行状态机）、{@link PonyRiding}（骑乘调参 + 骑手定位）、
 * {@link PonyStats}（属性装配）。本类只保留：
 *   · 构造 / 抽象方法 / 虚参数 getter（子类覆写用）
 *   · Minecraft 的 @Override 钩子（hurt / mobInteract / registerGoals / tick 等），方法体一行委托
 * 以保证 41 个子类的调用接口零改动。
 *
 * 【构造期注意】构造函数会调用虚方法 refreshConfigAttributes()，此时子类字段尚未初始化。
 * 三个组件均以实例字段初始化器（= new ...）创建，在 super() 之后、构造体之前完成初始化，
 * 因此 refreshConfigAttributes → stats/riding 组件在构造期是可用且非 null 的。切勿改为在构造体内 new。
 *
 * Pony base class — skeleton + delegation layer. Concrete behavior is split into three
 * components (PonyFlight / PonyRiding / PonyStats); this class keeps only the constructor,
 * abstract methods, virtual parameter getters, and the Minecraft @Override hooks (one-line
 * delegation), so the 41 subclasses need no changes.
 */
public abstract class PonyEntity extends TamableAnimal implements GeoEntity, RangedAttackMob {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /* ── 组件（实例字段初始化器：保证构造期 refreshConfigAttributes 可用）── */
    public final PonyFlight flight = new PonyFlight(this);
    public final PonyRiding riding = new PonyRiding(this);
    public final PonyStats stats = new PonyStats(this);

    public PonyEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        refreshConfigAttributes();
    }

    /* ================================================================
     * 全局翅膀动画参数（所有小马共用，配置重载时刷新）
     * ================================================================ */

    // Global wing-animation params shared by all ponies; refreshed on config reload.
    // 包级可见：供同包 FlightRideAPI.tickRiddenFlight 读取缓存。
    static double GLOBAL_WING_FLAP_SPEED  = 0.4;
    static double GLOBAL_WING_DECAY_SPEED = 0.15;

    public static void refreshGlobalRideTuning() {
        GLOBAL_WING_FLAP_SPEED  = MythicalConfig.DATA.get("global_params", "wing_flap_speed", 0.4);
        GLOBAL_WING_DECAY_SPEED = MythicalConfig.DATA.get("global_params", "wing_decay_speed", 0.15);
    }

    /* ================================================================
     * 属性 / 骑乘调参：委托给组件；虚参数 getter 保留在本类供子类覆写
     * ================================================================ */

    /** 配置改动后由可视化编辑器调用：重新缓存骑乘调参（只读取配置，不改动核心框架逻辑） */
    public void refreshRideTuningCache() {
        riding.cacheRideTuning(entityId());
    }

    /**
     * 从配置文件刷新属性值。默认实现（供 36 个只装配核心属性的子类复用）：
     * 装配核心属性；若为可骑乘实体，额外缓存骑乘调参。
     * 子类若有额外逻辑可覆写并调用 super。
     */
    protected void refreshConfigAttributes() {
        if (isRideable()) riding.cacheRideTuning(entityId());
        stats.applyCoreStats(entityId(), canFly());
    }

    /** 是否可骑乘（决定 refreshConfigAttributes 是否缓存骑乘调参）。默认不可；7 个坐骑覆写为 true。 */
    protected boolean isRideable() { return false; }

    /** 委托：装配核心属性（保留给需要自定义装配顺序的子类调用）。 */
    protected final void applyCoreStats(String id, boolean flying) {
        stats.applyCoreStats(id, flying);
    }

    /** 委托：缓存骑乘调参（保留给需要自定义装配顺序的子类调用）。 */
    protected final void cacheRideTuning(String id) {
        riding.cacheRideTuning(id);
    }

    /** 包可见：供同包组件访问 protected 的 entityData（外部类不能直接访问 inherited protected 成员）。 */
    net.minecraft.network.syncher.SynchedEntityData entityDataRaw() { return this.entityData; }

    /** 统一的实体注册名（如 mythical_creatures_reborn:rainbow_dash），避免各子类硬编码字符串。 */
    protected final String entityId() {
        return ForgeRegistries.ENTITY_TYPES.getKey(this.getType()).toString();
    }

    /** 数据驱动掉落表：data/mythical_creatures_reborn/loot_tables/entities/<实体id>.json。
     *  getLootTable() 是 final，实际生效的钩子是 getDefaultLootTable()。 */
    @Override
    protected ResourceLocation getDefaultLootTable() {
        String id = entityId();
        return new ResourceLocation("mythical_creatures_reborn", "entities/" + id.substring(id.indexOf(':') + 1));
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
     * 动画（所有小马共用）
     * ================================================================ */

    /**
     * 攻击动画同步位：服务端由攻击 Goal 在 start()/stop() 置位与清除，客户端据此渲染 attack 动画。
     *
     * 用同步数据而非"服务端定时器"的原因：Goal 只在服务端 tick，客户端无从得知攻击何时开始/结束；
     * 同步位让两侧状态天然一致，客户端无需任何倒计时逻辑。
     * 生命周期由 Goal 的 start()/stop() 保证成对（Minecraft 的 GoalSelector 一定会调用 stop()），
     * 因此不存在"动画卡住不结束"的情况。
     *
     * Attack-animation sync flag, set/cleared by the attacking goal (server side). The client reads it
     * to play the "attack" animation, so no client-side timing logic is needed.
     */
    private static final EntityDataAccessor<Boolean> DATA_ATTACK_ANIM =
            SynchedEntityData.defineId(PonyEntity.class, EntityDataSerializers.BOOLEAN);

    /** 攻击动画的动画名：两个导出文件（spikezilla / windigo）里均为 attack。 */
    private static final String ATTACK_ANIM = "attack";

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACK_ANIM, false);
    }

    /** 服务端调用：开启/关闭攻击动画（自动同步到客户端）。 */
    public void setAttackAnimation(boolean playing) {
        this.entityData.set(DATA_ATTACK_ANIM, playing);
    }

    /** 攻击动画是否正在播放（两侧一致）。 */
    public boolean isAttackAnimationPlaying() {
        return this.entityData.get(DATA_ATTACK_ANIM);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, this::predicate));
    }

    private <T extends PonyEntity> PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<T> state) {
        // 死亡时停止动画：让尸体交给游戏原生死亡动画处理，避免原地循环 idle 像"假死"。
        // On death, stop the animation so the corpse is handled by the vanilla death animation
        // instead of looping idle in place.
        if (!this.isAlive()) return PlayState.STOP;
        // 攻击动画优先级最高：横扫 / 冲锋期间播放 attack（thenPlay 只播一遍，播完停在末帧）
        if (this.isAttackAnimationPlaying()) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay(ATTACK_ANIM));
            return PlayState.CONTINUE;
        }
        if (this.isInSittingPose()) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
        } else if (canFly() && (isFlying() || isHovering()) && !this.onGround()) {
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

    /* ================================================================
     * 小马不继承繁殖逻辑：禁止繁殖
     * ── 原版继承链 TamableAnimal → Animal(AgeableMob) 自带繁殖体系（喂食进恋爱状态 → 配对 → 生幼崽）。
     *    小马没有繁殖设定，显式封死全部繁殖入口。驯服食物走 getTamingItem()，与 isFood 无关，不受影响。
     * ================================================================ */
    @Nullable @Override public AgeableMob getBreedOffspring(net.minecraft.server.level.ServerLevel level, AgeableMob other) { return null; }
    @Override public boolean canBreed() { return false; }
    @Override public boolean isFood(ItemStack stack) { return false; }
    @Override public boolean canFallInLove() { return false; }
    @Override public void setInLove(@Nullable Player player) { } // 永不进入恋爱状态，繁殖 tick 永不触发
    @Override public void spawnChildFromBreeding(net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.animal.Animal mate) { } // 防御：即使误入也不产崽

    /* ================================================================
     * 伤害：主人/骑手保护 + 爆炸魔法火焰闪电免疫
     * ================================================================ */

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isTame() && source.getEntity() instanceof Player p && p == this.getOwner()) return false;
        if (this.isVehicle() && source.getEntity() != null
            && this.getControllingPassenger() != null && source.getEntity() == this.getControllingPassenger())
            return false;
        // 防御魔法/火焰/爆炸/雷电免疫：仅保护"已驯服的友方"（坐骑/宠物），让它们不被友军魔法与
        // 环境火焰误伤。敌对生物与未驯服个体正常受伤 —— 否则末日颅骨（亡灵）无法像原版骷髅那样
        // 在白天被晒燃烧，且更符合"这些免疫是保护我方而非敌方"的语义。
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
        // 注意：主动索敌"敌对目标"由子类决定 ——
        //   · 中立小马(NeutralPonyEntity) 会主动打模组敌对生物 + 原版 Enemy（防御）
        //   · 直接继承 PonyEntity 的中立生物（硬汉/梅菲斯）默认只反击、不主动狩猎（同原版中立）
        // 基类不再统一挂 NearestAttackableTargetGoal(Enemy)，避免中立生物行为比原版中立更激进。
    }

    /* ================================================================
     * 仇恨目标兜底过滤（所有小马共用）
     * ================================================================ */

    /**
     * 统一拦截一切仇恨目标设置：被排除实体（DuMmmMmmy 假人 / 盔甲架 / 展示框）永不成为目标。
     * 作为被动反击（HurtByTargetGoal / OwnerHurtByTargetGoal / OwnerHurtTargetGoal）与
     * 麋鹿族群广播（MooseHerd.alert）的最终兜底；主动索敌另由 NearestAttackableTargetGoal 的
     * 谓词在挑选阶段拦截，两者互补。若传入被排除实体而当前已有有效目标，则保留现有目标。
     */
    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && EntityHateFilter.shouldIgnore(target)) {
            if (this.getTarget() == null || EntityHateFilter.shouldIgnore(this.getTarget())) {
                super.setTarget(null);
            }
            return;
        }
        super.setTarget(target);
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
                    // 若正在空中飞行/悬停，坐下时强制落地，避免"空中悬停"
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
     * 骑手定位（所有小马共用，委托 PonyRiding）
     * ================================================================ */

    @Override
    protected void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        super.positionRider(passenger, callback);
        riding.placeRider(passenger);
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
        return riding.getControllingPassenger();
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

    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) {}
    @Override public int getAmbientSoundInterval() { return 200; }

    /* ================================================================
     * 飞行同步数据 & 状态（委托 PonyFlight）
     * ================================================================ */

    /** 需要飞行的子类：在 defineSynchedData 中调用（注册飞行同步位）。
     *
     *  【构造期陷阱】defineSynchedData() 是在 {@code Entity} 的构造函数里被调用的，早于本类的
     *  实例字段初始化器——也就是说，此刻 {@code this.flight} 还是 null。所以这里必须走**静态**路径，
     *  绝不能写成 {@code flight.defineFlyData()}，否则实体永远构造不出来（刷怪蛋右键无反应）。
     *  同理，未来若再往组件里加"构造期就要用"的数据，也必须做成静态或接收参数的形式。 */
    protected void defineFlyData() { PonyFlight.defineFlyData(this.entityData); }

    public boolean isFlying()   { return flight.isFlying(); }
    public boolean isHovering() { return flight.isHovering(); }
    public void setFlying(boolean v)   { flight.setFlying(v); }
    public void setHovering(boolean v) { flight.setHovering(v); }

    /* ================================================================
     * 自主飞行状态机（canFly() 子类使用，被子类 tick() 调用）
     * ================================================================ */

    // ── 自主飞行参数（全部硬编码、非配置驱动；改飞行手感必须改这里）────────
    // 单位：速度=方块/tick；时长=tick（20 tick=1 秒）。属平衡基线，非代码派生。
    protected double getFlightAscentSpeed()     { return 0.05D; }  // 平静上升速度(方块/tick)
    protected double getFlightDescendSpeed()    { return -0.03D; } // 降落速度(负=下落)
    protected double getFlightMaxHeight()       { return 8.0D; }   // 单次观光起飞最大升限(方块)
    protected int    getFlightHoverDuration()   { return 80; }     // 平静悬停时长(tick,≈4s)
    protected int    getFlightChance()          { return 1000; }   // 平静起飞概率分母(子类读配置覆盖)
    protected int    getFlightCooldownMin()     { return 200; }    // 落地后再起飞冷却下限(tick)
    protected int    getFlightCooldownMax()     { return 400; }    // 落地后再起飞冷却上限(tick)
    protected int    getFlightDurationMin()     { return 60; }     // 平静观光时长下限(tick)
    protected int    getFlightDurationMax()     { return 100; }    // 平静观光时长上限(tick)

    /** 驱动自主飞行状态机（委托 PonyFlight）。 */
    protected void tickFlight() { flight.tickFlight(); }
}
