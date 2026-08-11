package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.entity.UnstableItemEntity;
import com.shao.mythicalcreatures.sound.ModSounds;

import com.shao.mythicalcreatures.config.MythicalConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WindigoEntity extends HostilePonyEntity {
    //    原生 EntityType 的 sized(8,10) 只给了一根细柱框，所以这里自定义矩形 AABB 覆盖全身。
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
        applyCoreStats(entityId(), canFly());
    }

    @Override protected boolean canFly() { return false; } // 雪魔不飞行（地面行为）
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
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 48.0));
    }

    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        defineFlyData();
    }

    @Override public void tick() {
        super.tick();
        // 雪魔不飞行：不调用 tickFlight（无起飞-悬停-落地循环），地面行为。
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 雪魔攻击（纯远程霰弹）：用 RangedAttackGoal 控制节奏（同模组普通生物：
        // 目标在射程内且有视线时射击，间隔 20 tick≈1 秒；射程外会自动靠近）。
        // 雪魔为地面单位，地面寻路对大碰撞箱可用，故可直接使用 RangedAttackGoal。
        this.goalSelector.getAvailableGoals().removeIf(w ->
                w.getGoal() instanceof MeleeAttackGoal || w.getGoal() instanceof RangedAttackGoal);
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 20, 40.0F));
        // 目标：攻击一切见到的生物（玩家 / 动物 / 村民 / 其它怪物都在内），
        // 不含盔甲架（ArmorStand 是 LivingEntity 需显式排除）与方块实体（本就不是 LivingEntity，天然不会被选）。
        // 已驯服的个体不攻击其主人。
        this.targetSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof NearestAttackableTargetGoal);
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                p -> !(p instanceof ArmorStand) && !(this.isTame() && p == this.getOwner())));
    }

    /**
     * 雪魔远程攻击（霰弹枪式）：一次随机发射 10~20 颗「不稳定物品」投掷物，
     * 随机散布但都瞄准同一目标；发射节奏由 RangedAttackGoal 控制（见 registerGoals）。
     * 投掷物命中造成 15 点魔法伤害（见 UnstableItemEntity）。
     */
    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        if (this.level().isClientSide() || target == null || !target.isAlive()) return;

        int count = 10 + this.random.nextInt(11); // 10..20
        double sx = this.getX();
        double sy = this.getY(0.5D);
        double sz = this.getZ();
        double tx = target.getX();
        double ty = target.getY(0.5D);
        double tz = target.getZ();

        Vec3 aim = new Vec3(tx - sx, ty - sy, tz - sz);
        if (aim.lengthSqr() < 1.0E-6D) aim = new Vec3(0.0D, 0.0D, 1.0D);
        aim = aim.normalize();

        double speed = 1.5D;
        double spread = 0.15D; // 随机分布幅度（弧度级，决定散布锥半角）

        this.level().playSound(null, sx, sy, sz,
                SoundEvents.ENDER_PEARL_THROW, net.minecraft.sounds.SoundSource.NEUTRAL, 0.3F,
                0.4F / (this.level().getRandom().nextFloat() * 0.4F + 0.8F));

        for (int i = 0; i < count; i++) {
            UnstableItemEntity proj = new UnstableItemEntity(this.level(), this);
            // 在瞄准方向上叠加随机偏移 → 随机分布，但整体仍指向同一目标
            double ox = (this.random.nextDouble() - 0.5D) * 2.0D * spread;
            double oy = (this.random.nextDouble() - 0.5D) * 2.0D * spread;
            double oz = (this.random.nextDouble() - 0.5D) * 2.0D * spread;
            Vec3 dir = aim.add(ox, oy, oz).normalize();
            proj.shoot(dir.x, dir.y, dir.z, (float) speed, 0.0F);
            this.level().addFreshEntity(proj);
        }
    }

}
