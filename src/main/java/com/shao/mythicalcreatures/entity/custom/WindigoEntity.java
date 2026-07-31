package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.sound.ModSounds;

import com.shao.mythicalcreatures.config.MythicalConfig;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class WindigoEntity extends HostilePonyEntity {

    // ── 雪魔碰撞箱：模型是横躺的长条生物（geo 反推约 20 宽 × 8 高 × 33 深）。
    //    原生 EntityType 的 sized(8,10) 只给了一根细柱框，所以这里自定义矩形 AABB 覆盖全身。
    //    Windigo collision box: the geo model is a long horizontal slab (~20 wide × 8 tall × 33 deep).
    //    The native sized(8,10) is a thin pillar, so we build a custom rectangular AABB to cover the whole body.
    private static final float BB_WIDTH = 20.0F;   // X 半宽 = 10
    private static final float BB_DEPTH = 33.0F;   // Z 半深 = 16.5
    private static final float BB_HEIGHT = 8.0F;    // Y 总高
    private static final float BB_Y_OFFSET = 0.0F;  // 框底相对脚底的位置（正值=抬高，负值=下沉）

    public WindigoEntity(EntityType<WindigoEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        // width 仅用于骑乘/视高参考，取较大边；真实矩形底面由 makeBoundingBox() 决定。
        // width is only used for riding/eye reference; the real rectangle comes from makeBoundingBox().
        return EntityDimensions.scalable(Math.max(BB_WIDTH, BB_DEPTH), BB_HEIGHT);
    }

    @Override
    protected AABB makeBoundingBox() {
        double hw = BB_WIDTH * 0.5;
        double hd = BB_DEPTH * 0.5;
        double minY = this.getY() - BB_Y_OFFSET;
        return new AABB(this.getX() - hw, minY, this.getZ() - hd,
                        this.getX() + hw, minY + BB_HEIGHT, this.getZ() + hd);
    }

    @Override protected void refreshConfigAttributes() {
        var h = this.getAttribute(Attributes.MAX_HEALTH);
        if (h != null) h.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:windigo", "max_health"));
        var s = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (s != null) s.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:windigo", "move_speed"));
        var d = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (d != null) d.setBaseValue(MythicalConfig.DATA.entityAttr("mythicalcreatures:windigo", "attack_damage"));
        var f = this.getAttribute(Attributes.FLYING_SPEED);
        if (f != null) f.setBaseValue((float) MythicalConfig.DATA.entityAttr("mythicalcreatures:windigo", "fly_speed"));
        this.setHealth(this.getMaxHealth());
    }

    @Override protected boolean canFly() { return true; }
    @Override protected Item getTamingItem() { return Items.APPLE; }

    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSoundEvent() { return ModSounds.WINDIGO_AMBIENT.get(); }
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getHurtSoundEvent() { return ModSounds.WINDIGO_HURT.get(); }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythicalcreatures:windigo", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythicalcreatures:windigo", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythicalcreatures:windigo", "attack_damage"))
                .add(Attributes.FLYING_SPEED, (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:windigo", "fly_speed"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }

    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        defineFlyData();
    }

    @Override public void tick() {
        super.tick();
        tickFlight();
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {}
}
