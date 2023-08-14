package org.betterx.wover.datagen.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.concurrent.CompletableFuture;

/**
 * A factory for a {@link DataProvider}.
 *
 * @param <T> The type of the {@link DataProvider}
 */
public interface WoverDataProvider<T extends DataProvider> {
    /**
     * Called, when The Data needs to be serialized.
     *
     * @param output           The output to write the data to.
     * @param registriesFuture A future sent from the Fabric DataGen API
     * @return A new {@link DataProvider}
     */
    T getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    );

    /**
     * Allows a {@link WoverDataProvider} to create a secondary {@link DataProvider}.
     *
     * @param <T> The type of the secondary {@link DataProvider}
     */
    interface Secondary<T extends DataProvider> {
        /**
         * Called, when The Data needs to be serialized.
         *
         * @param output           The output to write the data to.
         * @param registriesFuture A future sent from the Fabric DataGen API
         * @return A new {@link DataProvider}
         */
        T getSecondaryProvider(
                FabricDataOutput output,
                CompletableFuture<HolderLookup.Provider> registriesFuture
        );
    }

    /**
     * Allows a {@link WoverDataProvider} to create a tertiary {@link DataProvider}.
     *
     * @param <T> The type of the tertiary {@link DataProvider}
     */
    interface Tertiary<T extends DataProvider> {
        /**
         * Called, when The Data needs to be serialized.
         *
         * @param output           The output to write the data to.
         * @param registriesFuture A future sent from the Fabric DataGen API
         * @return A new {@link DataProvider}
         */
        T getTertiaryProvider(
                FabricDataOutput output,
                CompletableFuture<HolderLookup.Provider> registriesFuture
        );
    }
}
