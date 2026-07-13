package org.betterx.wover.biome.api;

import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.builder.event.OnBootstrapBiomes;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.biome.impl.BiomeManagerImpl;
import org.betterx.wover.entrypoint.LibWoverBiome;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Main entry point to create and query {@link Biome}s.
 * <p>
 * Use {@link #vanilla(ResourceLocation)} to define a completely new Biome, or
 * {@link #wrapped(ResourceKey)} to attach additional {@link BiomeData} (fog density, climate parameters,
 * intended placement tag) to a Biome that already exists (for example a vanilla one). Both return a
 * {@link BiomeKey} whose {@link BiomeKey#bootstrap(org.betterx.wover.biome.api.builder.BiomeBootstrapContext)}
 * method creates the fluent {@link BiomeBuilder} that fills in the Biome's content.
 * <p>
 * Once the world is loaded, {@link #biomeData(ResourceLocation)} and {@link #biomeDataForHolder(Holder)}
 * can be used to look up the {@link BiomeData} that was registered for a Biome.
 */
public class BiomeManager {
    /**
     * Fired while the vanilla {@link Registries#BIOME} registry is bootstrapped, before any
     * {@link BiomeBuilder} was applied. Use this event if you need to register a {@link Biome} directly,
     * without going through the {@link BiomeBuilder} API.
     */
    public static final Event<OnBootstrapRegistry<Biome>> BOOTSTRAP_BIOME_REGISTRY
            = BiomeManagerImpl.BOOTSTRAP_BIOME_REGISTRY;

    /**
     * Fired whenever the internal {@link org.betterx.wover.biome.api.builder.BiomeBootstrapContext} used to
     * gather all {@link BiomeBuilder}s is (re-)created. This is used internally to collect the builders
     * created by {@link BiomeKey#bootstrap(org.betterx.wover.biome.api.builder.BiomeBootstrapContext)} and
     * feed them into the Biome, {@link BiomeData} and Biome-tag registries.
     */
    public static final Event<OnBootstrapBiomes> BOOTSTRAP_BIOMES_WITH_DATA
            = BiomeManagerImpl.BOOTSTRAP_BIOMES_WITH_DATA;

    /**
     * Creates a {@link BiomeKey} for a completely new, vanilla-style Biome.
     * <p>
     * Call {@link BiomeKey#bootstrap(org.betterx.wover.biome.api.builder.BiomeBootstrapContext)} on the
     * returned key (usually from a {@link org.betterx.wover.biome.api.builder.event.OnBootstrapBiomes}
     * subscriber or a {@link org.betterx.wover.datagen.api.provider.multi.WoverBiomeProvider}) to obtain a
     * {@link BiomeBuilder.Vanilla} that fills in the Biome's terrain, climate, effects and mob spawns.
     *
     * @param location The location of the new Biome.
     * @return The key for the new Biome.
     */
    public static BiomeKey<BiomeBuilder.Vanilla> vanilla(ResourceLocation location) {
        return BiomeManagerImpl.vanilla(location);
    }

    /**
     * Creates a {@link BiomeKey} that attaches {@link BiomeData} to an already existing Biome (for example a
     * vanilla one, or one registered by another mod), instead of defining a new one.
     * <p>
     * Call {@link BiomeKey#bootstrap(org.betterx.wover.biome.api.builder.BiomeBootstrapContext)} on the
     * returned key to obtain a {@link BiomeBuilder.Wrapped} that only lets you set the fog density, climate
     * parameters and biome tags that make up the {@link BiomeData} — the underlying Biome itself is left
     * untouched.
     *
     * @param key The key of the already existing Biome.
     * @return The key wrapping the existing Biome.
     */
    public static BiomeKey<BiomeBuilder.Wrapped> wrapped(@NotNull ResourceKey<Biome> key) {
        return BiomeManagerImpl.wrapped(key);
    }

    /**
     * Get {@link BiomeData} for biome.
     *
     * @param biome - {@link ResourceLocation} of the biome.
     * @return {@link BiomeData} or null if it was not found.
     */
    public static @Nullable BiomeData biomeData(ResourceLocation biome) {
        return biomeData(WorldState.registryAccess(), biome);
    }

    /**
     * Get {@link BiomeData} for biome.
     *
     * @param registryAccess The RegistryAccess
     * @param biome          - {@link ResourceLocation} of the biome.
     * @return {@link BiomeData} or null if it was not found.
     */
    public static BiomeData biomeData(HolderLookup.Provider registryAccess, ResourceLocation biome) {
        if (registryAccess == null) return null;

        return registryAccess
                .lookup(Registries.BIOME)
                .flatMap(r -> r.get(ResourceKey.create(Registries.BIOME, biome)))
                .map(h -> biomeDataForHolder(registryAccess, h))
                .orElse(null);
    }

    /**
     * Get {@link BiomeData} for biome. This Method will use the {@link WorldState} to get the
     * {@link BiomeDataRegistry} and then get the {@link BiomeData} for the given biome.
     *
     * @param biome - {@link Holder<Biome>} from.
     * @return {@link BiomeData} or null if it was not found.
     */
    public static @Nullable BiomeData biomeDataForHolder(Holder<Biome> biome) {
        return biomeDataForHolder(WorldState.registryAccess(), biome);
    }

    /**
     * Get {@link BiomeData} for biome. This Method will use the {@link RegistryAccess} to get the
     * {@link BiomeDataRegistry} and then get the {@link BiomeData} for the given biome.
     *
     * @param acc   The RegistryAccess
     * @param biome - {@link Holder<Biome>} from.
     * @return {@link BiomeData} or null if it was not found.
     */
    public static @Nullable BiomeData biomeDataForHolder(HolderLookup.Provider acc, Holder<Biome> biome) {
        if (acc != null) {
            final HolderLookup.RegistryLookup<BiomeData> reg = acc.lookupOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);
            ResourceLocation id = biome.unwrapKey().map(ResourceKey::location).orElse(null);
            if (id != null) {
                return reg.get(BiomeDataRegistry.createKey(id)).map(Holder.Reference::value).orElse(null);
            }
        }
        return null;
    }

    /**
     * Set biome in chunk at specified position.
     *
     * @param chunk {@link ChunkAccess} chunk to set biome in.
     * @param pos   {@link BlockPos} biome position.
     * @param biome {@link Holder<Biome>} instance. Should be biome from world.
     */
    public static void setBiome(ChunkAccess chunk, BlockPos pos, Holder<Biome> biome) {
        final int sectionY = (pos.getY() - chunk.getMinY()) >> 4;
        final PalettedContainerRO<Holder<Biome>> biomes = chunk.getSection(sectionY).getBiomes();
        if (biomes instanceof PalettedContainer<Holder<Biome>> palette) {
            palette.set((pos.getX() & 15) >> 2, (pos.getY() & 15) >> 2, (pos.getZ() & 15) >> 2, biome);
        } else {
            LibWoverBiome.C.LOG.warn("Unable to change Biome at " + pos);
        }
    }

    /**
     * Set biome in world at specified position.
     *
     * @param level {@link LevelAccessor} world to set biome in.
     * @param pos   {@link BlockPos} biome position.
     * @param biome {@link Holder<Biome>} instance. Should be biome from world.
     */
    public static void setBiome(LevelAccessor level, BlockPos pos, Holder<Biome> biome) {
        final ChunkAccess chunk = level.getChunk(pos);
        setBiome(chunk, pos, biome);
    }
}
