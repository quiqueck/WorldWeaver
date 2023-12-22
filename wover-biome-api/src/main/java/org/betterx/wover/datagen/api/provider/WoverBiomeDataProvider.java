package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link WoverRegistryContentProvider} for {@link BiomeData}.
 */
public abstract class WoverBiomeDataProvider extends WoverRegistryContentProvider<BiomeData> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverBiomeDataProvider(
            @NotNull ModCore modCore
    ) {
        this(modCore, modCore.id("default"));
    }

    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore    The ModCore instance of the Mod that is providing this instance.
     * @param providerId The id of the provider. Every Provider (for the same Registry)
     *                   needs a unique id.
     */
    public WoverBiomeDataProvider(
            @NotNull ModCore modCore,
            @NotNull ResourceLocation providerId
    ) {
        super(modCore, providerId.toString() + " (Biome Data)", BiomeDataRegistry.BIOME_DATA_REGISTRY);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    @Override
    protected abstract void bootstrap(BootstapContext<BiomeData> context);
}



