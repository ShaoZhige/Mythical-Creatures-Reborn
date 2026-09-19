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

public class RobotSombraEntity extends HostilePonyEntity {

    public RobotSombraEntity(EntityType<RobotSombraEntity> type, Level level) {
        super(type, level);
    }

    @Override protected boolean canFly() { return false; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    // 语音（环境音 null / 受伤·死亡 = 铁傀儡音）已收进 EntitySoundProfiles 总表统一管理，这里不再覆写。
    // 脚步音：基类 PonyEntity 把 playStepSound 置空（小马无声），这里特意改回铁傀儡脚步声。
    // Step sound: base PonyEntity silences playStepSound; override it back to Iron Golem steps.
    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PonyAttributes.of("mythical_creatures_reborn:robot_sombra");
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {}
}
