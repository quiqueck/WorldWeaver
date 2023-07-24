package org.betterx.wover.core.mixin.registry;

import org.betterx.wover.core.impl.registry.DatapackRegistryBuilderImpl;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VanillaRegistries.class)
public class VanillaRegistriesMixin {
    @Shadow
    @Final
    private static RegistrySetBuilder BUILDER;

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void together_registerSurface(CallbackInfo ci) {
        DatapackRegistryBuilderImpl.bootstrap((key, bootstrap) -> {
            BUILDER.add((ResourceKey) key, (RegistrySetBuilder.RegistryBootstrap<? extends Object>) bootstrap);
        });

    }
}
