package org.betterx.wover.core.mixin.registry;

import org.betterx.wover.core.impl.registry.DatapackRegistryBuilderImpl;

import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RegistryDataLoader.class, priority = 200)
public class RegistryDataLoaderMixinEarly {
    // Fabric (0.68) force-changes the directory path for all modded registries to be prefixed with
    // the mod id. We do not want this for Registries managed using our DatapackRegistryBuilder,
    // but instead force the vanilla behavior.
    @Inject(method = "registryDirPath", at = @At("RETURN"), cancellable = true)
    private static void prependDirectoryWithNamespace(ResourceLocation id, CallbackInfoReturnable<String> info) {
        if (DatapackRegistryBuilderImpl.isRegistered(id)) {
            info.setReturnValue(info.getReturnValue());
        }
    }
}
