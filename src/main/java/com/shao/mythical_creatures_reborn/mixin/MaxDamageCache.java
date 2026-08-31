package com.shao.mythical_creatures_reborn.mixin;

import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 物品 max_damage 覆盖值的缓存。| Cache for the per-item max_damage override.
 * <p>
 * getMaxDamage 是高频路径（热栏每帧、物品栏/铁砧/创造标签搜索等都会调用），
 * 这里把「物品 -> 覆盖耐久」缓存起来，避免每帧做注册表查找 + 配置查找。
 * 与 mixin 分离成普通类，避免 @Mixin 把静态字段合并进目标类导致外部无法可靠清理。
 * </p>
 * <p>
 * 配置重载（bake）时必须调用 {@link #clear()} 使缓存失效。
 * </p>
 */
public final class MaxDamageCache {

    /** 物品 -> 覆盖耐久；-1 表示无覆盖（未配置 max_damage 或覆盖值<=0）。键只看物品本身，与堆叠数据无关。 */
    private static final Map<Item, Integer> CACHE = new ConcurrentHashMap<>();

    private MaxDamageCache() {}

    /**
     * 取缓存的覆盖耐久；若未缓存则通过 supplier 计算并写入。
     * @param item 物品
     * @param compute 计算函数（仅在缓存未命中时调用一次）
     * @return 覆盖耐久；无覆盖时返回 -1
     */
    public static int getOrCompute(Item item, java.util.function.IntSupplier compute) {
        Integer cached = CACHE.get(item);
        if (cached != null) return cached;
        int raw = compute.getAsInt();
        int value = raw > 0 ? raw : -1;
        CACHE.put(item, value);
        return value;
    }

    /** 配置重载时清空缓存，使其重新读取最新覆盖值。 */
    public static void clear() {
        CACHE.clear();
    }
}
