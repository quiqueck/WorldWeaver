package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link WoverRegistryContentProvider} for {@link Biome}s. This Provider
 * will only supply Biomes for the Biome Registry. If you want to supply
 * all Biome related things, use {@code WoverBiomeProvider} from the wover-biome-api.
 */
public abstract class WoverBiomeOnlyProvider extends WoverRegistryContentProvider<Biome> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverBiomeOnlyProvider(
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
    public WoverBiomeOnlyProvider(
            @NotNull ModCore modCore,
            @NotNull ResourceLocation providerId
    ) {
        super(modCore, providerId.toString() + " (Biome)", Registries.BIOME);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    @Override
    protected abstract void bootstrap(BootstapContext<Biome> context);
}
