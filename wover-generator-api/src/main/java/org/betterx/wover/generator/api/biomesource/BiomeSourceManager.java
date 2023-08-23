package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.generator.impl.biomesource.BiomeSourceManagerImpl;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import java.util.Set;

public class BiomeSourceManager {
    public static void register(ResourceLocation location, Codec<BiomeSource> codec) {
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
     * {@link org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource} for example
     * includes all biomes that have the {@code minecraft:is_nether} tag. The
     * {@link org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource} includes all biomes
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
