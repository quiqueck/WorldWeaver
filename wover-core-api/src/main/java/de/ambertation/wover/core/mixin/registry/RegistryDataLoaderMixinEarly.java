package de.ambertation.wover.core.mixin.registry;

import de.ambertation.wover.core.impl.registry.DatapackRegistryBuilderImpl;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Registries.class, priority = 200)
public class RegistryDataLoaderMixinEarly {
    // Fabric (0.68) force-changes the directory path for all modded registries to be prefixed with
    // the mod id. We do not want this for Registries managed using our DatapackRegistryBuilder,
    // but instead force the vanilla behavior.
    @Inject(method = "elementsDirPath", at = @At("RETURN"), cancellable = true)
    private static void prependDirectoryWithNamespace(
            ResourceKey<? extends Registry<?>> resourceKey,
            CallbackInfoReturnable<String> info
    ) {
        if (DatapackRegistryBuilderImpl.isRegistered(resourceKey.location())) {
            info.setReturnValue(info.getReturnValue());
        }
    }
}
