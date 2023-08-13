package org.betterx.wover.biome.api;

import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.builder.event.OnBootstrapBiomes;
import org.betterx.wover.biome.impl.BiomeManagerImpl;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class BiomeManager {
    public static final Event<OnBootstrapRegistry<Biome>> BOOTSTRAP_BIOME_REGISTRY
            = BiomeManagerImpl.BOOTSTRAP_BIOME_REGISTRY;
    public static final Event<OnBootstrapBiomes> BOOTSTRAP_BIOMES_WITH_DATA
            = BiomeManagerImpl.BOOTSTRAP_BIOMES_WITH_DATA;

    public static BiomeKey<BiomeBuilder.Vanilla> vanilla(ResourceLocation location) {
        return BiomeManagerImpl.vanilla(location);
    }
}
