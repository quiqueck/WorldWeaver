package de.ambertation.wover.item.mixin.item_stack_setup;

import de.ambertation.wover.item.api.ItemStackHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ShapedRecipe.class, ShapelessRecipe.class, RepairItemRecipe.class})
public class CraftingRecipeMixin {
    @Inject(
            method = "assemble(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN"), cancellable = false)
    public <R extends RecipeInput> void wover_setupItemStack(
            RecipeInput recipeInput, HolderLookup.Provider provider, CallbackInfoReturnable<ItemStack> cir
    ) {
        ItemStackHelper.callItemStackSetupIfPossible(cir.getReturnValue());
    }
}
