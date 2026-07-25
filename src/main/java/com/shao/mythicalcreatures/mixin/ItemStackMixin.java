package com.shao.mythicalcreatures.mixin;

import com.shao.mythicalcreatures.config.MythicalConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 读取配置文件中的 max_damage 覆盖物品耐久上限。
 * <p>
 * priority=800（低于默认1000），让 KubeJS、Tool Stats Tweaker 等第三方模组
 * 有机会在同一个注入点覆盖我们的结果。| Lower priority allows other mods to override our values.
 * </p>
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true,
            require = 1)
    private void mythical_overrideMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        String id = BuiltInRegistries.ITEM.getKey(self.getItem()).toString();
        double override = MythicalConfig.DATA.equipAttr(id, "max_damage");
        if (override > 0) {
            cir.setReturnValue((int) override);
        }
    }
}
