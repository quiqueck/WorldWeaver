package de.ambertation.wover.item.mixin.item_stack_setup;

import de.ambertation.wover.item.api.ItemStackHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTransformRecipe.class)
public class SmithingTransformRecipeMixin {
    @Inject(
            method = "assemble(Lnet/minecraft/world/item/crafting/SmithingRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN"), cancellable = false)
    public void wover_setupItemStack(
            SmithingRecipeInput smithingRecipeInput, HolderLookup.Provider provider,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        ItemStackHelper.callItemStackSetupIfPossible(cir.getReturnValue(), provider);
    }
}
