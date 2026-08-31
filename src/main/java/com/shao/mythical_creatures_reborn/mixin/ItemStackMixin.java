package com.shao.mythical_creatures_reborn.mixin;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
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
 * 有机会在同一注入点覆盖本模组的注入结果。| Lower priority allows other mods to override our values.
 * </p>
 * <p>
 * getMaxDamage 是高频路径（热栏每帧、物品栏/铁砧/创造标签搜索等都会调用），
 * 故把「物品 -> 覆盖耐久」交给 {@link MaxDamageCache} 缓存，避免每帧做注册表查找 + 配置查找。
 * 缓存仅在未命中时才查一次配置；配置重载时由 {@link MaxDamageCache#clear()} 失效。
 * </p>
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true,
            require = 1)
    private void mythical_overrideMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        int override = MaxDamageCache.getOrCompute(self.getItem(),
                () -> (int) MythicalConfig.DATA.equipAttr(
                        BuiltInRegistries.ITEM.getKey(self.getItem()).toString(), "max_damage"));
        if (override > 0) {
            cir.setReturnValue(override);
        }
    }
}
