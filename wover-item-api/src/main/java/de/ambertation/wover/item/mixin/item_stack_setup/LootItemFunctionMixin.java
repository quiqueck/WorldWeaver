package de.ambertation.wover.item.mixin.item_stack_setup;

import de.ambertation.wover.item.api.ItemStackHelper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LootItemFunction.class)
public interface LootItemFunctionMixin {
    @ModifyArg(
            method = "method_514",
            at = @At(value = "INVOKE", target = "Ljava/util/function/BiFunction;apply(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
            index = 0
    )
    private static Object wover_decorate(
            Object itemStack
    ) {
        return ItemStackHelper.callItemStackSetupIfPossible((ItemStack) itemStack);
    }
}
