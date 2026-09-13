package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.sound.ModSounds;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import com.shao.mythical_creatures_reborn.util.EntityHateFilter;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SkullOfDoomEntity extends HostilePonyEntity {

    /**
     * 是否正在俯冲/爬升。仅服务端有意义，由 {@link SkullDiveAttackGoal} 在 start()/stop() 置位。
     * 为 true 时 {@link #tick()} 会跳过 {@link #tickFlight()}，把 deltaMovement 的写入权
     * 完全交给俯冲 Goal（否则二者会互相覆盖）。
     */
    private boolean diving = false;

    public SkullOfDoomEntity(EntityType<SkullOfDoomEntity> type, Level level) {
        super(type, level);
    }

    /** 会飞：和蜜蜂一样悬浮振翅。基础飞行状态机（tickFlight）自带 ASCENT→HOVER→DESCENT 观光循环，
     *  有仇恨时一直悬停追击 —— 质感等同蜜蜂悬停。把起飞概率分母调小让它在白天更常悬停盘旋。 */
    @Override protected boolean canFly() { return true; }
    @Override protected int getFlightChance() { return 120; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() { return ModSounds.SKULL_OF_DOOM_AMBIENT.get(); }
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return ModSounds.SKULL_OF_DOOM_HURT.get(); }

    /**
     * 亡灵生物：让原版机制把末日颅骨当作亡灵对待，和僵尸/骷髅同一原理。
     *   1) mobType() 返回 MobType.UNDEAD —— 亡灵杀手(Smite)加成、亡灵药水反转、治疗/伤害反转走此判定；
     *   2) 实体类型已登记到 EntityTypeTags.UNDEAD（data/minecraft/tags/entity_types/undead.json）；
     *   3) 白天露天被阳光点燃（见 aiStep）。
     */
    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    /**
     * 攻击 AI：
     *  · 移动层：用自定义的"俯冲近战"替换基类默认的贴身行走近战（MeleeAttackGoal）。
     *    平时由飞行状态机悬停在目标上方，进入触发距离后俯冲扑击、接触造成一次近战伤害，再拉起悬停——
     *    行为对齐原版幻翼/恼鬼。
     *  · 目标层：攻击**除亡灵类以外的所有生物**（玩家 / 动物 / 村民 / 其它非亡灵怪物），
     *    排除项：亡灵（MobType.UNDEAD）、以及 EntityHateFilter 里的特殊实体（DuMmmMmmy 假人、原版盔甲架）。
     *    已驯服的个体不攻击其主人。
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 移除基类的行走近战与"只打玩家"，改由俯冲 Goal + 通吃索敌接管
        this.goalSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof MeleeAttackGoal);
        this.goalSelector.addGoal(3, new SkullDiveAttackGoal(this));

        this.targetSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof NearestAttackableTargetGoal);
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                p -> !EntityHateFilter.shouldIgnore(p)
                        && p.getMobType() != MobType.UNDEAD
                        && !(this.isTame() && p == this.getOwner())));
    }

    /** 飞行同步数据（canFly()==true 必须调用，否则 isFlying/isHovering 读不到字段） */
    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        defineFlyData();
    }

    /** 驱动自主飞行状态机（蜜蜂式悬停）；俯冲期间跳过，交由 SkullDiveAttackGoal 独占移动。 */
    @Override public void tick() {
        super.tick();
        if (!this.diving) tickFlight();
    }

    /** 是否处于俯冲/爬升阶段（服务端）。 */
    public boolean isDiving() { return this.diving; }

    /** 由 {@link SkullDiveAttackGoal} 调用：进入/退出"跳过悬停状态机"的俯冲模式。 */
    public void setDiving(boolean diving) { this.diving = diving; }

    /**
     * 亡灵灼烧：白天露天被阳光点燃，与僵尸/骷髅一致。
     * 戴着头盔时改为消耗头盔耐久（坏掉后掉落 / 帽子消失），没戴才真正着火。
     */
    @Override
    public void aiStep() {
        if (this.isAlive() && this.level().isDay() && !this.level().isClientSide()
                && this.level().canSeeSky(this.blockPosition())
                && !this.isInWaterRainOrBubble()) {
            ItemStack head = this.getItemBySlot(EquipmentSlot.HEAD);
            if (!head.isEmpty()) {
                if (head.isDamageableItem()) {
                    head.setDamageValue(head.getDamageValue() + this.random.nextInt(2));
                    if (head.getDamageValue() >= head.getMaxDamage()) {
                        this.broadcastBreakEvent(EquipmentSlot.HEAD);
                        this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }
            } else {
                this.setSecondsOnFire(8);
            }
        }
        super.aiStep();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PonyAttributes.flying("mythical_creatures_reborn:skull_of_doom");
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {}
}
