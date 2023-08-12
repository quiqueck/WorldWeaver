package org.betterx.wover.datagen.api;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Handles the boostrapping as well as the serialization of a {@link Registry} to
 * a DataPack.
 *
 * @param <T> The element type of the registry.
 */
public abstract class WoverRegistryProvider<T> implements WoverDataProvider<FabricDynamicRegistryProvider> {
    /**
     * The title of the provider. Mainly used for logging.
     */
    public final String title;

    /**
     * The Key to the Registry.
     */
    public final ResourceKey<Registry<T>> registryKey;

    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    protected final ModCore modCore;

    /**
     * Creates a new instance of {@link WoverRegistryProvider}.
     *
     * @param modCore     The ModCore instance of the Mod that is providing this instance.
     * @param title       The title of the provider. Mainly used for logging.
     * @param registryKey The Key to the Registry.
     */
    protected WoverRegistryProvider(
            ModCore modCore,
            String title,
            ResourceKey<Registry<T>> registryKey
    ) {
        this.modCore = modCore;
        this.title = title;
        this.registryKey = registryKey;
    }

    /**
     * Called, when the Elements of the Registry need to be created and added.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrap(BootstapContext<T> context);

    /**
     * Called, when the Registry needs to be serialized. The returned provider
     * basically lists all elements from the registry that need to be serialized.
     *
     * @param output           The output to write the data to.
     * @param registriesFuture A future sent from the Fabric DataGen API
     * @return A new {@link FabricDynamicRegistryProvider} that lists all elements
     */
    public abstract FabricDynamicRegistryProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    );

    /**
     * Called, when the Registry needs to be added to the {@link RegistrySetBuilder}.
     *
     * @param registryBuilder The builder to add the registry to.
     */
    public abstract void buildRegistry(RegistrySetBuilder registryBuilder);
}
