package org.betterx.wover.datagen.api;

import net.minecraft.data.DataProvider;

import org.jetbrains.annotations.Nullable;

/**
 * Marker interface for auto providers that are added to all global Datapacks. You can register an
 * auto provider by calling {@link WoverDataGenEntryPoint#registerAutoProvider(PackBuilder.ProviderFactory)}.
 * <p>
 * When a new DataPack is created, the system will verify that the registered provider actually implements
 * this interface or throw an IllegalArgumentException.
 */
public interface WoverAutoProvider {
    /**
     * A {@link WoverAutoProvider} that can intercept (and potentially replace or suppress)
     * other {@link WoverDataProvider}s before they are added to a Datapack.
     * <p>
     * Instances of this interface are applied, in registration order, to every provider
     * that is added to a Datapack. This allows a mod to, for example, wrap, filter or
     * completely replace providers registered by other mods.
     */
    interface WithRedirect extends WoverAutoProvider {
        /**
         * Called for every {@link WoverDataProvider} that is added to a Datapack.
         *
         * @param provider The {@link WoverDataProvider} that was about to be added, or the
         *                 result of a previous redirector, or {@code null} if a previous
         *                 redirector already suppressed the provider.
         * @param <T>      The type of the {@link DataProvider}.
         * @return The (possibly wrapped or replaced) {@link WoverDataProvider} that should be
         * added instead, or {@code null} to prevent the provider from being added at all.
         */
        <T extends DataProvider> @Nullable WoverDataProvider<T> redirect(@Nullable WoverDataProvider<T> provider);
    }
}
