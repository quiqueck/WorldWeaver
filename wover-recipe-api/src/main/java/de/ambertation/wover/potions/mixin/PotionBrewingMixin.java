package de.ambertation.wover.potions.mixin;

import de.ambertation.wover.potions.impl.PotionManagerImpl;

import net.minecraft.world.item.alchemy.PotionBrewing;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {
    @WrapOperation(method = "bootstrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionBrewing$Builder;build()Lnet/minecraft/world/item/alchemy/PotionBrewing;"))
    private static PotionBrewing wover_bootstrapPotions(
            PotionBrewing.Builder instance, Operation<PotionBrewing> original
    ) {
        PotionManagerImpl.BOOTSTRAP_POTIONS.emit(c -> c.bootstrap(instance));
        return original.call(instance);
    }
}
