package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 生物属性构造的单一入口 | Single entry point for mob attribute construction.
 * <p>
 * 背景 | Why: 此前 38 个实体各自复制同一段「生命 / 移速 / 攻击 / 护甲 / 跟随范围」
 * 的 {@code .add(...)} 调用链，彼此只差一个注册名字符串。本类把该模式收敛到一处，
 * 新增生物只需一行调用；也避免将来改判据（如统一改跟随范围）要改 38 个文件。
 * <p>
 * Previously every entity class copy-pasted the same five {@code .add(...)} lines,
 * differing only by the registry id string. Centralised here.
 * <p>
 * 行为约定 | Behaviour notes:
 * <ul>
 *   <li>数值仍全部来自 {@link MythicalConfig#DATA}，因此 {@code common.toml} 的 overrides
 *       语义完全不变。</li>
 *   <li>{@code armor}、{@code fly_speed} 等**未**在 {@code ENTITY_DEFAULTS} 登记的键仍按
 *       {@code entityAttr} 的既有约定取 0.0（护甲 0 是刻意默认值，不是漏配）。</li>
 *   <li>{@link #DEFAULT_FOLLOW_RANGE} 与全项目其它调用点保持一致，且可被
 *       {@code global_params.follow_range} 覆盖。</li>
 * </ul>
 */
public final class PonyAttributes {

    /** 跟随范围默认值（{@code global_params.follow_range} 未覆盖时使用）| default follow range */
    public static final double DEFAULT_FOLLOW_RANGE = 16.0;

    private PonyAttributes() {
    }

    /** 核心四项：生命 / 移速 / 攻击伤害 / 护甲（不含跟随范围与飞行速度）。 */
    public static AttributeSupplier.Builder core(AttributeSupplier.Builder base, String id) {
        return base
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr(id, "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr(id, "move_speed"))
                .add(Attributes.ATTACK_DAMAGE, MythicalConfig.DATA.entityAttr(id, "attack_damage"))
                .add(Attributes.ARMOR, MythicalConfig.DATA.entityAttr(id, "armor"));
    }

    /**
     * 核心三项：生命 / 移速 / 护甲——**不注册 ATTACK_DAMAGE**。
     * <p>
     * 仅供不做近战攻击的实体使用（如紫悦之杖召唤物 TwilightMagicEntity）；它与
     * {@code Mob.createMobAttributes()} 的默认属性表保持一致，不会凭空多出一条 0 点攻击属性。
     */
    public static AttributeSupplier.Builder coreNoAttack(AttributeSupplier.Builder base, String id) {
        return base
                .add(Attributes.MAX_HEALTH, MythicalConfig.DATA.entityAttr(id, "max_health"))
                .add(Attributes.MOVEMENT_SPEED, MythicalConfig.DATA.entityAttr(id, "move_speed"))
                .add(Attributes.ARMOR, MythicalConfig.DATA.entityAttr(id, "armor"));
    }

    /**
     * 标准生物（{@code TamableAnimal} 基类）：核心四项 + 全局跟随范围。
     *
     * @param id 实体注册名，如 {@code mythical_creatures_reborn:bear}
     */
    public static AttributeSupplier.Builder of(String id) {
        return core(TamableAnimal.createMobAttributes(), id)
                .add(Attributes.FOLLOW_RANGE,
                        MythicalConfig.DATA.get("global_params", "follow_range", DEFAULT_FOLLOW_RANGE));
    }

    /**
     * 标准飞行生物：{@link #of(String)} 再追加 {@code FLYING_SPEED}。
     * <p>
     * 注意：{@code fly_speed} 必须已在 {@code D.ENTITY_DEFAULTS} 登记，否则该属性静默为 0.0、实体飞不起来。
     */
    public static AttributeSupplier.Builder flying(String id) {
        return of(id).add(Attributes.FLYING_SPEED, flySpeed(id));
    }

    /**
     * 特殊生物：核心四项挂在指定基类上，跟随范围改用**实体自身键**
     * {@code get(id, "follow_range", fallback)}（雪魔 80 / 紫悦之杖召唤物 20），
     * 从而不被 {@code global_params.follow_range} 拉低。
     */
    public static AttributeSupplier.Builder scoped(AttributeSupplier.Builder base, String id, double followFallback) {
        return core(base, id)
                .add(Attributes.FOLLOW_RANGE, MythicalConfig.DATA.get(id, "follow_range", followFallback));
    }

    /** 飞行速度取值（统一按原实现收窄为 float 精度）| flying speed, narrowed to float as before. */
    public static double flySpeed(String id) {
        return (float) MythicalConfig.DATA.entityAttr(id, "fly_speed");
    }
}
