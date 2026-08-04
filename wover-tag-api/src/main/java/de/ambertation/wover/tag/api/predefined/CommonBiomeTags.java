package de.ambertation.wover.tag.api.predefined;

import de.ambertation.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.ApiStatus;

/**
 * Tags that are used to mark the purpose of an end biome in a vanilla generation.
 */
public class CommonBiomeTags {
    /**
     * {@code wover:is_end/center} - Tag for biomes that are in the center of the end (ie. the dragon island).
     */
    public static final TagKey<Biome> IS_END_CENTER = TagManager.BIOMES.makeWorldWeaverTag("is_end/center");

    /**
     * {@code wover:is_end/land} - Tag for biomes that are highlands or midlands in the end.
     */
    public static final TagKey<Biome> IS_END_LAND = TagManager.BIOMES.makeWorldWeaverTag("is_end/land");

    /**
     * {@code wover:is_end/highland} - Tag for biomes that are highlands in the end.
     */
    public static final TagKey<Biome> IS_END_HIGHLAND = TagManager.BIOMES.makeWorldWeaverTag("is_end/highland");

    /**
     * {@code wover:is_end/midland} - Tag for biomes that are midlands in the end.
     */
    public static final TagKey<Biome> IS_END_MIDLAND = TagManager.BIOMES.makeWorldWeaverTag("is_end/midland");
    /**
     * {@code wover:is_end/barrens} - Tag for biomes that are barrens in the end.
     */
    public static final TagKey<Biome> IS_END_BARRENS = TagManager.BIOMES.makeWorldWeaverTag("is_end/barrens");
    /**
     * {@code wover:is_end/small_island} - Tag for biomes that are small islands in the end.
     */
    public static final TagKey<Biome> IS_SMALL_END_ISLAND = TagManager.BIOMES.makeWorldWeaverTag("is_end/small_island");

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
