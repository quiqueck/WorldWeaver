package org.betterx.wover.generator.impl.biomesource.end;

import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.ApiStatus;

/**
 * Helper class until FAPI integrates <a href="https://github.com/FabricMC/fabric/pull/2369">this PR</a>
 */
public class TheEndBiomesHelper {
    @ApiStatus.Internal
    private static Map<TagKey, Set<ResourceKey<Biome>>> END_BIOMES = new HashMap<>();

    @ApiStatus.Internal
    public static void add(TagKey<Biome> type, ResourceKey<Biome> biome) {
        if (biome == null) return;
        END_BIOMES.computeIfAbsent(type, t -> new HashSet<>()).add(biome);
    }

    private static boolean has(TagKey<Biome> type, ResourceKey<Biome> biome) {
        if (biome == null) return false;
        Set<ResourceKey<Biome>> set = END_BIOMES.get(type);
        if (set == null) return false;
        return set.contains(biome);
    }

    /**
     * Returns true if the given biome was added as a main end Biome in the end, considering the Vanilla end biomes,
     * and any biomes added to the End by mods.
     *
     * @param biome The biome to search for
     */
    public static boolean canGenerateAsMainIslandBiome(ResourceKey<Biome> biome) {
        return has(CommonBiomeTags.IS_END_CENTER, biome);
    }

    /**
     * Returns true if the given biome was added as a small end islands Biome in the end, considering the Vanilla end biomes,
     * and any biomes added to the End by mods.
     *
     * @param biome The biome to search for
     */
    public static boolean canGenerateAsSmallIslandsBiome(ResourceKey<Biome> biome) {
        return has(CommonBiomeTags.IS_SMALL_END_ISLAND, biome);
    }

    /**
     * Returns true if the given biome was added as a Highland Biome in the end, considering the Vanilla end biomes,
     * and any biomes added to the End by mods.
     *
     * @param biome The biome to search for
     */
    public static boolean canGenerateAsHighlandsBiome(ResourceKey<Biome> biome) {
        return has(CommonBiomeTags.IS_END_HIGHLAND, biome);
    }

    /**
     * Returns true if the given biome was added as midland biome in the end, considering the Vanilla end biomes,
     * and any biomes added to the End as midland biome by mods.
     *
     * @param biome The biome to search for
     */
    public static boolean canGenerateAsEndMidlands(ResourceKey<Biome> biome) {
        return has(CommonBiomeTags.IS_END_MIDLAND, biome);
    }

    /**
     * Returns true if the given biome was added as barrens biome in the end, considering the Vanilla end biomes,
     * and any biomes added to the End as barrens biome by mods.
     *
     * @param biome The biome to search for
     */
    public static boolean canGenerateAsEndBarrens(ResourceKey<Biome> biome) {
        return has(CommonBiomeTags.IS_END_BARRENS, biome);
    }

    public static boolean canGenerateInEnd(ResourceKey<Biome> biome) {
        return canGenerateAsHighlandsBiome(biome)
                || canGenerateAsEndBarrens(biome)
                || canGenerateAsEndMidlands(biome)
                || canGenerateAsSmallIslandsBiome(biome)
                || canGenerateAsMainIslandBiome(biome);
    }
}
