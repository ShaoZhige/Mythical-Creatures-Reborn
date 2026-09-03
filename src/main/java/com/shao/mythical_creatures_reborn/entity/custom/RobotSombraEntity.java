package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RobotSombraEntity extends HostilePonyEntity {

    public RobotSombraEntity(EntityType<RobotSombraEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void refreshConfigAttributes() {
        applyCoreStats(entityId(), canFly());
    }

    @Override protected boolean canFly() { return false; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    // 机械黑晶王沿用铁傀儡音效：受击 / 死亡 / 脚步均为铁傀儡音；环境音为 null（铁傀儡本就无环境音）。
    // Robot Sombra reuses Iron Golem sounds: hurt / death / step are Iron Golem; ambient stays null (golem is silent).
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() { return null; }
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return SoundEvents.IRON_GOLEM_HURT; }

    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() { return SoundEvents.IRON_GOLEM_DEATH; }

    // 脚步音：基类 PonyEntity 把 playStepSound 置空（小马无声），这里特意改回铁傀儡脚步声。
    // Step sound: base PonyEntity silences playStepSound; override it back to Iron Golem steps.
    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:robot_sombra", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:robot_sombra", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:robot_sombra", "attack_damage"))
                .add(Attributes.ARMOR, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:robot_sombra", "armor"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {}
}
