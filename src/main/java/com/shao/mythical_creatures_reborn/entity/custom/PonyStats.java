package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import com.shao.mythical_creatures_reborn.entity.BossBarManager;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 小马属性装配组件：把 common.toml 的数值写进实体的属性表 + 统一掉落表路径。
 *
 * 从 PonyEntity 抽出，无自身状态（纯函数式），只持有 owner 引用用于读属性/判 boss。
 * 虚方法（entityId / canFly / applyCoreStats）仍留在 PonyEntity，本组件通过 owner 回调，
 * 保证 41 个子类的参数接口零改动。
 *
 * Pony attribute-assembly component: writes common.toml values into the entity's attribute map.
 * Stateless; holds the owner only to read attributes and check boss status. Virtual parameter
 * methods stay on PonyEntity and are read back through the owner, so subclasses are untouched.
 */
public final class PonyStats {

    private final PonyEntity owner;

    public PonyStats(PonyEntity owner) {
        this.owner = owner;
    }

    /**
     * 应用核心属性：MAX_HEALTH / MOVEMENT_SPEED / FLYING_SPEED(仅飞行) / ATTACK_DAMAGE / ARMOR(仅注册实体)，并回满血。
     * Apply core stats from config and heal to full.
     */
    public void applyCoreStats(String id, boolean flying) {
        var h = owner.getAttribute(Attributes.MAX_HEALTH);
        if (h != null) h.setBaseValue((float) MythicalConfig.DATA.entityAttr(id, "max_health"));
        var s = owner.getAttribute(Attributes.MOVEMENT_SPEED);
        if (s != null) s.setBaseValue((float) MythicalConfig.DATA.entityAttr(id, "move_speed"));
        if (flying) {
            var f = owner.getAttribute(Attributes.FLYING_SPEED);
            if (f != null) f.setBaseValue((float) MythicalConfig.DATA.entityAttr(id, "fly_speed"));
        }
        var d = owner.getAttribute(Attributes.ATTACK_DAMAGE);
        if (d != null) d.setBaseValue((float) MythicalConfig.DATA.entityAttr(id, "attack_damage"));
        // 护甲：仅对注册了 ARMOR 属性的实体生效（其余实体 getAttribute 返回 null 自动跳过），
        // 使 armor 与生命/移速/攻击一样可经 common.toml 覆盖并实时刷新。
        var ar = owner.getAttribute(Attributes.ARMOR);
        if (ar != null) ar.setBaseValue((float) MythicalConfig.DATA.entityAttr(id, "armor"));
        // boss 近战击退增强：原版近战击退 = 基础 0.4（LivingEntity.hurt 内硬编码）
        // + ATTACK_KNOCKBACK × 0.5（Mob.doHurtTarget）。给 boss 设 1.0 → 额外 0.5，
        // 总击退约 0.9（≈原版 2 倍），让 boss 的挥击有明显「击飞」压迫感。非 boss 保持默认 0。
        var kb = owner.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (kb != null && BossBarManager.isBoss(owner.getClass())) {
            kb.setBaseValue(1.0F);
        }
        // 索敌范围下限：统一至少 32 格（config global_params.follow_range 可调）。
        // 原版 NearestAttackableTargetGoal 的索敌半径 = FOLLOW_RANGE 属性，小马默认 16 格太近，
        // 大体型敌人（大熊座/九头蛇等）稍远就锁不到；提高后远距离也能锁定（该 goal 无视线限制，
        // 与紫悦魔法用 AABB 扫实体的索敌一致）。Math.max 保留子类显式更大的值（如雪魔 48）。
        var fr = owner.getAttribute(Attributes.FOLLOW_RANGE);
        if (fr != null) {
            double minFollow = MythicalConfig.DATA.get("global_params", "follow_range", 32.0);
            if (fr.getBaseValue() < minFollow) fr.setBaseValue(minFollow);
        }
        owner.setHealth(owner.getMaxHealth());
    }
}
