package com.shao.mythicalcreatures.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TwilightSparkleEntity extends Mob {

    private boolean flying = false;

    public TwilightSparkleEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public boolean isFlying() {
        return flying;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        // 暂时无 AI，后续可以加
    }

    @Override
    public void tick() {
        super.tick();
        // 自动判断飞行状态：垂直速度向上且不在地面上
        boolean wasFlying = this.flying;
        if (!this.onGround() && this.getDeltaMovement().y > 0.05) {
            this.flying = true;
        } else if (this.onGround()) {
            this.flying = false;
        }
        // 落地缓冲：在空中且垂直速度向下，继续飞行态一小段时间
        if (!this.onGround() && this.getDeltaMovement().y < -0.1) {
            this.flying = false;
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // 无声
    }
}
