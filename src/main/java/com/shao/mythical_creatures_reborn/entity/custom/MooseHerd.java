package com.shao.mythical_creatures_reborn.entity.custom;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * 麋鹿族群联动工具：维系「成年 + 小麋鹿」组成的家族单位行为。
 * 任一麋鹿被非麋鹿攻击时，让范围内全部麋鹿（含自身）仇恨该攻击者（猪灵式族群联动）；
 * 索敌/仇恨一律排除麋鹿自身，保证族群之间、同族群内部互不攻击。
 */
public final class MooseHerd {

    /** 一只麋鹿被攻击时，向周围多远的麋鹿广播仇恨 */
    public static final double ALERT_RADIUS = 20.0D;

    private MooseHerd() {}

    /** 是否为麋鹿（成年或幼体） */
    public static boolean isMoose(Entity e) {
        return e instanceof AdultMooseEntity || e instanceof BabyMooseEntity;
    }

    /**
     * 族群仇恨广播：范围内所有麋鹿把攻击者设为当前目标。
     * 传入的 attacker 必须非麋鹿（调用方已保证），避免族群互相仇恨。
     */
    public static void alert(LivingEntity attacker, Level level, double x, double y, double z) {
        AABB box = new AABB(
                x - ALERT_RADIUS, y - ALERT_RADIUS, z - ALERT_RADIUS,
                x + ALERT_RADIUS, y + ALERT_RADIUS, z + ALERT_RADIUS);
        for (Entity e : level.getEntities((Entity) null, box)) {
            if (isMoose(e) && e instanceof Mob mob) {
                mob.setTarget(attacker);
            }
        }
    }
}
