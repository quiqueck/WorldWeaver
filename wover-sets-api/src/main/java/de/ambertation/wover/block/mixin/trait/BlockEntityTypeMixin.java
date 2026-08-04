package de.ambertation.wover.block.mixin.trait;

import de.ambertation.wover.block.impl.trait.BlockEntityTypeAccessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin implements BlockEntityTypeAccessor {
    @Unique
    private Set<Block> wover_validSet;

    public Set<Block> wover_getValidSet() {
        return wover_validSet;
    }

    public void wover_setValidSet(Set<Block> validSet) {
        this.wover_validSet = validSet;
    }

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    void wover_isValid(
            BlockState blockState, CallbackInfoReturnable<Boolean> cir
    ) {
        if (wover_validSet != null && wover_validSet.contains(blockState.getBlock())) {
            cir.setReturnValue(true);
        }
    }
}
