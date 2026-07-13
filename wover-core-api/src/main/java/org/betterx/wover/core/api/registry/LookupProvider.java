package org.betterx.wover.core.api.registry;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implemented by objects that can resolve a {@link HolderGetter} for another registry, so elements of that
 * registry can be looked up by their {@link ResourceKey} while bootstrapping. {@link CustomBootstrapContext}
 * implements this interface using its current {@link net.minecraft.data.worldgen.BootstrapContext}.
 */
public interface LookupProvider {
    /**
     * Returns a {@link HolderGetter} that can be used to resolve elements of the given registry.
     *
     * @param registryKey The key of the registry to look up.
     * @param <S>         The type of the elements stored in the registry.
     * @return The {@link HolderGetter} for the given registry, or {@code null} if it could not be resolved.
     */
    <S> @Nullable HolderGetter<S> lookup(@NotNull ResourceKey<? extends Registry<? extends S>> registryKey);
}