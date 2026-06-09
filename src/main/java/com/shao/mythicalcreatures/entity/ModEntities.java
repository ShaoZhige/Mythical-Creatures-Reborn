package com.shao.mythicalcreatures.entity;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.entity.custom.TwilightMagicEntity;
import com.shao.mythicalcreatures.entity.custom.TwilightSparkleEntity;
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
                            .sized(1.0F, 1.6F)
                            .clientTrackingRange(10)
                            .build("twilight_sparkle"));

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
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("meteor_fireball"));
}
