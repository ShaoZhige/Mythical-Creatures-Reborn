package com.shao.mythicalcreatures.sound;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MythicalCreaturesMod.MODID);

    public static final RegistryObject<SoundEvent> TWILIGHT_SPARKLE_AMBIENT =
            register("entity.twilight_sparkle.ambient");
    public static final RegistryObject<SoundEvent> TWILIGHT_SPARKLE_HURT =
            register("entity.twilight_sparkle.hurt");

    public static final RegistryObject<SoundEvent> RAINBOW_DASH_AMBIENT =
            register("entity.rainbow_dash.ambient");
    public static final RegistryObject<SoundEvent> RAINBOW_DASH_HURT =
            register("entity.rainbow_dash.hurt");

    public static final RegistryObject<SoundEvent> APPLEJACK_AMBIENT =
            register("entity.applejack.ambient");
    public static final RegistryObject<SoundEvent> APPLEJACK_HURT =
            register("entity.applejack.hurt");

    private static RegistryObject<SoundEvent> register(String path) {
        return SOUND_EVENTS.register(path,
                () -> SoundEvent.createVariableRangeEvent(
                        new ResourceLocation(MythicalCreaturesMod.MODID, path)));
    }
}
