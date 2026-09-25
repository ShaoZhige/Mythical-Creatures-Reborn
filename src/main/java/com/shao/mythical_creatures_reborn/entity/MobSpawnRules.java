package com.shao.mythical_creatures_reborn.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

/**
 * 刷怪放置判定谓词集合（供 {@link MobCatalog} 逐条引用）。
 *
 * <p>被动生物直接用原版 {@code Animal::checkAnimalSpawnRules}，不在本类里重复实现。
 * 这里放的是本模组特有的几条规则：通用敌对、洞穴、河岸、村庄。</p>
 *
 * <p>Spawn predicates for {@link MobCatalog}. Passive mobs use vanilla
 * {@code Animal::checkAnimalSpawnRules} directly.</p>
 */
public final class MobSpawnRules {

    private MobSpawnRules() {
    }

    /**
     * 通用敌对生物生成判定（适用于任意 Entity 子类，本项目敌对生物继承自 PonyEntity/Animal 而非 Monster）：
     * 非和平难度 + 原版 {@link Monster#isDarkEnoughToSpawn}（时间校正后的"够暗"判定）。
     *
     * <p>不能用 {@code getBrightness(LightLayer.SKY, pos)} 判夜晚：那是原始天光，露天下恒为 15、
     * 不随昼夜变化，会让敌对生物在空旷处永远刷不出来。{@code isDarkEnoughToSpawn} 用的是
     * 时间校正后的亮度（{@code getMaxLocalRawBrightness}），与僵尸/骷髅同一原理。</p>
     *
     * Generic hostile spawn rule (any entity; our hostiles extend PonyEntity/Animal, not Monster):
     * non-peaceful AND vanilla {@link Monster#isDarkEnoughToSpawn} (time-adjusted darkness).
     * The old rule used the RAW sky light (LightLayer.SKY), which is 15 in the open at ANY time of
     * day, so hostiles could never spawn in the open at night. isDarkEnoughToSpawn uses the
     * time-adjusted brightness (getMaxLocalRawBrightness), matching vanilla zombies/skeletons.
     */
    public static <T extends Entity> boolean hostile(EntityType<T> entityType, ServerLevelAccessor level,
            MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && Monster.isDarkEnoughToSpawn(level, pos, random);
    }

    /** 洞穴：完全无天光（被方块遮挡 = 地下/洞穴）。 */
    public static <T extends Entity> boolean cave(EntityType<T> entityType, ServerLevelAccessor level,
            MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && level.getBrightness(LightLayer.SKY, pos) <= 0;
    }

    /** 河边：群系由 JSON 限制为 river/beach，仅要求非和平（白天夜间皆可）。 */
    public static <T extends Entity> boolean riverbank(EntityType<T> entityType, ServerLevelAccessor level,
            MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL;
    }

    /** 村庄结构（平原/沙漠/热带草原/雪原/针叶林五种）的 ResourceKey 列表，用于"硬汉生成在村庄附近"判定。 */
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
    public static <T extends Entity> boolean village(EntityType<T> entityType, ServerLevelAccessor level,
            MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return false;
        if (!(level instanceof ServerLevel sl)) return false;
        for (var holder : VILLAGE_STRUCTURES) {
            if (sl.structureManager().getStructureWithPieceAt(pos, holder).isValid()) return true;
        }
        return false;
    }
}
