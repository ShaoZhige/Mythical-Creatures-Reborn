package com.shao.mythical_creatures_reborn.event;

import com.shao.mythical_creatures_reborn.MythicalCreaturesMod;
import com.shao.mythical_creatures_reborn.item.UrsaClawsItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 大熊座之爪双持连击：
 * 原版挥手动画一次只能挥一只手（LivingEntity 只有一个 swingingArm），
 * 且副手武器不参与攻击。这里在「主手 + 副手都是大熊座之爪」且主手
 * 满蓄力攻击后，延迟数刻让副手补一次**完整斩击**：
 * 挥击动画 + 扫击粒子 + 攻击音效 + 伤害（含锋利/AOE，受护甲减免）+ 耐久。
 * 视觉与数值上形成货真价实的左右连击。
 *
 * 防御性处理（延迟窗口内的边缘情况）：
 * - 切换武器：补击前校验主手 + 副手仍都是大熊座之爪，任一换掉则静默取消；
 * - 玩家死亡：LivingDeathEvent 立即清除，tick 侧 isAlive() 双保险；
 * - 目标死亡/消失：补击时目标无效则只挥动画不造成伤害；
 * - 目标超出范围（被击退太远/tp 走）：超过 STRIKE_RANGE 不结算伤害；
 * - 重生/跨维度（含 tp 到另一维度）：PlayerEvent.Clone /
 *   PlayerChangedDimensionEvent 直接取消；
 * - 登出：清除记录，防止 map 泄漏；
 * - 旁观模式：isSpectator() 时取消；
 * - 连点防刷：只有主手攻击蓄力 ≥90% 时才会安排副手补击。
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UrsaClawsDualWieldHandler {

    /**
     * 主手攻击后，副手补击的延迟（tick）。略错开，看起来是连击而不是同时挥。
     * Delay (ticks) before the off-hand follow-up strike, so it reads as a combo, not both hands at once.
     */
    private static final int OFFHAND_SWING_DELAY = 5;

    /** 副手补击的最大有效距离（超过则只挥动画不结算伤害） */
    private static final double STRIKE_RANGE = 6.0;

    /** 待补击信息：剩余延迟 + 目标 UUID */
    private record Pending(int ticksLeft, UUID targetId) {}

    /** 待补击的玩家 → 补击信息 */
    private static final Map<UUID, Pending> PENDING_STRIKES = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!(player.getMainHandItem().getItem() instanceof UrsaClawsItem)
                || !(player.getOffhandItem().getItem() instanceof UrsaClawsItem)) return;
        // 只有满蓄力（≥90%）攻击才触发副手补击，防止快速连点刷双倍伤害
        // Only a near-full charge (≥90%) schedules the off-hand strike, so rapid-clicking
        // cannot farm double damage.
        if (player.getAttackStrengthScale(0.5F) < 0.9F) return;
        if (!(event.getTarget() instanceof LivingEntity)) return;

        PENDING_STRIKES.put(player.getUUID(),
                new Pending(OFFHAND_SWING_DELAY, event.getTarget().getUUID()));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;
        Pending pending = PENDING_STRIKES.get(player.getUUID());
        if (pending == null) return;

        // 任何一条不满足就静默取消，绝不凭空挥手
        if (!player.isAlive() || player.isSpectator() || player.isRemoved()
                || !(player.getMainHandItem().getItem() instanceof UrsaClawsItem)
                || !(player.getOffhandItem().getItem() instanceof UrsaClawsItem)) {
            PENDING_STRIKES.remove(player.getUUID());
            return;
        }

        if (pending.ticksLeft() > 1) {
            PENDING_STRIKES.put(player.getUUID(),
                    new Pending(pending.ticksLeft() - 1, pending.targetId()));
            return;
        }

        PENDING_STRIKES.remove(player.getUUID());

        // 副手挥击动画（服务端广播，本人第一人称 + 旁观者均可见）
        player.swing(InteractionHand.OFF_HAND, true);

        // 目标仍有效且在范围内 → 结算完整斩击（伤害+AOE+粒子+音效+耐久）
        if (player.level() instanceof ServerLevel serverLevel) {
            Entity e = serverLevel.getEntity(pending.targetId());
            if (e instanceof LivingEntity target && target.isAlive()
                    && player.distanceTo(target) <= STRIKE_RANGE) {
                UrsaClawsItem.offhandStrike(player, player.getOffhandItem(), target);
            }
        }
    }

    /** 玩家死亡瞬间取消待补击（tick 侧 isAlive 为双保险） */
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            PENDING_STRIKES.remove(player.getUUID());
        }
    }

    /** 重生（含死亡重生与末地返回）：实体被重建，取消旧记录 */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        PENDING_STRIKES.remove(event.getEntity().getUUID());
    }

    /** 跨维度传送（下界门、末地门、跨维度 tp 等）：取消，避免落地后凭空挥手 */
    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        PENDING_STRIKES.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PENDING_STRIKES.remove(event.getEntity().getUUID());
    }
}
