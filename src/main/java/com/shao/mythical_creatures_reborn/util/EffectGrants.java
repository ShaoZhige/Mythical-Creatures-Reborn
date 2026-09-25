package com.shao.mythical_creatures_reborn.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 药水效果（buff / debuff）的<b>唯一下发入口</b>。
 *
 * <p>所有给生物挂药水效果的代码都必须经过本类，不要直接调用
 * {@code entity.addEffect(...)} / {@code entity.removeEffect(...)}。</p>
 *
 * <h2>两种给予条件（调用前必须先选一个）</h2>
 *
 * <h3>① 限时效果 —— {@link #timed}</h3>
 * 到点自动消失，不需要任何管理。用于攻击附加（熊爪剑流血）、投掷物命中（珍贵宝石）。
 * <p>刷新语义沿用原版 {@code addEffect}：同等级再次命中会刷新到更长时长，
 * 所以「在持续时间内反复命中 = 不断重置计时」。</p>
 *
 * <h3>② 永久效果，自动添加与移除 —— {@link #autoPermanent}</h3>
 * 调用方<b>每次都把「当前该不该有」这个判断结果传进来</b>，本类据此自动添加或移除，
 * 调用方不需要自己写 if/else 去 addEffect / removeEffect。
 * 用于套装加成（条件 = 穿齐四件）、可爱标志（条件 = 背包/饰品栏里有）。</p>
 *
 * <p><b>为什么要把条件传进来，而不是让本类自己猜：</b>药水效果本身不记录来源。
 * 如果靠「等级相同 + duration&lt;0 就是本方的」来猜，遇到同名同等级就会互相误删、来回反复给
 * （buff 闪烁）；如果靠「多久没来续期」超时推断，则收回会有 1~2 个检测周期的延迟。
 * 直接把条件传进来，语义最直接：<b>true 就保证有，false 就立刻收回</b>，零延迟、零猜测。</p>
 *
 * <p><b>身份标识 {@code source} 是必须的：</b>它标明「这句话是谁说的」。同一玩家身上，
 * 苹果嘉儿套装与苹果嘉儿可爱标志都会要求「急迫 II」，两条来源各自独立记账 ——
 * 摘掉标志时只注销标志那条，套装那条还在，效果自然保留；反之亦然。
 * 若不带身份，后写入的 false 会把前一条 true 顶掉，导致本该保留的效果被误删。</p>
 *
 * <p>How to use: call {@link #timed} for finite effects and {@link #autoPermanent} for
 * permanent ones, passing your own condition and a unique source tag. Nothing else in the
 * codebase should call {@code addEffect} directly.</p>
 */
public final class EffectGrants {

    private EffectGrants() {
    }

    // ══════════════════════════════════════════════════════════════════
    //  一、限时效果
    // ══════════════════════════════════════════════════════════════════

    /**
     * 给目标挂一个限时效果，不指定施加方。
     *
     * @param durationTicks 持续时长（tick，20 tick = 1 秒）
     * @param amplifier     等级（0 = I 级）
     */
    public static void timed(LivingEntity target, MobEffect effect, int durationTicks, int amplifier) {
        timed(target, effect, durationTicks, amplifier, null);
    }

    /**
     * 给目标挂一个限时效果，并记录施加方。
     *
     * @param source 施加方实体，用于伤害归因 / 死亡消息；传 {@code null} 表示不归因
     */
    public static void timed(LivingEntity target, MobEffect effect, int durationTicks, int amplifier,
                             @Nullable Entity source) {
        if (target.level().isClientSide) return;   // 客户端效果由服务端同步，不本地下发
        MobEffectInstance instance = new MobEffectInstance(effect, durationTicks, amplifier);
        if (source != null) {
            target.addEffect(instance, source);
        } else {
            target.addEffect(instance);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  二、永久效果：自动添加与移除
    // ══════════════════════════════════════════════════════════════════

    /** 玩家 UUID → 效果 → （来源标识 → 该来源要求的等级）。 */
    private static final Map<UUID, Map<MobEffect, Map<String, Integer>>> SOURCES = new HashMap<>();

    /**
     * 自动添加与移除一个永久（无限时长）药水效果。
     *
     * <p>调用方每次检查时把「当前是否应该拥有这个效果」的结果传进来即可：</p>
     * <pre>{@code
     * // 套装：穿齐就给，脱下立刻收回
     * EffectGrants.autoPermanent(player, isWearingSet(player, set), effect, amplifier, "set:applejack");
     *
     * // 可爱标志：在背包/饰品栏里就给
     * EffectGrants.autoPermanent(player, hasItem(player, mark), effect, amplifier, "cutiemark:applejack");
     * }</pre>
     *
     * <p><b>检测频率由调用方自己决定</b>：每次调用就是一次「当前状态汇报」，本方法内部不做任何节流，
     * 调用方按自己的节奏调用即可（目前套装与可爱标志都是每 20 tick = 1 秒检查一次）。
     * 各调用方的周期是各自独立的，以后需要改成不同频率时互不影响。</p>
     *
     * <p>若目标身上已有同种效果：已有等级更高则让位（不降级），已有外源药水（duration &gt; 0）
     * 则完全不接管（避免把药水的剩余时长换成永久），等它自然过期后会自行补上。</p>
     *
     * @param shouldHave 当前是否应该有这个效果。<b>这正是"调用方知道、而本类猜不到"的关键信息</b>
     * @param amplifier  该来源要求的等级（0 = I 级）
     * @param source     来源标识，需全局唯一，例如 {@code "set:applejack"} / {@code "cutiemark:applejack"}
     */
    public static void autoPermanent(Player player, boolean shouldHave, MobEffect effect,
                                     int amplifier, String source) {
        if (player.level().isClientSide) return;

        Map<MobEffect, Map<String, Integer>> byEffect =
                SOURCES.computeIfAbsent(player.getUUID(), u -> new HashMap<>());
        Map<String, Integer> bySource = byEffect.computeIfAbsent(effect, e -> new HashMap<>());

        if (shouldHave) {
            bySource.put(source, amplifier);
        } else if (bySource.remove(source) == null) {
            // 本方<b>从未登记过</b>这个来源 —— 不是本方给的，就不该由本方收回。
            // 否则「没穿套装 / 没带标志」的那一轮空转检查会把别处给的无限 buff 误删。
            if (bySource.isEmpty()) {
                byEffect.remove(effect);
                if (byEffect.isEmpty()) SOURCES.remove(player.getUUID());
            }
            return;
        }

        // 还没有任何来源要它 → 连效果条目一起清掉，避免空 map 累积
        int desired = highestAmplifier(bySource);
        if (desired < 0) {
            byEffect.remove(effect);
            if (byEffect.isEmpty()) SOURCES.remove(player.getUUID());
        }
        applyDesired(player, effect, desired);
    }

    /**
     * 玩家退出时清理来源记录，并<b>顺手把本方给过的永久效果收回去</b>。
     *
     * <p>不能只清记录：无限 buff 会被写进存档，若只删记录，这些效果就再没有来源接管，
     * 会成为永久残留的孤儿 buff。收回后若玩家仍穿着套装，登录时第一次套装检查会重新给上。</p>
     *
     * <p>换维度 / 重生时<b>不要</b>调用本方法 —— 那时效果仍在，清掉记录就没人能收回了。</p>
     */
    public static void clear(Player player) {
        Map<MobEffect, Map<String, Integer>> byEffect = SOURCES.remove(player.getUUID());
        if (byEffect == null) return;
        for (MobEffect effect : byEffect.keySet()) {
            MobEffectInstance cur = player.getEffect(effect);
            if (cur != null && cur.getDuration() < 0) {
                player.removeEffect(effect);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  内部实现
    // ══════════════════════════════════════════════════════════════════

    /** 所有在册来源里的最高等级；没有任何来源时返回 -1。 */
    private static int highestAmplifier(Map<String, Integer> bySource) {
        int best = -1;
        for (int v : bySource.values()) {
            if (v > best) best = v;
        }
        return best;
    }

    /**
     * 把实际 buff 调整到目标等级。{@code desiredAmplifier < 0} 表示已无来源，应移除。
     * <p>
     * 几处刻意的 early-return，都是为了不把 HUD 上的 buff 图标刷得抖动、也不误伤外源药水：
     * <ul>
     *   <li>已经是目标等级的无限 buff → 完全不动（绝不重复 add，否则图标会反复刷新）；</li>
     *   <li>外源药水（duration &gt; 0）在场 → 不接管。这里刻意<b>不</b>用 {@code addEffect} 去"升级"它：
     *       {@code MobEffectInstance.update} 在等级更高时会连 duration 一起取新值，
     *       会把药水的剩余时长换成 -1（永久），等于白白吞掉玩家喝的药水。
     *       等药水自然过期后，下一次 {@link #autoPermanent} 会补上永久 buff。</li>
     * </ul>
     */
    private static void applyDesired(Player player, MobEffect effect, int desiredAmplifier) {
        MobEffectInstance cur = player.getEffect(effect);

        if (desiredAmplifier < 0) {
            // 已无任何来源：只删本方给过的无限 buff，药水等外源一律不动
            if (cur != null && cur.getDuration() < 0) player.removeEffect(effect);
            return;
        }

        if (cur != null) {
            if (cur.getDuration() >= 0) return;
            if (cur.getAmplifier() == desiredAmplifier) return;
            player.removeEffect(effect);   // 本方无限 buff 等级不符（升 / 降级）：先清，再按目标给
        }
        player.addEffect(new MobEffectInstance(effect, -1, desiredAmplifier, false, false, true));
    }
}
