package de.ambertation.wover.generator.api.biomesource;

import de.ambertation.wover.generator.impl.biomesource.BiomeSourceManagerImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import java.util.Set;

/**
 * Registers custom {@link BiomeSource} types and reads the biome exclusion config used by WoVer's own
 * {@link BiomeSource} implementations.
 * <p>
 * Registering a new {@link BiomeSource} type here is only necessary if you are implementing your own
 * {@link BiomeSource} (comparable to {@link de.ambertation.wover.generator.api.biomesource.end.WoverEndConfig
 * WoverEndConfig}/{@link de.ambertation.wover.generator.api.biomesource.nether.WoverNetherConfig
 * WoverNetherConfig}). Most mod developers instead reuse the {@code wover:nether_biome_source}/
 * {@code wover:end_biome_source} types that WoVer already registers.
 */
public class BiomeSourceManager {
    /**
     * Registers a new {@link BiomeSource} type under the given id.
     * <p>
     * This is the same mechanism vanilla uses to register {@link BiomeSource} codecs
     * ({@code minecraft:biome_source}, e.g. {@code multi_noise} or {@code fixed}) — it makes {@code type: <location>}
     * a valid value for the {@code biome_source} field of a {@code chunk_generator} in
     * {@code data/<namespace>/dimension/*.json}.
     *
     * @param location The id the {@link BiomeSource} type is registered under.
     * @param codec    The {@link MapCodec} used to (de)serialize the {@link BiomeSource}.
     */
    public static void register(ResourceLocation location, MapCodec<BiomeSource> codec) {
        BiomeSourceManagerImpl.register(location, codec);
    }

    /**
     * Returns a set of biomes that are excluded for the given tag.
     * <p>
     * The list of excluded biomes is read from the {@code data/<modID>/config/biome_config.json} file. The
     * following content would exclude the crimson forest from the nether:
     * <pre class="json"> {
     *   "exclude": {
     *     "minecraft:is_nether": ["minecraft:crimson_forest"]
     *   }
     * }</pre>
     * <p>
     * The keys in the exclude listing are the IDs of valid Biome-Tags. The value is
     * an array of biome IDs that should be excluded from the biome source.
     * <p>
     * Wover Biome Sources include Biomes into a dimension using the assigned Biome-Tags.
     * {@link de.ambertation.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource} for example
     * includes all biomes that have the {@code minecraft:is_nether} tag. The
     * {@link de.ambertation.wover.generator.impl.biomesource.end.WoverEndBiomeSource} includes all biomes
     * that have the {@code c:is_end_center}, {@code c:is_end_highland}, {@code c:is_end_midland}, {@code c:is_end_barrens},
     * {@code c:is_small_end_island} or {@code minecraft:is_end} tag. Excluding a biome from one of the end tags
     * might still include it in the end if it has additional End-Tags (all End Biomes are at least in the
     * {@code minecraft:is_end} Tag). In order to ensure, that a biome is not spawned in the end, you can
     * use the special key {@code *:is_end} in the json file to exclude biomes from all end tags:
     * <pre class="json"> {
     *   "exclude": {
     *     "*:is_end": ["minecraft:void"]
     *   }
     * }</pre>
     *
     * @param tag The tag to get the excluded biomes for.
     * @return A set of biomes that are excluded for the given tag.
     */
    public static Set<ResourceLocation> getExcludedBiomes(TagKey<Biome> tag) {
        return BiomeSourceManagerImpl.getExcludedBiomes(tag);
    }
}
