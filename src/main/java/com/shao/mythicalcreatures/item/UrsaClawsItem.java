package com.shao.mythicalcreatures.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

/**
 * 大熊座之爪（Ursa Claws）
 * 单次挥击 100 点伤害（基础值由 Tier 攻击加成 + 构造函数参数决定），
 * 走 SwordItem 默认逻辑 → 可正常附魔「锋利」且受护甲减免；
 * 攻速修正 -3.3F → 实际 0.7 次/秒，比最慢的斧头(0.8)还慢。
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
     */
    public static void offhandStrike(Player player, ItemStack stack, LivingEntity target) {
        if (player.level().isClientSide()) return;

        // 主目标伤害（玩家攻击伤害源，正常受护甲/保护附魔减免）
        float dmg = effectiveDamage(stack);
        target.hurt(player.damageSources().playerAttack(player), dmg);

        // AOE（与主手一致）
        aoeDamage(stack, target, player);

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
}
