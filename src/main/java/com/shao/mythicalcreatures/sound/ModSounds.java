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

    public static final RegistryObject<SoundEvent> ARCTIC_SCORPION_AMBIENT =
            register("entity.arctic_scorpion.ambient");
    public static final RegistryObject<SoundEvent> ARCTIC_SCORPION_HURT =
            register("entity.arctic_scorpion.hurt");
    public static final RegistryObject<SoundEvent> BEAR_AMBIENT =
            register("entity.bear.ambient");
    public static final RegistryObject<SoundEvent> BEAR_HURT =
            register("entity.bear.hurt");
    public static final RegistryObject<SoundEvent> COCKATRICE_AMBIENT =
            register("entity.cockatrice.ambient");
    public static final RegistryObject<SoundEvent> COCKATRICE_HURT =
            register("entity.cockatrice.hurt");
    public static final RegistryObject<SoundEvent> CRABZILLA_AMBIENT =
            register("entity.crabzilla.ambient");
    public static final RegistryObject<SoundEvent> CRABZILLA_HURT =
            register("entity.crabzilla.hurt");
    public static final RegistryObject<SoundEvent> CRAGADILE_AMBIENT =
            register("entity.cragadile.ambient");
    public static final RegistryObject<SoundEvent> CRAGADILE_HURT =
            register("entity.cragadile.hurt");
    public static final RegistryObject<SoundEvent> IRON_WILL_AMBIENT =
            register("entity.iron_will.ambient");
    public static final RegistryObject<SoundEvent> IRON_WILL_HURT =
            register("entity.iron_will.hurt");
    public static final RegistryObject<SoundEvent> KINGBOWSER_9000_AMBIENT =
            register("entity.kingbowser_9000.ambient");
    public static final RegistryObject<SoundEvent> KINGBOWSER_9000_HURT =
            register("entity.kingbowser_9000.hurt");
    public static final RegistryObject<SoundEvent> LEVIATHAN_AMBIENT =
            register("entity.leviathan.ambient");
    public static final RegistryObject<SoundEvent> LEVIATHAN_HURT =
            register("entity.leviathan.hurt");
    public static final RegistryObject<SoundEvent> RHINOCEROS_AMBIENT =
            register("entity.rhinoceros.ambient");
    public static final RegistryObject<SoundEvent> RHINOCEROS_HURT =
            register("entity.rhinoceros.hurt");
    public static final RegistryObject<SoundEvent> SKULL_OF_DOOM_AMBIENT =
            register("entity.skull_of_doom.ambient");
    public static final RegistryObject<SoundEvent> SKULL_OF_DOOM_HURT =
            register("entity.skull_of_doom.hurt");
    public static final RegistryObject<SoundEvent> SPIKEZILLA_AMBIENT =
            register("entity.spikezilla.ambient");
    public static final RegistryObject<SoundEvent> SPIKEZILLA_HURT =
            register("entity.spikezilla.hurt");
    public static final RegistryObject<SoundEvent> SPIKEZILLA_DEATH =
            register("entity.spikezilla.death");
    public static final RegistryObject<SoundEvent> CHIEF_THUNDERHOOVES_AMBIENT =
            register("entity.chief_thunderhooves.ambient");
    public static final RegistryObject<SoundEvent> CHIEF_THUNDERHOOVES_HURT =
            register("entity.chief_thunderhooves.hurt");
    public static final RegistryObject<SoundEvent> PRINCE_RUTHERFORD_AMBIENT =
            register("entity.prince_rutherford.ambient");
    public static final RegistryObject<SoundEvent> PRINCE_RUTHERFORD_HURT =
            register("entity.prince_rutherford.hurt");
    public static final RegistryObject<SoundEvent> BUFFALO_HURT =
            register("entity.buffalo.hurt");
    public static final RegistryObject<SoundEvent> TIMBER_WOLF_AMBIENT =
            register("entity.timber_wolf.ambient");
    public static final RegistryObject<SoundEvent> TIMBER_WOLF_HURT =
            register("entity.timber_wolf.hurt");
    public static final RegistryObject<SoundEvent> TOUGH_GUY_AMBIENT =
            register("entity.tough_guy.ambient");
    public static final RegistryObject<SoundEvent> TOUGH_GUY_HURT =
            register("entity.tough_guy.hurt");
    public static final RegistryObject<SoundEvent> TOUGH_GUY_DEATH =
            register("entity.tough_guy.death");
    public static final RegistryObject<SoundEvent> URSA_MAJOR_AMBIENT =
            register("entity.ursa_major.ambient");
    public static final RegistryObject<SoundEvent> URSA_MAJOR_HURT =
            register("entity.ursa_major.hurt");
    public static final RegistryObject<SoundEvent> WINDIGO_AMBIENT =
            register("entity.windigo.ambient");
    public static final RegistryObject<SoundEvent> WINDIGO_HURT =
            register("entity.windigo.hurt");
    public static final RegistryObject<SoundEvent> HYDRA_AMBIENT =
            register("entity.hydra.ambient");
    public static final RegistryObject<SoundEvent> HYDRA_HURT =
            register("entity.hydra.hurt");
    public static final RegistryObject<SoundEvent> MANTICORE_AMBIENT =
            register("entity.manticore.ambient");
    public static final RegistryObject<SoundEvent> MANTICORE_HURT =
            register("entity.manticore.hurt");
    public static final RegistryObject<SoundEvent> PHOENIX_AMBIENT =
            register("entity.phoenix.ambient");
    public static final RegistryObject<SoundEvent> PHOENIX_HURT =
            register("entity.phoenix.hurt");
    public static final RegistryObject<SoundEvent> GARBLE_AMBIENT =
            register("entity.garble.ambient");
    public static final RegistryObject<SoundEvent> GARBLE_HURT =
            register("entity.garble.hurt");

    private static RegistryObject<SoundEvent> register(String path) {
        return SOUND_EVENTS.register(path,
                () -> SoundEvent.createVariableRangeEvent(
                        new ResourceLocation(MythicalCreaturesMod.MODID, path)));
    }
}
