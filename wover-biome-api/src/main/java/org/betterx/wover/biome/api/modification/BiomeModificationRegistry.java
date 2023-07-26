package org.betterx.wover.biome.api.modification;

import org.betterx.wover.biome.impl.modification.BiomeModificationRegistryImpl;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverBiome;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Provides a Datapack backed registry for Biome Modifications.
 */
public class BiomeModificationRegistry {
    /**
     * Event that is fired when the Biome Modification Registry is being loaded/initialized.
     * <p>
     * This event can be used to create Modifications at Runtime. However, it is recommended to use
     * a DataGenerator to create Modifications whenever possible.
     */
    public static final Event<OnBootstrapRegistry<BiomeModification>> BOOTSTRAP_BIOME_MODIFICATION_REGISTRY
            = BiomeModificationRegistryImpl.BOOTSTRAP_BIOME_MODIFICATION_REGISTRY;

    /**
     * The key for the Biome Modification Registry.
     */
    public static final ResourceKey<Registry<BiomeModification>> BIOME_MODIFICATION_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(WoverBiome.C.id("wover/worldgen/biome_modifications"));

    /**
     * Creates a  Key for a Biome Modification.
     *
     * @param modificationID The ID of the Modification.
     * @return The Key for the Modification in this Registry
     */
    public static ResourceKey<BiomeModification> createKey(
            ResourceLocation modificationID
    ) {
        return BiomeModificationRegistryImpl.createKey(modificationID);
    }

    private BiomeModificationRegistry() {
    }
}
