package com.shao.mythical_creatures_reborn.entity;

import com.shao.mythical_creatures_reborn.entity.custom.AdultMooseEntity;
import com.shao.mythical_creatures_reborn.entity.custom.ApplejackEntity;
import com.shao.mythical_creatures_reborn.entity.custom.ArcticScorpionEntity;
import com.shao.mythical_creatures_reborn.entity.custom.BabyMooseEntity;
import com.shao.mythical_creatures_reborn.entity.custom.BearEntity;
import com.shao.mythical_creatures_reborn.entity.custom.BlackWidowEntity;
import com.shao.mythical_creatures_reborn.entity.custom.BuffaloEntity;
import com.shao.mythical_creatures_reborn.entity.custom.CentipedeEntity;
import com.shao.mythical_creatures_reborn.entity.custom.ChiefThunderhoovesEntity;
import com.shao.mythical_creatures_reborn.entity.custom.CockatriceEntity;
import com.shao.mythical_creatures_reborn.entity.custom.CrabzillaEntity;
import com.shao.mythical_creatures_reborn.entity.custom.CragadileEntity;
import com.shao.mythical_creatures_reborn.entity.custom.FluttershyEntity;
import com.shao.mythical_creatures_reborn.entity.custom.GarbleEntity;
import com.shao.mythical_creatures_reborn.entity.custom.HolyLightRadianceEntity;
import com.shao.mythical_creatures_reborn.entity.custom.HydraEntity;
import com.shao.mythical_creatures_reborn.entity.custom.IronWillEntity;
import com.shao.mythical_creatures_reborn.entity.custom.KingbowserEntity;
import com.shao.mythical_creatures_reborn.entity.custom.LeviathanEntity;
import com.shao.mythical_creatures_reborn.entity.custom.ManticoreEntity;
import com.shao.mythical_creatures_reborn.entity.custom.MavisEntity;
import com.shao.mythical_creatures_reborn.entity.custom.ParaspriteEntity;
import com.shao.mythical_creatures_reborn.entity.custom.PhoenixEntity;
import com.shao.mythical_creatures_reborn.entity.custom.PinkiePieEntity;
import com.shao.mythical_creatures_reborn.entity.custom.PrinceRutherfordEntity;
import com.shao.mythical_creatures_reborn.entity.custom.RainbowCentipedeEntity;
import com.shao.mythical_creatures_reborn.entity.custom.RainbowDashEntity;
import com.shao.mythical_creatures_reborn.entity.custom.RarityEntity;
import com.shao.mythical_creatures_reborn.entity.custom.RhinocerosEntity;
import com.shao.mythical_creatures_reborn.entity.custom.RobotSombraEntity;
import com.shao.mythical_creatures_reborn.entity.custom.SkullOfDoomEntity;
import com.shao.mythical_creatures_reborn.entity.custom.SpikezillaEntity;
import com.shao.mythical_creatures_reborn.entity.custom.TimberWolfEntity;
import com.shao.mythical_creatures_reborn.entity.custom.ToughGuyEntity;
import com.shao.mythical_creatures_reborn.entity.custom.TwilightMagicEntity;
import com.shao.mythical_creatures_reborn.entity.custom.TwilightSparkleEntity;
import com.shao.mythical_creatures_reborn.entity.custom.UrsamajorEntity;
import com.shao.mythical_creatures_reborn.entity.custom.WindigoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Animal;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

import java.util.List;
import java.util.function.Supplier;

/**
 * 生物注册元数据表 —— 新增一个生物时**唯一的登记处**。
 *
 * <p>在此之前，加一个生物要手工改 6 处：{@link ModEntities} 里加字段、{@code MythicalCreaturesMod}
 * 里加属性注册、加刷怪放置、{@code ClientSetup} 里加渲染器、再新建一个 Model 和一个 Renderer 类。
 * 现在后四项全部由本表驱动，只剩「{@link ModEntities} 加一个字段 + 本表加一行」。</p>
 *
 * <p>字段含义：</p>
 * <ul>
 *   <li>{@code type} —— {@link ModEntities} 里的注册对象（字段仍保留，因为刷怪蛋等物品要直接引用）；</li>
 *   <li>{@code attributes} —— 实体类的 {@code createAttributes()}，由 {@link PonyAttributes} 统一构造；</li>
 *   <li>{@code spawnRule} —— 刷怪放置谓词；被动生物用 {@code Animal::checkAnimalSpawnRules}，
 *       敌对的几条见 {@link MobSpawnRules}；{@code null} 表示不参与自然刷怪（召唤物等）；</li>
 *   <li>{@code renderBase} —— 客户端资源基准名，对应 {@code geo/&lt;名&gt;.geo.json} +
 *       {@code textures/entity/&lt;名&gt;.png}（与实体 id 不必相同，但要与磁盘上的文件一致）；</li>
 *   <li>{@code animation} —— 动画基准名，传 {@code null} 表示与 {@code renderBase} 同名。
 *       38 个生物现在都有各自的动画文件，因此本表全部走同名回落；字段保留是为了给
 *       「确实要复用别的动画文件」留出口；</li>
 *   <li>{@code cullDisabled} —— 超大型实体禁用视锥剔除。</li>
 * </ul>
 *
 * <p>Registration catalog for every mob in this mod: the single place to touch when adding one.
 * Drives attribute registration, spawn placements and client renderers.</p>
 */
public final class MobCatalog {

    private MobCatalog() {
    }

    /**
     * 一条生物定义。
     *
     * @param type         实体类型注册对象
     * @param attributes   属性构造器
     * @param spawnRule    刷怪放置谓词（null = 不注册）
     * @param renderBase   客户端资源基准名
     * @param animation    动画基准名（null = 与 renderBase 同名）
     * @param cullDisabled 是否禁用视锥剔除
     */
    public record MobDef<T extends Mob & GeoAnimatable>(
            RegistryObject<EntityType<T>> type,
            Supplier<AttributeSupplier.Builder> attributes,
            SpawnPlacements.SpawnPredicate<T> spawnRule,
            String renderBase,
            String animation,
            boolean cullDisabled) {

        /** 动画名为空时回落到资源基准名，省得每行都写两遍。 */
        public MobDef {
            if (animation == null) {
                animation = renderBase;
            }
        }
    }

    /** 常规生物：动画与资源同名，启用视锥剔除。 */
    public static <T extends Mob & GeoAnimatable> MobDef<T> mob(
            RegistryObject<EntityType<T>> type, Supplier<AttributeSupplier.Builder> attributes,
            SpawnPlacements.SpawnPredicate<T> spawnRule, String renderBase) {
        return new MobDef<>(type, attributes, spawnRule, renderBase, null, false);
    }

    /** 超大型生物：禁用视锥剔除，防止抬头 / 靠近时模型被剔除而消失（动画与资源同名）。 */
    public static <T extends Mob & GeoAnimatable> MobDef<T> giant(
            RegistryObject<EntityType<T>> type, Supplier<AttributeSupplier.Builder> attributes,
            SpawnPlacements.SpawnPredicate<T> spawnRule, String renderBase) {
        return new MobDef<>(type, attributes, spawnRule, renderBase, null, true);
    }

    /**
     * 全部生物。顺序沿用原 {@code registerAttributes} 的登记顺序，保证注册次序与改造前一致。
     */
    public static final List<MobDef<?>> ALL = List.of(
            // ── 小马主角与召唤物 ──
            mob(ModEntities.TWILIGHT_SPARKLE, TwilightSparkleEntity::createAttributes, Animal::checkAnimalSpawnRules, "twilight_sparkle"),
            mob(ModEntities.RAINBOW_DASH, RainbowDashEntity::createAttributes, Animal::checkAnimalSpawnRules, "rainbow_dash"),
            mob(ModEntities.APPLEJACK, ApplejackEntity::createAttributes, Animal::checkAnimalSpawnRules, "applejack"),
            mob(ModEntities.TWILIGHT_MAGIC, TwilightMagicEntity::createAttributes, null, "twilight_magic"),

            // ── 已有专属动画的生物 ──
            mob(ModEntities.BEAR, BearEntity::createAttributes, Animal::checkAnimalSpawnRules, "bear"),
            mob(ModEntities.COCKATRICE, CockatriceEntity::createAttributes, MobSpawnRules::hostile, "cockatrice"),
            mob(ModEntities.KINGBOWSER_9000, KingbowserEntity::createAttributes, MobSpawnRules::hostile, "kingbowser_9000"),
            mob(ModEntities.PARASPRITE, ParaspriteEntity::createAttributes, Animal::checkAnimalSpawnRules, "parasprite"),
            mob(ModEntities.PHOENIX, PhoenixEntity::createAttributes, Animal::checkAnimalSpawnRules, "phoenix"),
            giant(ModEntities.URSA_MAJOR, UrsamajorEntity::createAttributes, MobSpawnRules::hostile, "ursa_major"),
            mob(ModEntities.GARBLE, GarbleEntity::createAttributes, MobSpawnRules::hostile, "garble"),
            mob(ModEntities.FLUTTERSHY, FluttershyEntity::createAttributes, Animal::checkAnimalSpawnRules, "fluttershy"),
            mob(ModEntities.HOLY_LIGHT_RADIANCE, HolyLightRadianceEntity::createAttributes, Animal::checkAnimalSpawnRules, "holy_light_radiance"),
            mob(ModEntities.PINKIE_PIE, PinkiePieEntity::createAttributes, Animal::checkAnimalSpawnRules, "pinkie_pie"),
            mob(ModEntities.RARITY, RarityEntity::createAttributes, Animal::checkAnimalSpawnRules, "rarity"),

            // ── 其余生物（全部使用各自的 <renderBase>.animation.json）──
            // 2026-09-19：38 个生物的专属动画全部导出完成，原先 20 处指向共享占位动画
            // mod_placeholder 的接线全部改回自己的文件，否则 Blockbench 里做好的动作在游戏里不生效。
            mob(ModEntities.BUFFALO, BuffaloEntity::createAttributes, Animal::checkAnimalSpawnRules, "buffalo"),
            mob(ModEntities.CHIEF_THUNDERHOOVES, ChiefThunderhoovesEntity::createAttributes, Animal::checkAnimalSpawnRules, "chiefthunderhooves"),
            mob(ModEntities.BLACK_WIDOW_SPIDER, BlackWidowEntity::createAttributes, MobSpawnRules::hostile, "blackwidow"),
            mob(ModEntities.LEVIATHAN, LeviathanEntity::createAttributes, MobSpawnRules::hostile, "leviathan"),
            mob(ModEntities.CENTIPEDE, CentipedeEntity::createAttributes, MobSpawnRules::cave, "centipede"),
            // 九头蛇：用自己的 hydra.animation.json（idle / walk / run / attack）。它曾与此表的其它
            // 生物一样错指向共享的 mod_placeholder 空动画，导致 BB 里做好的动画一直没生效
            // —— 详见 2026-09-15 的动画接线排查。
            mob(ModEntities.HYDRA, HydraEntity::createAttributes, MobSpawnRules::hostile, "hydra"),
            giant(ModEntities.WINDIGO, WindigoEntity::createAttributes, MobSpawnRules::hostile, "windigo"),
            mob(ModEntities.BABY_MOOSE, BabyMooseEntity::createAttributes, Animal::checkAnimalSpawnRules, "moose"),
            mob(ModEntities.ADULT_MOOSE, AdultMooseEntity::createAttributes, Animal::checkAnimalSpawnRules, "moosebig"),
            mob(ModEntities.TOUGH_GUY, ToughGuyEntity::createAttributes, MobSpawnRules::village, "toughguy"),
            mob(ModEntities.MAVIS, MavisEntity::createAttributes, MobSpawnRules::village, "mavis"),
            mob(ModEntities.MANTICORE, ManticoreEntity::createAttributes, MobSpawnRules::hostile, "manticore"),
            mob(ModEntities.RAINBOW_CENTIPEDE, RainbowCentipedeEntity::createAttributes, MobSpawnRules::hostile, "giantcentipede"),
            mob(ModEntities.ARCTIC_SCORPION, ArcticScorpionEntity::createAttributes, MobSpawnRules::hostile, "arcticscorpion"),
            mob(ModEntities.TIMBER_WOLF, TimberWolfEntity::createAttributes, MobSpawnRules::hostile, "timberwolf"),
            mob(ModEntities.CRABZILLA, CrabzillaEntity::createAttributes, MobSpawnRules::riverbank, "crabzilla"),
            mob(ModEntities.IRON_WILL, IronWillEntity::createAttributes, Animal::checkAnimalSpawnRules, "ironwill"),
            mob(ModEntities.SKULL_OF_DOOM, SkullOfDoomEntity::createAttributes, MobSpawnRules::hostile, "skullofdoom"),
            mob(ModEntities.PRINCE_RUTHERFORD, PrinceRutherfordEntity::createAttributes, Animal::checkAnimalSpawnRules, "princeyakfur"),
            giant(ModEntities.SPIKEZILLA, SpikezillaEntity::createAttributes, MobSpawnRules::hostile, "spikezilla"),
            mob(ModEntities.RHINOCEROS, RhinocerosEntity::createAttributes, Animal::checkAnimalSpawnRules, "rhinoceros"),
            mob(ModEntities.ROBOT_SOMBRA, RobotSombraEntity::createAttributes, MobSpawnRules::hostile, "robot_sombra"),
            mob(ModEntities.CRAGADILE, CragadileEntity::createAttributes, MobSpawnRules::hostile, "cragadile")
    );
}
