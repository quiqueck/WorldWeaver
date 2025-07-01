package org.betterx.wover.block.mixin.trait;

import org.betterx.wover.block.api.trait.BlockTraits;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {
    @Inject(method = "getStripped", at = @At("HEAD"), cancellable = true)
    void wover_getStripped(BlockState blockState, CallbackInfoReturnable<Optional<BlockState>> cir) {
        var strippedState = BlockTraits.STRIPABLE.getStrippedBlockState(blockState);
        if (strippedState != blockState) cir.setReturnValue(Optional.of(strippedState));
    }
}