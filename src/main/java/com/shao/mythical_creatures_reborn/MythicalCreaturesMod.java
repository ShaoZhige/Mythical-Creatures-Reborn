package com.shao.mythical_creatures_reborn;

import com.shao.mythical_creatures_reborn.block.ModBlocks;
import com.shao.mythical_creatures_reborn.mixin.MaxDamageCache;
import com.shao.mythical_creatures_reborn.client.CuriosIntegration;
import com.shao.mythical_creatures_reborn.client.CutieMarkConfig;
import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import com.shao.mythical_creatures_reborn.effect.ModEffects;
import com.shao.mythical_creatures_reborn.entity.MobCatalog;
import com.shao.mythical_creatures_reborn.entity.ModEntities;
import com.shao.mythical_creatures_reborn.item.ModItems;
import com.shao.mythical_creatures_reborn.item.SetBonusManager;
import com.shao.mythical_creatures_reborn.sound.ModSounds;
import com.shao.mythical_creatures_reborn.util.GeckoLibCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

import static com.shao.mythical_creatures_reborn.MythicalCreaturesMod.MODID;

@Mod(MODID)
public class MythicalCreaturesMod {

    public static final String MODID = "mythical_creatures_reborn";

    private static final org.apache.logging.log4j.Logger LOGGER =
            org.apache.logging.log4j.LogManager.getLogger(MythicalCreaturesMod.class);

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> MYTHICAL_TAB = TABS.register("mythical_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mythical_creatures_reborn"))
                    .icon(() -> new ItemStack(ModItems.TWILIGHT_SWORD.get()))
                    .displayItems((params, output) -> {
                        ModItems.ITEMS.getEntries().forEach(entry ->
                                output.accept(entry.get()));
                    })
                    .build());

    public MythicalCreaturesMod() {
        // GeckoLib 自检必须先于任何注册动作：一旦缺失，后续实体 / 渲染器类会随机抛出 NoClassDefFoundError
        GeckoLibCompat.verifyOrThrow();

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
        // 注册自定义网络通道（坐骑下降键等自定义按键同步）
        com.shao.mythical_creatures_reborn.network.ModNetwork.register();
    }

    /**
     * 按 {@link MobCatalog} 逐条注册刷怪放置规则（谓词本身写在元数据表里，见 {@code MobCatalog.MobDef#spawnRule}）。
     *
     * <p>被动生物用原版 {@code Animal::checkAnimalSpawnRules}，敌对生物的判定见
     * {@link com.shao.mythical_creatures_reborn.entity.MobSpawnRules}；真正的生物群系刷怪由
     * data/forge/biome_modifier 下的 JSON 控制。</p>
     *
     * <p>注册顺序坑：SpawnPlacements.register 第2参是 SpawnPlacements.Type，第3参才是 Heightmap.Types。
     * Registration order pitfall: in SpawnPlacements.register the 2nd arg is SpawnPlacements.Type
     * and the 3rd is Heightmap.Types (NOT the other way around).</p>
     */
    private static void registerSpawnPlacements() {
        for (MobCatalog.MobDef<?> mob : MobCatalog.ALL) {
            registerSpawnPlacement(mob);
        }
    }

    /** 捕获通配符：把 {@link MobCatalog.MobDef} 的具体实体类型传进泛型的 {@link SpawnPlacements#register}。 */
    private static <T extends Mob & GeoAnimatable> void registerSpawnPlacement(MobCatalog.MobDef<T> mob) {
        if (mob.spawnRule() == null) {
            return; // 召唤物等不参与自然刷怪，不注册放置规则
        }
        SpawnPlacements.register(mob.type().get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mob.spawnRule());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onConfigLoaded(ModConfigEvent event) {
            if (event.getConfig().getSpec() == CutieMarkConfig.SPEC) {
                CutieMarkConfig.CLIENT_CONFIG = event.getConfig();
                return;
            }
            if (event.getConfig().getSpec() != MythicalConfig.SPEC) return;
            MythicalConfig.COMMON_CONFIG = event.getConfig();

            if (event instanceof ModConfigEvent.Loading) {
                // 仅在配置**首次加载**（进入世界时）解析一次；物品/实体属性不是热加载的，
                // reload 后再解析会误导玩家以为改完就生效（实际已生成的物品修饰器不会重算）。
                MythicalConfig.DATA.bake();
                // 配置重新解析后，物品耐久覆盖缓存需失效（MaxDamageCache.clear() 接线）
                MaxDamageCache.clear();
            } else if (event instanceof ModConfigEvent.Reloading) {
                // 热重载：明确告知玩家属性需重启游戏才生效，不做重解析，避免误导。
                LOGGER.warn("[MythicalCreatures] common.toml 已热重载，但物品/实体属性（攻击、护甲、血量、移速等）"
                        + "不是热加载项——需【重启游戏/重新进入世界】后新配置才会生效。");
            }
        }

        /** 按 {@link MobCatalog} 逐条注册生物属性（顺序沿用原手工登记顺序）。 */
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            for (MobCatalog.MobDef<?> mob : MobCatalog.ALL) {
                putAttributesFor(event, mob);
            }
        }

        /** 捕获通配符：{@code MobDef<?>} 不能直接喂给泛型的 {@code event.put(EntityType<T>, ...)}。 */
        private static <T extends Mob & GeoAnimatable> void putAttributesFor(
                EntityAttributeCreationEvent event, MobCatalog.MobDef<T> mob) {
            event.put(mob.type().get(), mob.attributes().get().build());
        }
    }
}
