package com.shao.mythicalcreatures;

import com.shao.mythicalcreatures.block.ModBlocks;
import com.shao.mythicalcreatures.mixin.MaxDamageCache;
import com.shao.mythicalcreatures.client.CuriosIntegration;
import com.shao.mythicalcreatures.client.CutieMarkConfig;
import com.shao.mythicalcreatures.config.MythicalConfig;
import com.shao.mythicalcreatures.effect.ModEffects;
import com.shao.mythicalcreatures.entity.ModEntities;
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
import com.shao.mythicalcreatures.entity.custom.PonyEntity;
import com.shao.mythicalcreatures.item.ModItems;
import com.shao.mythicalcreatures.item.SetBonusManager;
import com.shao.mythicalcreatures.sound.ModSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.SlotTypeMessage;

import static com.shao.mythicalcreatures.MythicalCreaturesMod.MODID;

@Mod(MODID)
public class MythicalCreaturesMod {

    public static final String MODID = "mythicalcreatures";

    private static final org.apache.logging.log4j.Logger LOGGER =
            org.apache.logging.log4j.LogManager.getLogger(MythicalCreaturesMod.class);

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> MYTHICAL_TAB = TABS.register("mythical_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mythicalcreatures"))
                    .icon(() -> new ItemStack(ModItems.TWILIGHT_SWORD.get()))
                    .displayItems((params, output) -> {
                        ModItems.ITEMS.getEntries().forEach(entry ->
                                output.accept(entry.get()));
                    })
                    .build());

    public MythicalCreaturesMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        TABS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);

        // Curios 联动：仅在客户端且 Curios 已加载时注册渲染器
        if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("curios")) {
            modEventBus.addListener(this::onCuriosClientSetup);
        }

        // 客户端配置（仅客户端注册，服务端不加载）
        if (FMLEnvironment.dist == Dist.CLIENT) {
            CutieMarkConfig.register();
        }

        // 服务端配置（自动同步到客户端）
        MythicalConfig.register();
    }

    private void onCuriosClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(CuriosIntegration::registerRenderers);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(SetBonusManager::registerAllSets);
        event.enqueueWork(MythicalCreaturesMod::registerSpawnPlacements);
    }

    /**
     * 被动生物（CREATURE）用 Animal 的草地/光照判定，敌对生物（MONSTER）用 Monster 的夜间/光照判定。
     * 真正的生物群系刷怪由 data/forge/biome_modifier 下的 JSON 控制。
     */
    private static void registerSpawnPlacements() {
        // ── 被动生物（中立）──
        SpawnPlacements.register(ModEntities.BUFFALO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.CHIEF_THUNDERHOOVES.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.BABY_MOOSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.ADULT_MOOSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.IRON_WILL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.PRINCE_RUTHERFORD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.RHINOCEROS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        // 凤凰：高山生物（CREATURE，白天刷新）
        SpawnPlacements.register(ModEntities.PHOENIX.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        // 熊 / 精灵飞蝇：各类森林（CREATURE，白天刷新）
        SpawnPlacements.register(ModEntities.BEAR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.PARASPRITE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);

        // ── 敌对生物（夜间/暗处生成）──
        // 这些实体继承自 PonyEntity（Animal），并非 Monster 子类，
        // 因此不能直接用 Monster::checkMonsterSpawnRules（其泛型上界为 Monster）。
        // 通用谓词 checkHostileSpawnRules = 非和平 + 天空亮度<=8（夜晚/洞穴/阴影）；
        // 蜈蚣用 checkCaveSpawnRules（需完全无天光=地下），螃蟹斯拉用 checkRiverbankSpawnRules（河边群系），
        // 壮汉与梅菲斯用 checkVillageSpawnRules（村庄结构范围内）。
        // 注册顺序坑：SpawnPlacements.register 第2参是 SpawnPlacements.Type，第3参才是 Heightmap.Types。
        // Registration order pitfall: in SpawnPlacements.register the 2nd arg is SpawnPlacements.Type
        // and the 3rd is Heightmap.Types (NOT the other way around).
        SpawnPlacements.register(ModEntities.BLACK_WIDOW_SPIDER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
        SpawnPlacements.register(ModEntities.LEVIATHAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
        SpawnPlacements.register(ModEntities.CENTIPEDE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkCaveSpawnRules);
        SpawnPlacements.register(ModEntities.HYDRA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
        SpawnPlacements.register(ModEntities.WINDIGO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
        SpawnPlacements.register(ModEntities.TOUGH_GUY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkVillageSpawnRules);
        // 梅菲斯：村庄结构范围内生成（MONSTER 类别使其仅夜晚刷，符合"夜晚/暗处"）
        SpawnPlacements.register(ModEntities.MAVIS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkVillageSpawnRules);
        SpawnPlacements.register(ModEntities.MANTICORE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
        SpawnPlacements.register(ModEntities.RAINBOW_CENTIPEDE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
        SpawnPlacements.register(ModEntities.ARCTIC_SCORPION.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
        SpawnPlacements.register(ModEntities.TIMBER_WOLF.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
        SpawnPlacements.register(ModEntities.CRABZILLA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkRiverbankSpawnRules);
        SpawnPlacements.register(ModEntities.SKULL_OF_DOOM.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
        SpawnPlacements.register(ModEntities.SPIKEZILLA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
        SpawnPlacements.register(ModEntities.ROBOT_SOMBRA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
        SpawnPlacements.register(ModEntities.CRAGADILE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythicalCreaturesMod::checkHostileSpawnRules);
    }

    /**
     * 通用敌对生物生成判定（适用于任意 Entity 子类，本项目敌对生物继承自 PonyEntity/Animal 而非 Monster）：
     * 非和平难度 + 夜晚或足够暗（天空亮度 <= 8，即夜间 / 洞穴 / 阴影）。
     * 原来写的是 天空亮度 <= 理论最大亮度，露天恒等成立，导致白天满地乱刷——已修正。
     *
     * Generic hostile spawn rule (any entity; our hostiles extend PonyEntity/Animal, not Monster):
     * non-peaceful difficulty AND sky light <= 8 (night / cave / shadow). An earlier version
     * compared against the *max* brightness, which is always true outdoors and caused daytime
     * spawns everywhere — now fixed.
     */
    private static <T extends net.minecraft.world.entity.Entity> boolean checkHostileSpawnRules(
            EntityType<T> pEntityType, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getDifficulty() != Difficulty.PEACEFUL
                && pLevel.getBrightness(LightLayer.SKY, pPos) <= 8;
    }

    /** 洞穴：完全无天光（被方块遮挡 = 地下/洞穴）。 */
    private static <T extends net.minecraft.world.entity.Entity> boolean checkCaveSpawnRules(
            EntityType<T> pEntityType, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getDifficulty() != Difficulty.PEACEFUL
                && pLevel.getBrightness(LightLayer.SKY, pPos) <= 0;
    }

    /** 河边：群系由 JSON 限制为 river/beach，仅要求非和平（白天夜间皆可）。 */
    private static <T extends net.minecraft.world.entity.Entity> boolean checkRiverbankSpawnRules(
            EntityType<T> pEntityType, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getDifficulty() != Difficulty.PEACEFUL;
    }

    /** 村庄结构（平原/沙漠/热带草原/雪原/针叶林五种）的 ResourceKey 列表，用于"壮汉生成在村庄附近"判定。 */
    private static final List<ResourceKey<Structure>> VILLAGE_STRUCTURES = List.of(
            BuiltinStructures.VILLAGE_PLAINS, BuiltinStructures.VILLAGE_DESERT,
            BuiltinStructures.VILLAGE_SAVANNA, BuiltinStructures.VILLAGE_SNOWY,
            BuiltinStructures.VILLAGE_TAIGA);

    /**
     * 村庄附近：非和平 + 所处位置位于某个村庄结构（平原/沙漠/热带草原/雪原/针叶林）范围内。
     * 1.20.1 没有 BuiltinTags，故用 BuiltinStructures.* 的 ResourceKey<Structure>
     * 配合 structureManager().getStructureWithPieceAt(pos, key).isValid() 判定。
     *
     * Near a village: non-peaceful AND the position lies inside one of the village structures
     * (plains/desert/savanna/snowy/taiga). 1.20.1 has no BuiltinTags, so we use
     * BuiltinStructures.* (ResourceKey<Structure>) with
     * structureManager().getStructureWithPieceAt(pos, key).isValid().
     */
    private static <T extends net.minecraft.world.entity.Entity> boolean checkVillageSpawnRules(
            EntityType<T> pEntityType, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.getDifficulty() == Difficulty.PEACEFUL) return false;
        if (!(pLevel instanceof ServerLevel sl)) return false;
        for (var holder : VILLAGE_STRUCTURES) {
            if (sl.structureManager().getStructureWithPieceAt(pPos, holder).isValid()) return true;
        }
        return false;
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onConfigLoaded(ModConfigEvent event) {
            if (event.getConfig().getSpec() != MythicalConfig.SPEC) return;

            if (event instanceof ModConfigEvent.Loading) {
                // 仅在配置**首次加载**（进入世界时）解析一次；物品/实体属性不是热加载的，
                // reload 后再解析会误导玩家以为改完就生效（实际已生成的物品修饰器不会重算）。
                MythicalConfig.DATA.bake();
            } else if (event instanceof ModConfigEvent.Reloading) {
                // 热重载：明确告知玩家属性需重启游戏才生效，不做重解析，避免误导。
                LOGGER.warn("[MythicalCreatures] common.toml 已热重载，但物品/实体属性（攻击、护甲、血量、移速等）"
                        + "不是热加载项——需【重启游戏/重新进入世界】后新配置才会生效。");
            }
        }

        @SubscribeEvent
        public static void enqueueIMC(InterModEnqueueEvent event) {
            if (!ModList.get().isLoaded("curios")) return;
            int slots = Math.max(1, (int) MythicalConfig.DATA.get("global_params", "cutie_mark_slots", 1.0));
            InterModComms.sendTo("mythicalcreatures", "curios", () ->
                new SlotTypeMessage.Builder("cutie_mark")
                    .size(slots)
                    .icon(new net.minecraft.resources.ResourceLocation("mythicalcreatures", "slot/empty_cutie_mark_slot"))
                    .build());
        }

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.TWILIGHT_SPARKLE.get(), TwilightSparkleEntity.createAttributes().build());
            event.put(ModEntities.RAINBOW_DASH.get(), RainbowDashEntity.createAttributes().build());
            event.put(ModEntities.APPLEJACK.get(), ApplejackEntity.createAttributes().build());
            event.put(ModEntities.TWILIGHT_MAGIC.get(), TwilightMagicEntity.createAttributes().build());
            event.put(ModEntities.BEAR.get(), BearEntity.createAttributes().build());
            event.put(ModEntities.COCKATRICE.get(), CockatriceEntity.createAttributes().build());
            event.put(ModEntities.KINGBOWSER_9000.get(), KingbowserEntity.createAttributes().build());
            event.put(ModEntities.PARASPRITE.get(), ParaspriteEntity.createAttributes().build());
            event.put(ModEntities.PHOENIX.get(), PhoenixEntity.createAttributes().build());
            event.put(ModEntities.URSA_MAJOR.get(), UrsamajorEntity.createAttributes().build());
            event.put(ModEntities.GARBLE.get(), GarbleEntity.createAttributes().build());
            event.put(ModEntities.FLUTTERSHY.get(), FluttershyEntity.createAttributes().build());
            event.put(ModEntities.HOLY_LIGHT_RADIANCE.get(), HolyLightRadianceEntity.createAttributes().build());
            event.put(ModEntities.PINKIE_PIE.get(), PinkiePieEntity.createAttributes().build());
            event.put(ModEntities.RARITY.get(), RarityEntity.createAttributes().build());
            event.put(ModEntities.BUFFALO.get(), BuffaloEntity.createAttributes().build());
            event.put(ModEntities.CHIEF_THUNDERHOOVES.get(), ChiefThunderhoovesEntity.createAttributes().build());
            event.put(ModEntities.BLACK_WIDOW_SPIDER.get(), BlackWidowEntity.createAttributes().build());
            event.put(ModEntities.LEVIATHAN.get(), LeviathanEntity.createAttributes().build());
            event.put(ModEntities.CENTIPEDE.get(), CentipedeEntity.createAttributes().build());
            event.put(ModEntities.HYDRA.get(), HydraEntity.createAttributes().build());
            event.put(ModEntities.WINDIGO.get(), WindigoEntity.createAttributes().build());
            event.put(ModEntities.BABY_MOOSE.get(), BabyMooseEntity.createAttributes().build());
            event.put(ModEntities.ADULT_MOOSE.get(), AdultMooseEntity.createAttributes().build());
            event.put(ModEntities.TOUGH_GUY.get(), ToughGuyEntity.createAttributes().build());
            event.put(ModEntities.MAVIS.get(), MavisEntity.createAttributes().build());
            event.put(ModEntities.MANTICORE.get(), ManticoreEntity.createAttributes().build());
            event.put(ModEntities.RAINBOW_CENTIPEDE.get(), RainbowCentipedeEntity.createAttributes().build());
            event.put(ModEntities.ARCTIC_SCORPION.get(), ArcticScorpionEntity.createAttributes().build());
            event.put(ModEntities.TIMBER_WOLF.get(), TimberWolfEntity.createAttributes().build());
            event.put(ModEntities.CRABZILLA.get(), CrabzillaEntity.createAttributes().build());
            event.put(ModEntities.IRON_WILL.get(), IronWillEntity.createAttributes().build());
            event.put(ModEntities.SKULL_OF_DOOM.get(), SkullOfDoomEntity.createAttributes().build());
            event.put(ModEntities.PRINCE_RUTHERFORD.get(), PrinceRutherfordEntity.createAttributes().build());
            event.put(ModEntities.SPIKEZILLA.get(), SpikezillaEntity.createAttributes().build());
            event.put(ModEntities.RHINOCEROS.get(), RhinocerosEntity.createAttributes().build());
            event.put(ModEntities.ROBOT_SOMBRA.get(), RobotSombraEntity.createAttributes().build());
            event.put(ModEntities.CRAGADILE.get(), CragadileEntity.createAttributes().build());
        }
    }
}
