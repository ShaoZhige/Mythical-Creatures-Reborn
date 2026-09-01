package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.entity.UnstableItemEntity;
import com.shao.mythical_creatures_reborn.sound.ModSounds;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
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
        // 大碰撞箱翻越地形：可"走上"2 格高台阶（原版 0.6 只够跨半格）。飞行单位下无影响，保留无害。
        this.setMaxUpStep(2.0F);
    }

    /** 跳跃力（0.42 为原版约 1.25 格）。提到 0.6 ≈ 跳 2.2 格。飞行单位下无影响，保留无害。 */
    @Override
    protected float getJumpPower() {
        return 0.6F;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        // 逻辑宽度调大：getBbWidth() 来自 getDimensions().width（final，无法直接覆写），
        // 而原版近战攻击半径 getAttackReachSqr = 4×攻击者宽² + 目标getBbWidth()（线性近似）——
        // 对半深 16.5 的碰撞箱，该公式给出的攻击半径只有约 6 格，近战生物被箱子挡在中心距
        // 17 格外，永远进不了攻击范围（"撞到也打不到"）。把逻辑宽度撑到等效大值，
        // 攻击者攻击半径 ≈ 碰撞箱半深+1，贴到身体边缘即可挥击命中。
        // 物理碰撞仍走 makeBoundingBox 的常量框（20×33×8），不受此影响。
        // 雪魔为纯远程、无近战，此逻辑宽度不再影响攻击判定，保留以兼容同套碰撞箱体系。
        return EntityDimensions.scalable(400.0F, BB_HEIGHT);
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

    @Override protected boolean canFly() { return true; } // 雪魔飞行：常驻悬停追击（由 WindigoSkyChaseGoal 驱动）
    @Override protected Item getTamingItem() { return Items.APPLE; }

    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() { return ModSounds.WINDIGO_AMBIENT.get(); }
    @Nullable @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return ModSounds.WINDIGO_HURT.get(); }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:windigo", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:windigo", "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:windigo", "attack_damage"))
                // 飞行速度：canFly()=true 时由 applyCoreStats 注入 FLYING_SPEED 属性，这里也显式声明，
                // 否则该属性不存在、雪魔静默 0 速飞不起来（与末日颅骨同理）。
                .add(Attributes.FLYING_SPEED, (float) MythicalConfig.DATA.entityAttr("mythical_creatures_reborn:windigo", "fly_speed"))
                // 索敌距离 = 射程(40)的两倍，与飞行小马的比例一致（射程16/索敌32）；
                // 用雪魔专属键，避免被 global_params.follow_range 的默认值(32)拉低。
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("mythical_creatures_reborn:windigo", "follow_range", 80.0))
                // 满击退抗性：不被爆炸/击退/近战连击推走，防止被"控距"风筝致死或炸得下不来。
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        defineFlyData();
    }

    @Override public void tick() {
        super.tick();
        // 雪魔为飞行单位：每 tick 驱动飞行状态机（常驻悬停追击，由 WindigoSkyChaseGoal 设 angryFlight 维持）。
        tickFlight();
        // 少量冰雪环绕粒子（客户端）：每 4 tick 在身体周围飘 1 片雪花，营造冰雪气场而不卡顿。
        if (this.level().isClientSide && this.tickCount % 4 == 0) {
            this.level().addParticle(ParticleTypes.SNOWFLAKE,
                    this.getRandomX(3.0D), this.getY() + this.random.nextDouble() * this.getBbHeight(),
                    this.getRandomZ(3.0D),
                    0.0D, -0.02D, 0.0D);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 雪魔攻击（纯远程霰弹）：用自定义飞行追击 Goal 控制移动与射击节奏。
        // 该 Goal 接管水平靠拢 / 横向走位 / 后撤与高度跟随，并在进入射程且有视线时调用
        // performRangedAttack 发射 15~25 颗「不稳定物品」；框架 tickFlight() 负责无重力与悬停维持。
        this.goalSelector.getAvailableGoals().removeIf(w ->
                w.getGoal() instanceof MeleeAttackGoal || w.getGoal() instanceof WindigoSkyChaseGoal);
        this.goalSelector.addGoal(2, new WindigoSkyChaseGoal(this, 0.5D, 20, 40.0D));
        // 目标：攻击一切见到的生物（玩家 / 动物 / 村民 / 其它怪物都在内），
        // 不含盔甲架（ArmorStand 是 LivingEntity 需显式排除）与方块实体（本就不是 LivingEntity，天然不会被选）。
        // 已驯服的个体不攻击其主人。
        this.targetSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof NearestAttackableTargetGoal);
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                p -> !(p instanceof ArmorStand) && !(this.isTame() && p == this.getOwner())));
    }

    /**
     * 雪魔远程攻击（霰弹枪式）：一次随机发射 15~25 颗「不稳定物品」投掷物，
     * 随机散布但都瞄准同一目标；发射节奏由 WindigoSkyChaseGoal 控制（见 registerGoals）。
     * 投掷物命中造成 15 点魔法伤害（见 UnstableItemEntity）。
     */
    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        if (this.level().isClientSide() || target == null || !target.isAlive()) return;

        int count = 15 + this.random.nextInt(11); // 15..25
        double sx = this.getX();
        double sy = this.getY(0.5D);
        double sz = this.getZ();
        double tx = target.getX();
        double ty = target.getY(0.5D);
        double tz = target.getZ();

        Vec3 aim = new Vec3(tx - sx, ty - sy, tz - sz);
        if (aim.lengthSqr() < 1.0E-6D) aim = new Vec3(0.0D, 0.0D, 1.0D);
        aim = aim.normalize();

        double speed = 2.0D; // 弹速（原 1.5，现 2.0）
        double spread = 0.1D; // 随机分布幅度（弧度级，决定散布锥半角）

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
