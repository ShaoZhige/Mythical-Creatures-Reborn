package com.shao.mythicalcreatures.entity.custom;

import com.shao.mythicalcreatures.config.MythicalConfig;
import com.shao.mythicalcreatures.entity.ModEntities;
import com.shao.mythicalcreatures.entity.MagicBurstEntity;
import com.shao.mythicalcreatures.entity.TwilightStarEntity;
import com.shao.mythicalcreatures.entity.custom.TwilightMagicEntity;
import com.shao.mythicalcreatures.item.ModItems;
import com.shao.mythicalcreatures.sound.ModSounds;
import com.shao.mythicalcreatures.entity.TwilightStarEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TwilightSparkleEntity extends NeutralPonyEntity {

    public TwilightSparkleEntity(EntityType<TwilightSparkleEntity> type, Level level) {
        super(type, level);
    }

    private int magicSummonCooldown = 0;

    @Override protected void refreshConfigAttributes() {
        cacheRideTuning(entityId());
        applyCoreStats(entityId(), canFly());
    }

    @Override protected boolean canFly() { return true; }
    @Override protected Item getTamingItem() {
        return resolveTamingItem(MythicalConfig.D.TS_TAMING, ModItems.TWILIGHT_CUTIEMARK.get());
    }
    @Override @Nullable protected SoundEvent getAmbientSound() { return ModSounds.TWILIGHT_SPARKLE_AMBIENT.get(); }
    @Override @Nullable protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return ModSounds.TWILIGHT_SPARKLE_HURT.get(); }

    /* ── 飞行参数（慢速平稳，默认值即紫悦的风格） ── */
    @Override protected int    getFlightChance()        { return MythicalConfig.DATA.getInt("mythicalcreatures:twilight_sparkle", "flight_chance", 200); }
    @Override protected int    getFlightCooldownMin()   { return MythicalConfig.DATA.getInt("mythicalcreatures:twilight_sparkle", "fly_cooldown_min", 200); }
    @Override protected int    getFlightCooldownMax()   { return MythicalConfig.DATA.getInt("mythicalcreatures:twilight_sparkle", "fly_cooldown_max", 400); }
    @Override protected int    getFlightDurationMin()   { return MythicalConfig.DATA.getInt("mythicalcreatures:twilight_sparkle", "fly_duration_min", 100); }
    @Override protected int    getFlightDurationMax()   { return MythicalConfig.DATA.getInt("mythicalcreatures:twilight_sparkle", "fly_duration_max", 150); }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH,  (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "max_health"))
                .add(Attributes.MOVEMENT_SPEED, (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "move_speed"))
                .add(Attributes.FLYING_SPEED,   (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "fly_speed"))
                .add(Attributes.ATTACK_DAMAGE,  (float) MythicalConfig.DATA.entityAttr("mythicalcreatures:twilight_sparkle", "attack_damage"))
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get("global_params", "follow_range", 16.0));
    }

    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        defineFlyData();
    }

    @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof TwilightStarEntity) return false;
        return super.hurt(source, amount);
    }

    /* ── 骑乘飞行：逻辑统一在 FlightRideAPI，实体只做委托调用（默认值见 MythicalConfig.D.ENTITY_DEFAULTS） ── */
    // 飞行小马骑手定位以紫悦为标准（见 PonyEntity.FLYING_RIDER_*）
    @Override protected double getRiderBackOffset()    { return FLYING_RIDER_BACK; }
    @Override protected float  getRiderVerticalOffset() { return FLYING_RIDER_Y; }

    @Override protected @NotNull Vec3 getRiddenInput(Player player, @NotNull Vec3 v) {
        return FlightRideAPI.getRiddenInput(this, player, v);
    }

    @Override protected float getRiddenSpeed(@NotNull Player player) {
        return FlightRideAPI.getRiddenSpeed(this);
    }

    @Override protected void tickRidden(@NotNull Player player, @NotNull Vec3 v) {
        super.tickRidden(player, v);
        FlightRideAPI.tickRidden(this, player, v);
    }

    @Override public void travel(@NotNull Vec3 v) {
        if (!FlightRideAPI.flyingRideTravel(this, v)) {
            this.setNoGravity(false);
            super.travel(v);
        }
    }

    @Override public void tick() {
        super.tick();
        if (this.magicSummonCooldown > 0) this.magicSummonCooldown--;
        if (!FlightRideAPI.tickRiddenFlight(this)) tickFlight();
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        TwilightStarEntity projectile = new TwilightStarEntity(this.level(), this);
        projectile.shoot(target.getX() - this.getX(), target.getY(0.5) - this.getY(0.5),
                target.getZ() - this.getZ(), 1.5F, 1.0F);
        this.playSound(SoundEvents.LLAMA_SPIT, 1.0F, 1.0F);
        this.level().addFreshEntity(projectile);

        // 紫悦魔法爆发：冲击波 + 紫色魔法粒子四散（服务端 spawn，自动同步到客户端）
        this.castMagicBurst(this.level(), this.getX(), this.getY(0.7), this.getZ());

        // ② 发射时在紫悦身前画一个紫色五角星 + 外圈圆（攻击特效）
        float yaw = this.getYRot();
        double fx = -Math.sin(Math.toRadians(yaw));   // 身前水平方向（朝向）
        double fz = Math.cos(Math.toRadians(yaw));
        this.drawCastStar(this.level(),
                this.getX() + fx * 1.0, this.getY(0.7), this.getZ() + fz * 1.0,
                fx, fz);

        // 概率召唤紫悦的魔法团（一次召唤三只，以紫悦为主人，会自动环绕并攻击敌对生物）
        if (!this.level().isClientSide() && this.magicSummonCooldown <= 0 && this.random.nextFloat() < 0.35F) {
            for (int m = 0; m < 3; m++) {
                double ang = Math.toRadians(m * 120.0);
                double ox = Math.cos(ang) * 0.6;
                double oz = Math.sin(ang) * 0.6;
                TwilightMagicEntity magic = new TwilightMagicEntity(ModEntities.TWILIGHT_MAGIC.get(), this.level());
                magic.setPos(this.getX() + ox, this.getY(1.0D), this.getZ() + oz);
                magic.setOwner(this);
                this.level().addFreshEntity(magic);
            }
            this.magicSummonCooldown = 120; // 约 6 秒冷却
        }
    }

    /** 在 (x,y,z) 处生成一次紫悦魔法爆发：冲击波实体 + 向外四散的紫色魔法粒子。 */
    private void castMagicBurst(Level level, double x, double y, double z) {
        level.addFreshEntity(new MagicBurstEntity(level, x, y, z));
        // 注意：服务端 Level.addParticle 是空操作，必须走 ServerLevel.sendParticles 才能广播到客户端。
        if (!(level instanceof ServerLevel sl)) return;
        for (int i = 0; i < 28; i++) {
            Vec3 dir = new Vec3(level.random.nextDouble() - 0.5,
                                level.random.nextDouble() - 0.5,
                                level.random.nextDouble() - 0.5)
                    .normalize().scale(0.2 + level.random.nextDouble() * 0.4);
            ParticleOptions p = (i % 3 == 0) ? ParticleTypes.END_ROD
                    : (i % 3 == 1) ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.PORTAL;
            sl.sendParticles(p, x, y, z, 1, dir.x, dir.y, dir.z, 0.0);
        }
    }

    /**
     * 在 (cx,cy,cz) 处、以 (fx,fz) 为身前方向，用紫色粒子描边画一个五角星 + 外圈圆（攻击特效）。
     * 星所在平面法线 = 水平身前方向 forward=(fx,0,fz)；平面内两轴：side=(-fz,0,fx) 与 up=(0,1,0)。
     * 必须走 ServerLevel.sendParticles，否则服务端 addParticle 是空操作、客户端看不到。
     */
    private void drawCastStar(Level level, double cx, double cy, double cz, double fx, double fz) {
        if (!(level instanceof ServerLevel sl)) return;
        double sx = -fz, sz = fx;            // 平面内水平轴 side = forward × up
        final double R = 0.95, r = 0.40, RING = 1.12;
        // 五角星 10 个顶点（外、内交替），从正上方起
        double[] vx = new double[10], vy = new double[10];
        for (int i = 0; i < 10; i++) {
            double ang = Math.toRadians(-90 + i * 36);
            double rad = (i % 2 == 0) ? R : r;
            vx[i] = Math.cos(ang) * rad;     // 沿 side 轴
            vy[i] = Math.sin(ang) * rad;     // 沿 up 轴
        }
        // 沿 10 条边插值撒粒子
        int perEdge = 6;
        for (int i = 0; i < 10; i++) {
            int j = (i + 1) % 10;
            for (int k = 0; k <= perEdge; k++) {
                double t = (double) k / perEdge;
                double lx = vx[i] + (vx[j] - vx[i]) * t;
                double ly = vy[i] + (vy[j] - vy[i]) * t;
                sl.sendParticles(ParticleTypes.WITCH, cx + sx * lx, cy + ly, cz + sz * lx, 1, 0, 0, 0, 0.0);
            }
        }
        // 外圈圆
        int ringN = 50;
        for (int i = 0; i < ringN; i++) {
            double ang = Math.toRadians(i * 360.0 / ringN);
            double lx = Math.cos(ang) * RING;
            double ly = Math.sin(ang) * RING;
            sl.sendParticles(ParticleTypes.WITCH, cx + sx * lx, cy + ly, cz + sz * lx, 1, 0, 0, 0, 0.0);
        }
    }
}
