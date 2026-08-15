package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.sound.ModSounds;

import com.shao.mythicalcreatures.config.MythicalConfig;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SkullOfDoomEntity extends HostilePonyEntity {

    public SkullOfDoomEntity(EntityType<SkullOfDoomEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void refreshConfigAttributes() {
        applyCoreStats(entityId(), canFly());
    }

    /** 会飞：和蜜蜂一样悬浮振翅。基础飞行状态机（tickFlight）自带 ASCENT→HOVER→DESCENT 观光循环，
     *  有仇恨时一直悬停追击 —— 质感等同蜜蜂悬停。把起飞概率分母调小让它在白天更常悬停盘旋。 */
    @Override protected boolean canFly() { return true; }
    @Override protected int getFlightChance() { return 120; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSoundEvent() { return ModSounds.SKULL_OF_DOOM_AMBIENT.get(); }
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getHurtSoundEvent() { return ModSounds.SKULL_OF_DOOM_HURT.get(); }

    /**
     * 亡灵生物：让原版机制（亡灵伤害、亡灵药水、亡灵治疗/伤害反转、铁傀儡仇恨等）
     * 把末日颅骨当作亡灵对待。对应原版 Skeleton 的判定方式：
     *   1) 实体类型登记到 EntityTypeTags.UNDEAD（isUndead() 走此标签）
     *   2) mobType() 返回 MobType.UNDEAD（伤害/药水逻辑走此）
     */
    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    /** 飞行同步数据（canFly()==true 必须调用，否则 isFlying/isHovering 读不到字段） */
    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        defineFlyData();
    }

    /** 驱动自主飞行状态机（蜜蜂式悬停） */
    @Override public void tick() {
        super.tick();
        tickFlight();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythicalcreatures:skull_of_doom", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythicalcreatures:skull_of_doom", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythicalcreatures:skull_of_doom", "attack_damage"))
                .add(Attributes.FLYING_SPEED, (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:skull_of_doom", "fly_speed"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {}
}
