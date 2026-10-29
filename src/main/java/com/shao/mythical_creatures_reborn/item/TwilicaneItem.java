package com.shao.mythical_creatures_reborn.item;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TwilicaneItem extends Item {

    private static final Random RANDOM = new Random();

    public TwilicaneItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            List<? extends String> configList = MythicalConfig.DATA.twilicaneSpawnList();
            if (configList.isEmpty()) return InteractionResultHolder.fail(stack);

            // 从配置项解析全部合法实体类型
            List<EntityType<?>> types = new ArrayList<>();
            for (String id : configList) {
                ResourceLocation rl = ResourceLocation.tryParse(id);
                if (rl == null) continue;
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(rl);
                if (type != null) types.add(type);
            }
            if (types.isEmpty()) return InteractionResultHolder.fail(stack);

            EntityType<?> chosen = types.get(RANDOM.nextInt(types.size()));
            ServerLevel serverLevel = (ServerLevel) level;

            Mob mob = (Mob) chosen.create(serverLevel);
            if (mob != null) {
                mob.setPos(player.getX(), player.getY(), player.getZ());
                mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(mob.blockPosition()),
                        MobSpawnType.SPAWN_EGG, null, null);
                serverLevel.addFreshEntity(mob);

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        SpecialTooltip.appendSpecial("twilicane", stack, tooltip);
    }
}
