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
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
