package org.betterx.wover.biome.api.modification;

import org.betterx.wover.biome.impl.api.modification.BiomeModificationRegistryImpl;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverBiome;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BiomeModificationRegistry {
    public static final Event<OnBootstrapRegistry<BiomeModification>> BOOTSTRAP_BIOME_MODIFICATION_REGISTRY
            = BiomeModificationRegistryImpl.BOOTSTRAP_BIOME_MODIFICATION_REGISTRY;

    public static final ResourceKey<Registry<BiomeModification>> BIOME_MODIFICATION_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(WoverBiome.C.id("wover/worldgen/biome_modifications"));

    public static ResourceKey<BiomeModification> createKey(
            ResourceLocation modificationID
    ) {
        return BiomeModificationRegistryImpl.createKey(modificationID);
    }
}
