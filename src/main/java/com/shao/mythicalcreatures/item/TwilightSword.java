package com.shao.mythicalcreatures.item;

import com.shao.mythicalcreatures.entity.ModEntities;
import com.shao.mythicalcreatures.entity.custom.TwilightMagicEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class TwilightSword extends SwordItem {

    public TwilightSword(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isShiftKeyDown()) {
            // 右键 → 在玩家前方召唤紫悦的魔法
            Vec3 spawn = player.position().add(player.getLookAngle().scale(1.5)).add(0, 1.0, 0);
            summonMagic(level, player, hand, stack, spawn);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        // 潜行 + 右键 → 射线检测前方实体，削减当前血量 1/6，可致死
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 far = eye.add(look.scale(5.0));
        AABB scanArea = new AABB(eye, far).inflate(1.0);

        LivingEntity target = null;
        double closest = Double.MAX_VALUE;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, scanArea,
                e -> e != player && e.isAlive())) {
            AABB box = entity.getBoundingBox().inflate(0.2);
            Optional<Vec3> hit = box.clip(eye, far);
            if (hit.isPresent()) {
                double dist = eye.distanceToSqr(entity.position());
                if (dist < closest) {
                    closest = dist;
                    target = entity;
                }
            }
        }

        if (target != null) {
            if (!level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) level;

                // 削减最大血量 1/6（四舍五入取整），可击杀
                float reduce = Math.round(target.getMaxHealth() / 6.0F);
                target.setHealth(target.getHealth() - reduce);
                target.invulnerableTime = 0;

                // 消耗五分之一耐久
                int fifthDurability = stack.getMaxDamage() / 5;
                stack.hurtAndBreak(fifthDurability, player,
                        p -> p.broadcastBreakEvent(hand));

                // 紫色粒子
                Vec3 pos = target.position();
                for (int i = 0; i < 30; i++) {
                    double dx = (player.getRandom().nextDouble() - 0.5) * 2.0;
                    double dy = player.getRandom().nextDouble() * 2.0;
                    double dz = (player.getRandom().nextDouble() - 0.5) * 2.0;
                    serverLevel.sendParticles(ParticleTypes.PORTAL,
                            pos.x + dx, pos.y + dy, pos.z + dz,
                            1, dx * 0.05, dy * 0.05, dz * 0.05, 0.05);
                }
                for (int i = 0; i < 20; i++) {
                    double dx = (player.getRandom().nextDouble() - 0.5) * 2.5;
                    double dy = player.getRandom().nextDouble() * 2.0;
                    double dz = (player.getRandom().nextDouble() - 0.5) * 2.5;
                    serverLevel.sendParticles(ParticleTypes.WITCH,
                            pos.x + dx, pos.y + dy, pos.z + dz,
                            1, dx * 0.03, dy * 0.03, dz * 0.03, 0.03);
                }

                // 悦灵音效
                serverLevel.playSound(null, pos.x, pos.y, pos.z,
                        SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.PLAYERS, 0.6F, 1.2F);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(context.getHand());

        if (!player.isShiftKeyDown()) {
            // 对方块右键 → 在方块位置召唤紫悦的魔法
            Vec3 spawn = context.getClickLocation();
            summonMagic(context.getLevel(), player, context.getHand(), stack, spawn);
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
        }

        // 潜行时对方块右键走原来的 use 逻辑
        return super.useOn(context);
    }

    private void summonMagic(Level level, Player player, InteractionHand hand, ItemStack stack, Vec3 pos) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 0.6F, 1.5F);

        if (!level.isClientSide) {
            TwilightMagicEntity magic = new TwilightMagicEntity(ModEntities.TWILIGHT_MAGIC.get(), level);
            magic.setPos(pos.x, pos.y, pos.z);
            magic.setOwner(player);
            level.addFreshEntity(magic);

            stack.hurtAndBreak(10, player,
                    p -> p.broadcastBreakEvent(hand));
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, 40);
    }
}
