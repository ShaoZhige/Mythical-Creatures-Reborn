package com.shao.mythicalcreatures.effect;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MythicalCreaturesMod.MODID);

    public static final RegistryObject<MobEffect> BLEEDING = EFFECTS.register("bleeding",
            BleedingEffect::new);
    public static final RegistryObject<MobEffect> REPAIR = EFFECTS.register("repair",
            RepairEffect::new);
}
