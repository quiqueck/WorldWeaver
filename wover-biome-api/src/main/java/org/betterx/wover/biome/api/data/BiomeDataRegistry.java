package org.betterx.wover.biome.api.data;

import org.betterx.wover.biome.impl.data.BiomeDataRegistryImpl;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverSurface;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class BiomeDataRegistry {
    private BiomeDataRegistry() {

    }

    /**
     * This event is fired, after the surface rule registry was loaded. At this point, the
     * registry has gathered all surface rules from datapacks and is not yet frozen
     */
    public static final Event<OnBootstrapRegistry<BiomeData>> BOOTSTRAP_BIOME_DATA_REGISTRY
            = BiomeDataRegistryImpl.BOOTSTRAP_BIOME_DATA_REGISTRY;

    /**
     * The Key of the Registry. ({@code wover/worldgen/biome_data})
     */
    public static final ResourceKey<Registry<BiomeData>> BIOME_DATA_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(WoverSurface.C.id("wover/worldgen/biome_data"));

    /**
     * Creates a ResourceKey for  {@link BiomeData}.
     *
     * @param dataID The ID of the BiomeData
     * @return The ResourceKey
     */
    public static ResourceKey<BiomeData> createKey(
            ResourceLocation dataID
    ) {
        return BiomeDataRegistryImpl.createKey(dataID);
    }

    /**
     * Creates a ResourceKey for  {@link BiomeData}.
     *
     * @param biomeKey A Biome Key
     * @return The ResourceKey
     */
    public static ResourceKey<BiomeData> createKey(
            ResourceKey<Biome> biomeKey
    ) {
        return BiomeDataRegistryImpl.createKey(biomeKey.location());
    }
}
