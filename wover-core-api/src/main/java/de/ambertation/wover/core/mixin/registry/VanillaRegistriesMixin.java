package de.ambertation.wover.core.mixin.registry;

import de.ambertation.wover.core.impl.registry.DatapackRegistryBuilderImpl;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.SingleRegistryBootstrap;
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
    // Minecraft 26.3 split VanillaRegistries' single BUILDER into two RegistrySetBuilders:
    // WORLD_BUILDER (the former BUILDER: everything that is loaded once per world) and
    // RELOADABLE_BUILDER (the registries that became reloadable datapack registries in 26.3,
    // e.g. recipes and loot tables). Our custom registries are world registries - see
    // RegistryDataLoaderMixin, which appends them to RegistryDataLoader.WORLD_REGISTRIES - so
    // WORLD_BUILDER is the correct counterpart of the old BUILDER here.
    @Shadow
    @Final
    private static RegistrySetBuilder WORLD_BUILDER;

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void together_registerSurface(CallbackInfo ci) {
        DatapackRegistryBuilderImpl.bootstrap((key, bootstrap) -> {
            // 26.3: RegistrySetBuilder.RegistryBootstrap<T> moved out and became
            // net.minecraft.core.registries.SingleRegistryBootstrap<T>.
            WORLD_BUILDER.add((ResourceKey) key, (SingleRegistryBootstrap<? extends Object>) bootstrap);
        });

    }
}
