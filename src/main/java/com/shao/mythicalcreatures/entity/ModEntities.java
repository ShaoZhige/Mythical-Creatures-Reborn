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
                            .sized(5.0F, 5.0F).clientTrackingRange(10).build("bear"));
    public static final RegistryObject<EntityType<CockatriceEntity>> COCKATRICE =
            ENTITY_TYPES.register("cockatrice", () ->
                    EntityType.Builder.of(CockatriceEntity::new, MobCategory.CREATURE)
                            .sized(3.0F, 0.9F).clientTrackingRange(10).build("cockatrice"));
    public static final RegistryObject<EntityType<GarbleEntity>> GARBLE =
            ENTITY_TYPES.register("garble", () ->
                    EntityType.Builder.of(GarbleEntity::new, MobCategory.CREATURE)
                            .sized(5.0F, 3.6F).clientTrackingRange(10).build("garble"));
    public static final RegistryObject<EntityType<KingbowserEntity>> KINGBOWSER_9000 =
            ENTITY_TYPES.register("kingbowser_9000", () ->
                    EntityType.Builder.of(KingbowserEntity::new, MobCategory.CREATURE)
                            .sized(1.5F, 2.9F).clientTrackingRange(10).build("kingbowser_9000"));
    public static final RegistryObject<EntityType<ParaspriteEntity>> PARASPRITE =
            ENTITY_TYPES.register("parasprite", () ->
                    EntityType.Builder.of(ParaspriteEntity::new, MobCategory.CREATURE)
                            .sized(3.1F, 1.4F).clientTrackingRange(8).build("parasprite"));
    public static final RegistryObject<EntityType<PhoenixEntity>> PHOENIX =
            ENTITY_TYPES.register("phoenix", () ->
                    EntityType.Builder.of(PhoenixEntity::new, MobCategory.CREATURE)
                            .sized(8.0F, 2.0F).clientTrackingRange(10).build("phoenix"));
    public static final RegistryObject<EntityType<UrsamajorEntity>> URSA_MAJOR =
            ENTITY_TYPES.register("ursa_major", () ->
                    EntityType.Builder.of(UrsamajorEntity::new, MobCategory.CREATURE)
                            .sized(8.0F, 10.0F).clientTrackingRange(10).build("ursa_major"));
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
}
