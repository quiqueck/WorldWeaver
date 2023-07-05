package org.betterx.wover.datagen.api;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

public abstract class WoverRegistryProvider<T> {
    public final String title;

    public final ResourceKey<Registry<T>> registryKey;
    protected final ModCore modCore;

    public WoverRegistryProvider(
            ModCore modCore,
            String title,
            ResourceKey<Registry<T>> registryKey
    ) {
        this.modCore = modCore;
        this.title = title;
        this.registryKey = registryKey;
    }

    protected abstract void bootstrap(BootstapContext<T> context);
    protected abstract FabricDynamicRegistryProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    );

    public abstract void buildRegistry(RegistrySetBuilder registryBuilder);

    public boolean shouldAddToPack(@Nullable ResourceLocation pack) {
        return pack == null;
    }
}
