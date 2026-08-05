package de.ambertation.wover.item.mixin.item_stack_setup;

import de.ambertation.wover.item.api.ItemStackHelper;

import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInput.class)
public class ItemInputMixin {
    @Shadow
    @Final
    private Holder<Item> item;

    @WrapOperation(
            method = "createItemStack",
            at = @At(value = "NEW", target = "net/minecraft/world/item/ItemStack")
    )
    public ItemStack wover_init(
            Holder<Item> item, int count, DataComponentPatch components, Operation<ItemStack> original
    ) {
        return ItemStackHelper.callItemStackSetupIfPossible(original.call(item, count, components));
    }
}
