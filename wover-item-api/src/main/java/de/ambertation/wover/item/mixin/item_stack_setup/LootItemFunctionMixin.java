package de.ambertation.wover.item.mixin.item_stack_setup;

import de.ambertation.wover.item.api.ItemStackHelper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LootItemFunction.class)
public interface LootItemFunctionMixin {
    // 26.3 changed decorate(...)'s first parameter from a raw BiFunction<ItemStack, LootContext, ItemStack>
    // to Optional<Holder<LootItemFunction>>; lambda$decorate$0 now unwraps the Holder and checkcasts it to
    // LootItemFunction before calling apply(), so javac emits the invokeinterface against LootItemFunction
    // itself rather than BiFunction (confirmed via javap - constant pool owner for the apply() call is
    // net/minecraft/world/level/storage/loot/functions/LootItemFunction in 26.3, java/util/function/BiFunction
    // in 26.1). The call arguments (itemStack, lootContext) and their order are unchanged, so index 0 still
    // targets the item stack.
    @ModifyArg(
            method = "lambda$decorate$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction;apply(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            index = 0
    )
    private static Object wover_decorate(
            Object itemStack
    ) {
        return ItemStackHelper.callItemStackSetupIfPossible((ItemStack) itemStack);
    }
}
