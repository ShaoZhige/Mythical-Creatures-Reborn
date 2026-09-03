package com.shao.mythical_creatures_reborn.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.function.Predicate;

/**
 * 仇恨目标过滤：所有模组生物在索敌 / 反击时统一排除下列实体，避免对装饰、训练用实体产生仇恨。
 *  - DuMmmMmmy 试验假人（mod id 为 5 个 m 的 `dummmmmmy`）：用实体类名包前缀判定，
 *    不引入编译期依赖（该模组非必装，硬依赖会在未安装时抛 ClassNotFound）。
 *  - 原版盔甲架（ArmorStand）。
 * 注意：物品展示框（ItemFrame / GlowItemFrame）继承自 Entity 而非 LivingEntity，本就不会被
 * LivingEntity 类索敌目标（NearestAttackableTargetGoal、HurtByTargetGoal、MooseProximityTargetGoal 等）
 * 选中，故天然安全，无需在此显式判断；此处仅兜底 LivingEntity 范畴内的被排除实体。
 */
public final class EntityHateFilter {

    /** DuMmmMmmy 假人模组的类包前缀 */
    private static final String DUMMY_PACKAGE = "net.mehvahdjukaar.dummmmmmy";

    private EntityHateFilter() {}

    /** 该实体是否应被一律无视（不进入任何仇恨目标） */
    public static boolean shouldIgnore(LivingEntity e) {
        if (e == null) return true;
        if (e instanceof ArmorStand) return true;   // 盔甲架
        // 假人：用类名包前缀判定，避免编译期硬依赖该模组
        return e.getClass().getName().startsWith(DUMMY_PACKAGE);
    }

    /** 供 NearestAttackableTargetGoal 等使用的谓词：目标非忽略实体时返回 true */
    public static Predicate<LivingEntity> targetable() {
        return e -> !shouldIgnore(e);
    }
}
