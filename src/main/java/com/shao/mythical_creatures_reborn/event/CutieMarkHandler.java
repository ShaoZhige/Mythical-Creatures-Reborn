package com.shao.mythical_creatures_reborn.event;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.effect.ModEffects;
import com.shao.mythical_creatures_reborn.item.ModItems;
import com.shao.mythical_creatures_reborn.util.EffectGrants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 可爱标志 Buff：无限时长，每 {@value #CHECK_INTERVAL} tick（1 秒）检查一次，放入给予、取出移除。
 *
 * <p>与套装的差异只在「怎么判断该不该有」：套装读的是装备槽，纯同步内存读、结果永远确定；
 * 可爱标志还要额外查 Curios 饰品栏，而 Curios 的库存是 Capability，<b>可能未就绪或查询失败</b>。
 * 因此 {@link #hasItem} 返回三态 —— 明确有 / 明确无 / 查不到（{@code null}），
 * 只有拿到前两种<b>明确结论</b>时才通知 {@link EffectGrants}。</p>
 *
 * <p><b>本类不需要任何防抖机制。</b>只要把"查不到"如实报成 {@code null}（保持现状），
 * 就不存在「偶发抖动导致误判取下」的问题。旧版曾用「连续 3 次未命中才移除」来掩盖它，
 * 那只是因为当时把"查询失败"和"确实没有"混为一谈了；三态区分之后，
 * 查询成功即真值（背包与 Curios 槽内容都是内存数据），可以放心立即生效 ——
 * 摘下标志会<b>立刻</b>收回，不必等 3 秒。</p>
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CutieMarkHandler {

    private record EffectInfo(Supplier<MobEffect> effect, int amplifier) {}

    /** 可爱标志 → 效果表 */
    private static final Map<RegistryObject<? extends Item>, EffectInfo[]> CUTIEMARK_EFFECTS = new LinkedHashMap<>();

    /**
     * 可爱标志的检查周期（tick）：20 tick = 1 秒。
     *
     * <p>这是<b>可爱标志自己的节奏</b>，与套装各管各的（目前两边都取 20，
     * 但以后需要不同频率时可以各自调整，互不影响）。</p>
     */
    private static final int CHECK_INTERVAL = 20;

    static {
        CUTIEMARK_EFFECTS.put(ModItems.APPLEJACK_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.DIG_SPEED, 1)});
        CUTIEMARK_EFFECTS.put(ModItems.PINKIE_PIE_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.JUMP, 1)});
        CUTIEMARK_EFFECTS.put(ModItems.FLUTTERSHY_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.REGENERATION, 0)});
        CUTIEMARK_EFFECTS.put(ModItems.RARITY_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(ModEffects.REPAIR, 0)});
        CUTIEMARK_EFFECTS.put(ModItems.RAINBOW_DASH_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.MOVEMENT_SPEED, 1)});
        CUTIEMARK_EFFECTS.put(ModItems.TWILIGHT_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.WATER_BREATHING, 0),
                new EffectInfo(() -> MobEffects.NIGHT_VISION, 0)});
        CUTIEMARK_EFFECTS.put(ModItems.HOLY_LIGHT_RADIANCE_CUTIEMARK, new EffectInfo[]{
                new EffectInfo(() -> MobEffects.GLOWING, 0)});
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        // 每 20 tick（1 秒）检查一次。套装那边的周期也是 20，两者在同一 tick 一起跑。
        if (player.tickCount % CHECK_INTERVAL != 0) return;

        for (var entry : CUTIEMARK_EFFECTS.entrySet()) {
            Item item = entry.getKey().get();
            Boolean found = hasItem(player, item);

            // Curios 未就绪 / 查询失败 → 本轮无法判定，保持现状：
            // 既不当作"没有"（那会误删玩家背着的标志的 buff），也不当作"有"。
            if (found == null) continue;

            // 来源标识：套装侧是 "set:<套装id>"，这里用 "cutiemark:<物品注册名>"，互不冲突。
            String source = "cutiemark:" + BuiltInRegistries.ITEM.getKey(item);

            for (EffectInfo info : entry.getValue()) {
                // found 已是明确结论：true 保证有、false 立刻收回
                EffectGrants.autoPermanent(player, found, info.effect().get(),
                        info.amplifier(), source);
            }
        }
    }

    /**
     * 检查玩家是否携带指定的可爱标志 —— <b>两个来源缺一不可</b>：
     * <ol>
     *   <li><b>物品栏</b>：主背包 36 格（{@link Inventory#getContainerSize()} 覆盖的范围）；</li>
     *   <li><b>饰品栏</b>：安装了 Curios 时的<b>所有</b>饰品槽（逐个槽类型、逐格检查）。</li>
     * </ol>
     * 两者是「或」的关系：放在任意一处都算携带，从一处移到另一处不会中断 buff。
     *
     * @return {@link Boolean#TRUE}/{@link Boolean#FALSE} = 明确的有 / 无；
     *         {@code null} = <b>本次无法判定</b>（Curios Capability 未就绪，或库存查询抛异常）——
     *         调用方必须保持现状，不能当作"没有"，否则会把玩家明明带着的标志误判成取下
     */
    @Nullable
    private static Boolean hasItem(Player player, Item item) {
        // 来源一：物品栏（同步内存读，结果永远可信）
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).is(item)) return Boolean.TRUE;
        }
        if (!ModList.get().isLoaded("curios")) return Boolean.FALSE;

        // 来源二：Curios 饰品栏（所有槽类型）
        try {
            var handler = CuriosApi.getCuriosInventory(player).resolve();
            // 🔴 Capability 尚未挂载时（玩家刚登录 / 换维度 / 初始化中）拿到的是 empty。
            //    这是"查不到"，必须报 null —— 若当成"玩家没带"，buff 会被误删并来回闪。
            if (handler.isEmpty()) return null;

            var curios = handler.get();
            for (var slotEntry : curios.getCurios().entrySet()) {
                for (int i = 0; i < slotEntry.getValue().getStacks().getSlots(); i++) {
                    if (slotEntry.getValue().getStacks().getStackInSlot(i).is(item)) return Boolean.TRUE;
                }
            }
        } catch (Exception e) {
            // 库存 future 异常完成时抛 CompletionException —— 同样属于"查不到"，保持现状
            org.apache.logging.log4j.LogManager.getLogger(CutieMarkHandler.class)
                    .warn("Curios 库存查询失败，本次跳过可爱标志检查", e);
            return null;
        }
        return Boolean.FALSE;
    }
}
