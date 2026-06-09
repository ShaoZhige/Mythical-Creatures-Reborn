package com.shao.mythicalcreatures.block;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MythicalCreaturesMod.MODID);

    public static final RegistryObject<Block> APPLE = register("apple_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                    .strength(2.0F, 6.0F).sound(SoundType.WOOD).friction(0.98F)));

    public static final RegistryObject<Block> ARCTIC = register("arctic_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.ICE)
                    .strength(10.0F, 50.0F).sound(SoundType.STONE).friction(1.2F)));

    public static final RegistryObject<Block> BEAR_FUR = register("bear_fur_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN)
                    .strength(1.0F, 20.0F).sound(SoundType.WOOL)));

    public static final RegistryObject<Block> BONE = register("bone_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.SAND)
                    .strength(2.0F, 6.0F).sound(SoundType.BONE_BLOCK)));

    public static final RegistryObject<Block> CRYSTAL = register("crystal_block",
            () -> new CrystalBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND)
                    .strength(30.0F, 45.0F).sound(SoundType.GLASS).lightLevel(s -> 8)));

    public static final RegistryObject<Block> DARK_CRYSTAL = register("dark_crystal_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE)
                    .strength(800.0F, 4000.0F).sound(SoundType.GLASS)));

    public static final RegistryObject<Block> HARD_APPLE = register("hard_apple_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                    .strength(12.0F, 60.0F).sound(SoundType.GLASS).friction(1.05F)));

    public static final RegistryObject<Block> PHOENIX = register("phoenix_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.FIRE)
                    .strength(25.0F, 250.0F).sound(SoundType.GLASS).lightLevel(s -> 15)));

    public static final RegistryObject<Block> ROBOT = register("robot_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                    .strength(80.0F, 400.0F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> TWILICORN = register("twilicorn_block",
            () -> new TwilicornBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE)
                    .strength(25.0F, 750.0F).sound(SoundType.GLASS).lightLevel(s -> 15)));

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> reg = BLOCKS.register(name, block);
        return reg;
    }
}
