package org.betterx.wover.tag.api.predefined;

import org.betterx.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.ApiStatus;

/**
 * Tags that are used to mark the purpose of an end biome in a vanilla generation.
 */
public class CommonBiomeTags {
    /**
     * {@code c:is_end_center} - Tag for biomes that are in the center of the end (ie. the dragon island).
     */
    public static final TagKey<Biome> IS_END_CENTER = TagManager.BIOMES.makeCommonTag("is_end_center");

    /**
     * {@code c:is_end_highland} - Tag for biomes that are highlands in the end.
     */
    public static final TagKey<Biome> IS_END_HIGHLAND = TagManager.BIOMES.makeCommonTag("is_end_highland");

    /**
     * {@code c:is_end_midland} - Tag for biomes that are midlands in the end.
     */
    public static final TagKey<Biome> IS_END_MIDLAND = TagManager.BIOMES.makeCommonTag("is_end_midland");
    /**
     * {@code c:is_end_barrens} - Tag for biomes that are barrens in the end.
     */
    public static final TagKey<Biome> IS_END_BARRENS = TagManager.BIOMES.makeCommonTag("is_end_barrens");
    /**
     * {@code c:is_small_end_island} - Tag for biomes that are small islands in the end.
     */
    public static final TagKey<Biome> IS_SMALL_END_ISLAND = TagManager.BIOMES.makeCommonTag("is_small_end_island");

    /**
     * Called internally to ensure that the tags are created.
     */
    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }

    private CommonBiomeTags() {
    }
}
