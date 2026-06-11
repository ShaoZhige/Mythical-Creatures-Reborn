package com.shao.mythicalcreatures;

import com.shao.mythicalcreatures.block.ModBlocks;
import com.shao.mythicalcreatures.effect.ModEffects;
import com.shao.mythicalcreatures.entity.ModEntities;
import com.shao.mythicalcreatures.entity.custom.TwilightMagicEntity;
import com.shao.mythicalcreatures.entity.custom.TwilightSparkleEntity;
import com.shao.mythicalcreatures.item.ModItems;
import com.shao.mythicalcreatures.item.SetBonusManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

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
        TABS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);

        // Curios 联动：仅在客户端且 Curios 已加载时注册渲染器
        if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("curios")) {
            modEventBus.addListener(this::onCuriosClientSetup);
        }
    }

    private void onCuriosClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                Class.forName("com.shao.mythicalcreatures.client.CuriosIntegration")
                        .getMethod("registerRenderers")
                        .invoke(null);
            } catch (Exception ignored) {}
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(SetBonusManager::registerAllSets);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.TWILIGHT_SPARKLE.get(), TwilightSparkleEntity.createAttributes().build());
            event.put(ModEntities.TWILIGHT_MAGIC.get(), TwilightMagicEntity.createAttributes().build());
        }
    }
}
