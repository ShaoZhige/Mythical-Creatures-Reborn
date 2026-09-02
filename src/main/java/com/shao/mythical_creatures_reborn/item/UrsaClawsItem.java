package com.shao.mythical_creatures_reborn.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 大熊座之爪（Ursa Claws）
 * 单次挥击 100 点伤害（基础值 = 空手基准1.0 + 构造attackDamage87 + URSA Tier加成12），
 * 走 SwordItem 默认逻辑 → 可正常附魔「锋利」且受护甲减免；
 * 攻速修正 -3.65F → 实际 0.35 次/秒（原 -3.3F 的 0.7 对半砍更慢）。
 * 命中时对周围敌人追加 AOE 伤害（同样含锋利加成），半径见 AOE_RANGE。
 * 双持时副手会延迟补一次完整斩击（动画+粒子+音效+伤害），见 UrsaClawsDualWieldHandler。
 */
public class UrsaClawsItem extends SwordItem {

    public static final double AOE_RANGE = 4.0;

    public UrsaClawsItem(Tier tier, int attackDamage, float attackSpeed, Item.Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    /** 计算含锋利的有效伤害（与玩家主手攻击公式一致：基础 + 锋利加成） */
    public static float effectiveDamage(ItemStack stack) {
        float base = 0.0F;
        for (net.minecraft.world.entity.ai.attributes.AttributeModifier m :
                stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
            base += (float) m.getAmount();
        }
        int sharp = stack.getEnchantmentLevel(Enchantments.SHARPNESS);
        float extra = sharp > 0 ? (float) (1.0 + 0.5 * (sharp - 1)) : 0.0F;
        return base + extra;
    }

    /** 命中主目标后，对周围敌人追加 AOE 伤害（主手与副手斩击共用） */
    private static void aoeDamage(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        float dmg = effectiveDamage(stack);
        net.minecraft.world.phys.AABB box = target.getBoundingBox().inflate(AOE_RANGE);
        for (LivingEntity e : target.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (e == target || e == attacker) continue;
            if (!e.isAlive()) continue;
            e.hurt(attacker.damageSources().mobAttack(attacker), dmg);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!target.level().isClientSide()) {
            aoeDamage(stack, target, attacker);
        }
        return result;
    }

    /**
     * 副手完整斩击（仅服务端调用）：
     * 伤害（含锋利，受护甲减免）+ AOE + 扫击粒子 + 攻击音效 + 1 点耐久。
     * 与主手攻击效果对齐，用于双持连击的第二击。
     * 副手武器自身的附魔（火焰附加 / 击退 / 抢夺）在此按玩家攻击管线逻辑应用，
     * 使双持连击与主手一样正常受副手武器附魔影响。
     */
    public static void offhandStrike(Player player, ItemStack stack, LivingEntity target) {
        if (player.level().isClientSide()) return;

        // 主手命中会令目标进入约 10 tick 的受击无敌帧（i-frames）；副手补击在 5 tick 后触发，
        // 仍落在该窗口内会被 hurt() 直接吞掉（但挥击动画/粒子/音效不受影响）。补击前清零无敌帧，
        // 确保左右连击的副手这一下货真价实地结算伤害。
        if (target.invulnerableTime > 0) {
            target.invulnerableTime = 0;
        }

        // 主目标伤害（玩家攻击伤害源，正常受护甲/保护附魔减免）。
        // 主手挥击走玩家攻击管线天然含 +1.0 基础攻击力；副手手动结算需补上，使连击与主手伤害一致（99 + 1 = 100）。
        float dmg = effectiveDamage(stack) + 1.0F;

        // 抢夺：Vanilla 的 EnchantmentHelper.getMobLootingLevel 只读玩家主手武器。
        // 为让副手武器的抢夺等级在击杀掉落时生效，伤害结算（含同步发生的击杀掉落）期间临时把主手替换为副手武器，
        // 结束后再换回原主手。AOE 击杀一并纳入同一段作用域，保证掉落一致。
        ItemStack mainhand = player.getMainHandItem();
        boolean swapped = mainhand != stack;
        if (swapped) {
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        }
        try {
            target.hurt(player.damageSources().playerAttack(player), dmg);
            aoeDamage(stack, target, player);
        } finally {
            if (swapped) {
                player.setItemInHand(InteractionHand.MAIN_HAND, mainhand);
            }
        }

        // 火焰附加（读副手武器；与原版主手一致，仅作用于被直接斩中的主目标，不波及 AOE）
        int fireAspect = stack.getEnchantmentLevel(Enchantments.FIRE_ASPECT);
        if (fireAspect > 0 && !target.fireImmune()) {
            target.setSecondsOnFire(fireAspect * 4);
        }

        // 击退（读副手武器；方向由玩家指向目标，把目标推开，与原版一致）
        int knockback = stack.getEnchantmentLevel(Enchantments.KNOCKBACK);
        if (knockback > 0) {
            double dx = player.getX() - target.getX();
            double dz = player.getZ() - target.getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len > 0.0D) {
                target.knockback(0.4D + (double) knockback * 0.5D, dx / len, dz / len);
            }
        }

        // 耐久消耗（副手槽位破坏播报）
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.OFFHAND));

        // 扫击粒子：出现在玩家面前（与原版横扫粒子位置一致）
        if (player.level() instanceof ServerLevel serverLevel) {
            double px = player.getX() + (double) (-Mth.sin(player.getYRot() * ((float) Math.PI / 180F)));
            double pz = player.getZ() + (double) Mth.cos(player.getYRot() * ((float) Math.PI / 180F));
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    px, player.getY(0.5D), pz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        // 攻击音效（重击音，与满蓄力攻击一致）
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        SpecialTooltip.appendSpecial("ursa_claws", stack, tooltip);
    }
}
