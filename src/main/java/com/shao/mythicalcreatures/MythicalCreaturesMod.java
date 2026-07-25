package com.shao.mythicalcreatures;

import com.shao.mythicalcreatures.block.ModBlocks;
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
import com.shao.mythicalcreatures.item.ModItems;
import com.shao.mythicalcreatures.item.SetBonusManager;
import com.shao.mythicalcreatures.sound.ModSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onConfigLoaded(ModConfigEvent event) {
            if (event.getConfig().getSpec() == MythicalConfig.SPEC)
                MythicalConfig.DATA.bake();
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
        }
    }
}
