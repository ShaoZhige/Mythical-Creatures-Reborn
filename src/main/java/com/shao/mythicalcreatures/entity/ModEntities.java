package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.ApplejackEntity;
import com.shao.mythicalcreatures.entity.custom.BearEntity;
import com.shao.mythicalcreatures.entity.custom.CockatriceEntity;
import com.shao.mythicalcreatures.entity.custom.FluttershyEntity;
import com.shao.mythicalcreatures.entity.custom.GarbleEntity;
import com.shao.mythicalcreatures.entity.custom.HolyLightRadianceEntity;
import com.shao.mythicalcreatures.entity.custom.KingbowserEntity;
import com.shao.mythicalcreatures.entity.custom.ParaspriteEntity;
import com.shao.mythicalcreatures.entity.custom.PhoenixEntity;
import com.shao.mythicalcreatures.entity.custom.PinkiePieEntity;
import com.shao.mythicalcreatures.entity.custom.RainbowDashEntity;
import com.shao.mythicalcreatures.entity.custom.RarityEntity;
import com.shao.mythicalcreatures.entity.custom.TwilightMagicEntity;
import com.shao.mythicalcreatures.entity.custom.TwilightSparkleEntity;
import com.shao.mythicalcreatures.entity.custom.UrsamajorEntity;
import com.shao.mythicalcreatures.entity.custom.BuffaloEntity;
import com.shao.mythicalcreatures.entity.custom.ChiefThunderhoovesEntity;
import com.shao.mythicalcreatures.entity.custom.BlackWidowEntity;
import com.shao.mythicalcreatures.entity.custom.LeviathanEntity;
import com.shao.mythicalcreatures.entity.custom.CentipedeEntity;
import com.shao.mythicalcreatures.entity.custom.HydraEntity;
import com.shao.mythicalcreatures.entity.custom.WindigoEntity;
import com.shao.mythicalcreatures.entity.custom.BabyMooseEntity;
import com.shao.mythicalcreatures.entity.custom.AdultMooseEntity;
import com.shao.mythicalcreatures.entity.custom.ToughGuyEntity;
import com.shao.mythicalcreatures.entity.custom.MavisEntity;
import com.shao.mythicalcreatures.entity.custom.ManticoreEntity;
import com.shao.mythicalcreatures.entity.custom.RainbowCentipedeEntity;
import com.shao.mythicalcreatures.entity.custom.ArcticScorpionEntity;
import com.shao.mythicalcreatures.entity.custom.TimberWolfEntity;
import com.shao.mythicalcreatures.entity.custom.CrabzillaEntity;
import com.shao.mythicalcreatures.entity.custom.IronWillEntity;
import com.shao.mythicalcreatures.entity.custom.SkullOfDoomEntity;
import com.shao.mythicalcreatures.entity.custom.PrinceRutherfordEntity;
import com.shao.mythicalcreatures.entity.custom.SpikezillaEntity;
import com.shao.mythicalcreatures.entity.custom.RhinocerosEntity;
import com.shao.mythicalcreatures.entity.custom.RobotSombraEntity;
import com.shao.mythicalcreatures.entity.custom.CragadileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MythicalCreaturesMod.MODID);

    public static final RegistryObject<EntityType<TwilightStarEntity>> TWILIGHT_STAR =
            ENTITY_TYPES.register("twilight_star", () ->
                    EntityType.Builder.<TwilightStarEntity>of(TwilightStarEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("twilight_star"));

    // ── 不稳定物品：可投掷，命中给魔法伤害（无爆炸） ──
    public static final RegistryObject<EntityType<UnstableItemEntity>> UNSTABLE_ITEM =
            ENTITY_TYPES.register("unstable_item", () ->
                    EntityType.Builder.<UnstableItemEntity>of(UnstableItemEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("unstable_item"));

    public static final RegistryObject<EntityType<RainbowCloudEntity>> RAINBOW_CLOUD =
            ENTITY_TYPES.register("rainbow_cloud", () ->
                    EntityType.Builder.<RainbowCloudEntity>of(RainbowCloudEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("rainbow_cloud"));

    public static final RegistryObject<EntityType<AppleProjectileEntity>> APPLE_PROJECTILE =
            ENTITY_TYPES.register("apple_projectile", () ->
                    EntityType.Builder.<AppleProjectileEntity>of(AppleProjectileEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("apple_projectile"));

    public static final RegistryObject<EntityType<TwilightSparkleEntity>> TWILIGHT_SPARKLE =
            ENTITY_TYPES.register("twilight_sparkle", () ->
                    EntityType.Builder.of(TwilightSparkleEntity::new, MobCategory.CREATURE)
                            .sized(1.6F, 2.8F)
                            .clientTrackingRange(10)
                            .build("twilight_sparkle"));

    public static final RegistryObject<EntityType<RainbowDashEntity>> RAINBOW_DASH =
            ENTITY_TYPES.register("rainbow_dash", () ->
                    EntityType.Builder.of(RainbowDashEntity::new, MobCategory.CREATURE)
                            .sized(1.7F, 2.2F)
                            .clientTrackingRange(10)
                            .build("rainbow_dash"));

    public static final RegistryObject<EntityType<ApplejackEntity>> APPLEJACK =
            ENTITY_TYPES.register("applejack", () ->
                    EntityType.Builder.of(ApplejackEntity::new, MobCategory.CREATURE)
                            .sized(2.2F, 2.2F)
                            .clientTrackingRange(10)
                            .build("applejack"));

    public static final RegistryObject<EntityType<RainbowDashSlashEntity>> RAINBOW_DASH_SLASH =
            ENTITY_TYPES.register("rainbow_dash_slash", () ->
                    EntityType.Builder.<RainbowDashSlashEntity>of(RainbowDashSlashEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("rainbow_dash_slash"));

    public static final RegistryObject<EntityType<TwilightMagicEntity>> TWILIGHT_MAGIC =
            ENTITY_TYPES.register("twilight_magic", () ->
                    EntityType.Builder.of(TwilightMagicEntity::new, MobCategory.MISC)
                            .sized(1.2F, 1.2F)
                            .clientTrackingRange(8)
                            .build("twilight_magic"));

    public static final RegistryObject<EntityType<RainbowBeamEntity>> RAINBOW_BEAM =
            ENTITY_TYPES.register("rainbow_beam", () ->
                    EntityType.Builder.<RainbowBeamEntity>of(RainbowBeamEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("rainbow_beam"));

    // ── 紫悦施法视觉特效：短暂存在的冲击波实体（仅渲染，无碰撞/无 AI） ──
    public static final RegistryObject<EntityType<MagicBurstEntity>> MAGIC_BURST =
            ENTITY_TYPES.register("magic_burst", () ->
                    EntityType.Builder.<MagicBurstEntity>of(MagicBurstEntity::new, MobCategory.MISC)
                            .sized(3.0F, 3.0F)   // 包围盒略大，避免扩散环被视锥过早剔除
                            .clientTrackingRange(16)
                            .updateInterval(2)
                            .build("magic_burst"));

    public static final RegistryObject<EntityType<PhoenixFeatherEntity>> PHOENIX_FEATHER =
            ENTITY_TYPES.register("phoenix_feather", () ->
                    EntityType.Builder.<PhoenixFeatherEntity>of(PhoenixFeatherEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("phoenix_feather"));

    public static final RegistryObject<EntityType<MeteorFireballEntity>> METEOR_FIREBALL =
            ENTITY_TYPES.register("meteor_fireball", () ->
                    EntityType.Builder.<MeteorFireballEntity>of(MeteorFireballEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("meteor_fireball"));

    // ── 趣味投掷物 ──
    public static final RegistryObject<EntityType<BalloonProjectileEntity>> BALLOON_PROJECTILE =
            ENTITY_TYPES.register("balloon_projectile", () ->
                    EntityType.Builder.<BalloonProjectileEntity>of(BalloonProjectileEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
                            .build("balloon_projectile"));
    public static final RegistryObject<EntityType<ButterflyProjectileEntity>> BUTTERFLY_PROJECTILE =
            ENTITY_TYPES.register("butterfly_projectile", () ->
                    EntityType.Builder.<ButterflyProjectileEntity>of(ButterflyProjectileEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
                            .build("butterfly_projectile"));
    public static final RegistryObject<EntityType<CupcakeProjectileEntity>> CUPCAKE_PROJECTILE =
            ENTITY_TYPES.register("cupcake_projectile", () ->
                    EntityType.Builder.<CupcakeProjectileEntity>of(CupcakeProjectileEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
                            .build("cupcake_projectile"));

    // ── 新实体（从旧模型转换而来） ──
    public static final RegistryObject<EntityType<BearEntity>> BEAR =
            ENTITY_TYPES.register("bear", () ->
                    EntityType.Builder.of(BearEntity::new, MobCategory.CREATURE)
                            .sized(5.5F, 8.0F).clientTrackingRange(10).build("bear"));
    public static final RegistryObject<EntityType<CockatriceEntity>> COCKATRICE =
            ENTITY_TYPES.register("cockatrice", () ->
                    EntityType.Builder.of(CockatriceEntity::new, MobCategory.MONSTER)
                            .sized(3.0F, 1.2F).clientTrackingRange(10).build("cockatrice"));
    public static final RegistryObject<EntityType<GarbleEntity>> GARBLE =
            ENTITY_TYPES.register("garble", () ->
                    EntityType.Builder.of(GarbleEntity::new, MobCategory.MONSTER)
                            .sized(4.5F, 3.3F).clientTrackingRange(10).build("garble"));
    public static final RegistryObject<EntityType<KingbowserEntity>> KINGBOWSER_9000 =
            ENTITY_TYPES.register("kingbowser_9000", () ->
                    EntityType.Builder.of(KingbowserEntity::new, MobCategory.MONSTER)
                            .sized(1.7F, 2.3F).clientTrackingRange(10).build("kingbowser_9000"));
    public static final RegistryObject<EntityType<ParaspriteEntity>> PARASPRITE =
            ENTITY_TYPES.register("parasprite", () ->
                    EntityType.Builder.of(ParaspriteEntity::new, MobCategory.CREATURE)
                            .sized(2.0F, 1.4F).clientTrackingRange(8).build("parasprite"));
    public static final RegistryObject<EntityType<PhoenixEntity>> PHOENIX =
            ENTITY_TYPES.register("phoenix", () ->
                    EntityType.Builder.of(PhoenixEntity::new, MobCategory.CREATURE)
                            .sized(3.5F, 2.0F).clientTrackingRange(10).build("phoenix"));
    public static final RegistryObject<EntityType<UrsamajorEntity>> URSA_MAJOR =
            ENTITY_TYPES.register("ursa_major", () ->
                    EntityType.Builder.of(UrsamajorEntity::new, MobCategory.MONSTER)
                            .sized(14.0F, 22.0F).clientTrackingRange(10).build("ursa_major"));
    // ── 新小马 ──
    public static final RegistryObject<EntityType<FluttershyEntity>> FLUTTERSHY =
            ENTITY_TYPES.register("fluttershy", () ->
                    EntityType.Builder.of(FluttershyEntity::new, MobCategory.CREATURE)
                            .sized(1.9F, 2.1F).clientTrackingRange(10).build("fluttershy"));
    public static final RegistryObject<EntityType<HolyLightRadianceEntity>> HOLY_LIGHT_RADIANCE =
            ENTITY_TYPES.register("holy_light_radiance", () ->
                    EntityType.Builder.of(HolyLightRadianceEntity::new, MobCategory.CREATURE)
                            .sized(1.6F, 2.5F).clientTrackingRange(10).build("holy_light_radiance"));
    public static final RegistryObject<EntityType<PinkiePieEntity>> PINKIE_PIE =
            ENTITY_TYPES.register("pinkie_pie", () ->
                    EntityType.Builder.of(PinkiePieEntity::new, MobCategory.CREATURE)
                            .sized(1.7F, 2.1F).clientTrackingRange(10).build("pinkie_pie"));
    public static final RegistryObject<EntityType<RarityEntity>> RARITY =
            ENTITY_TYPES.register("rarity", () ->
                    EntityType.Builder.of(RarityEntity::new, MobCategory.CREATURE)
                            .sized(2.3F, 2.1F).clientTrackingRange(10).build("rarity"));

    // ── 新导出生物（geo 模型注册） ──
    public static final RegistryObject<EntityType<BuffaloEntity>> BUFFALO =
            ENTITY_TYPES.register("buffalo", () ->
                    EntityType.Builder.of(BuffaloEntity::new, MobCategory.CREATURE)
                            .sized(3.0F, 3.3F).clientTrackingRange(10).build("buffalo"));
    public static final RegistryObject<EntityType<ChiefThunderhoovesEntity>> CHIEF_THUNDERHOOVES =
            ENTITY_TYPES.register("chief_thunderhooves", () ->
                    EntityType.Builder.of(ChiefThunderhoovesEntity::new, MobCategory.CREATURE)
                            .sized(3.2F, 3.5F).clientTrackingRange(10).build("chief_thunderhooves"));
    public static final RegistryObject<EntityType<BlackWidowEntity>> BLACK_WIDOW_SPIDER =
            ENTITY_TYPES.register("black_widow", () ->
                    EntityType.Builder.of(BlackWidowEntity::new, MobCategory.MONSTER)
                            .sized(2.6F, 1.8F).clientTrackingRange(10).build("black_widow"));
    public static final RegistryObject<EntityType<LeviathanEntity>> LEVIATHAN =
            ENTITY_TYPES.register("leviathan", () ->
                    EntityType.Builder.of(LeviathanEntity::new, MobCategory.MONSTER)
                            .sized(4.5F, 3.5F).clientTrackingRange(10).build("leviathan"));
    public static final RegistryObject<EntityType<CentipedeEntity>> CENTIPEDE =
            ENTITY_TYPES.register("centipede", () ->
                    EntityType.Builder.of(CentipedeEntity::new, MobCategory.MONSTER)
                            .sized(3.0F, 1.2F).clientTrackingRange(10).build("centipede"));
    public static final RegistryObject<EntityType<HydraEntity>> HYDRA =
            ENTITY_TYPES.register("hydra", () ->
                    EntityType.Builder.of(HydraEntity::new, MobCategory.MONSTER)
                            .sized(9.0F, 12.0F).clientTrackingRange(10).build("hydra"));
    public static final RegistryObject<EntityType<WindigoEntity>> WINDIGO =
            ENTITY_TYPES.register("windigo", () ->
                    EntityType.Builder.of(WindigoEntity::new, MobCategory.MONSTER)
                            .sized(20.0F, 8.0F).clientTrackingRange(64).build("windigo"));
    public static final RegistryObject<EntityType<BabyMooseEntity>> BABY_MOOSE =
            ENTITY_TYPES.register("baby_moose", () ->
                    EntityType.Builder.of(BabyMooseEntity::new, MobCategory.CREATURE)
                            .sized(2.0F, 1.8F).clientTrackingRange(10).build("baby_moose"));
    public static final RegistryObject<EntityType<AdultMooseEntity>> ADULT_MOOSE =
            ENTITY_TYPES.register("adult_moose", () ->
                    EntityType.Builder.of(AdultMooseEntity::new, MobCategory.CREATURE)
                            .sized(2.5F, 3.3F).clientTrackingRange(10).build("adult_moose"));
    public static final RegistryObject<EntityType<ToughGuyEntity>> TOUGH_GUY =
            ENTITY_TYPES.register("tough_guy", () ->
                    EntityType.Builder.of(ToughGuyEntity::new, MobCategory.CREATURE)
                            .sized(2.2F, 3.0F).clientTrackingRange(10).build("tough_guy"));
    public static final RegistryObject<EntityType<MavisEntity>> MAVIS =
            ENTITY_TYPES.register("mavis", () ->
                    EntityType.Builder.of(MavisEntity::new, MobCategory.CREATURE)
                            .sized(1.0F, 3.0F).clientTrackingRange(10).build("mavis"));
    public static final RegistryObject<EntityType<ManticoreEntity>> MANTICORE =
            ENTITY_TYPES.register("manticore", () ->
                    EntityType.Builder.of(ManticoreEntity::new, MobCategory.MONSTER)
                            .sized(3.5F, 4.5F).clientTrackingRange(10).build("manticore"));
    public static final RegistryObject<EntityType<RainbowCentipedeEntity>> RAINBOW_CENTIPEDE =
            ENTITY_TYPES.register("rainbow_centipede", () ->
                    EntityType.Builder.of(RainbowCentipedeEntity::new, MobCategory.MONSTER)
                            .sized(3.0F, 1.3F).clientTrackingRange(10).build("rainbow_centipede"));
    public static final RegistryObject<EntityType<ArcticScorpionEntity>> ARCTIC_SCORPION =
            ENTITY_TYPES.register("arctic_scorpion", () ->
                    EntityType.Builder.of(ArcticScorpionEntity::new, MobCategory.MONSTER)
                            .sized(4.5F, 3.0F).clientTrackingRange(10).build("arctic_scorpion"));
    public static final RegistryObject<EntityType<TimberWolfEntity>> TIMBER_WOLF =
            ENTITY_TYPES.register("timber_wolf", () ->
                    EntityType.Builder.of(TimberWolfEntity::new, MobCategory.MONSTER)
                            .sized(2.0F, 1.8F).clientTrackingRange(10).build("timber_wolf"));
    public static final RegistryObject<EntityType<CrabzillaEntity>> CRABZILLA =
            ENTITY_TYPES.register("crabzilla", () ->
                    EntityType.Builder.of(CrabzillaEntity::new, MobCategory.MONSTER)
                            .sized(9.0F, 12.0F).clientTrackingRange(10).build("crabzilla"));
    public static final RegistryObject<EntityType<IronWillEntity>> IRON_WILL =
            ENTITY_TYPES.register("iron_will", () ->
                    EntityType.Builder.of(IronWillEntity::new, MobCategory.CREATURE)
                            .sized(3.5F, 5.5F).clientTrackingRange(10).build("iron_will"));
    public static final RegistryObject<EntityType<SkullOfDoomEntity>> SKULL_OF_DOOM =
            ENTITY_TYPES.register("skull_of_doom", () ->
                    EntityType.Builder.of(SkullOfDoomEntity::new, MobCategory.MONSTER)
                            .sized(3.5F, 5.0F).clientTrackingRange(10).build("skull_of_doom"));
    public static final RegistryObject<EntityType<PrinceRutherfordEntity>> PRINCE_RUTHERFORD =
            ENTITY_TYPES.register("prince_rutherford", () ->
                    EntityType.Builder.of(PrinceRutherfordEntity::new, MobCategory.CREATURE)
                            .sized(5.5F, 7.5F).clientTrackingRange(10).build("prince_rutherford"));
    public static final RegistryObject<EntityType<SpikezillaEntity>> SPIKEZILLA =
            ENTITY_TYPES.register("spikezilla", () ->
                    EntityType.Builder.of(SpikezillaEntity::new, MobCategory.MONSTER)
                            .sized(12.0F, 18.0F).clientTrackingRange(10).build("spikezilla"));
    public static final RegistryObject<EntityType<RhinocerosEntity>> RHINOCEROS =
            ENTITY_TYPES.register("rhinoceros", () ->
                    EntityType.Builder.of(RhinocerosEntity::new, MobCategory.CREATURE)
                            .sized(4.0F, 4.8F).clientTrackingRange(10).build("rhinoceros"));
    public static final RegistryObject<EntityType<RobotSombraEntity>> ROBOT_SOMBRA =
            ENTITY_TYPES.register("robot_sombra", () ->
                    EntityType.Builder.of(RobotSombraEntity::new, MobCategory.MONSTER)
                            .sized(1.8F, 2.6F).clientTrackingRange(10).build("robot_sombra"));
    public static final RegistryObject<EntityType<CragadileEntity>> CRAGADILE =
            ENTITY_TYPES.register("cragadile", () ->
                    EntityType.Builder.of(CragadileEntity::new, MobCategory.MONSTER)
                            .sized(4.5F, 3.0F).clientTrackingRange(10).build("cragadile"));
}
