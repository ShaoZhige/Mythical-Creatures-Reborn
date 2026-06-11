package com.shao.mythicalcreatures.event;

import com.shao.mythicalcreatures.MythicalCreaturesMod;
import com.shao.mythicalcreatures.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 珍贵宝石在铁砧中修复任意有耐久物品 25%。
 */
@Mod.EventBusSubscriber(modid = MythicalCreaturesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilRepairHandler {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        // 右键必须是珍贵宝石
        if (!right.is(ModItems.PRECIOUS_GEM.get())) return;
        // 左键必须有耐久且已损坏
        if (!left.isDamageableItem() || !left.isDamaged()) return;

        ItemStack result = left.copy();
        int max = result.getMaxDamage();
        int repair = max / 4;
        int newDamage = Math.max(0, result.getDamageValue() - repair);
        result.setDamageValue(newDamage);

        // 铁砧经验花费：1 级
        event.setCost(1);
        event.setMaterialCost(1);
        event.setOutput(result);
    }
}
