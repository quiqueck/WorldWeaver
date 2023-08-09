package org.betterx.wover.tag.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * Registry for biome tags.
 * <p>
 * This is an extension of {@link TagRegistry} that adds some biome-specific methods.
 */
public interface BiomeTagRegistry extends TagRegistry<Biome, TagBootstrapContext<Biome>> {
    /**
     * Get or create a structure {@link TagKey}.
     * <p>
     * This is a special tag that is used to determine if a biome is a structure biome.
     * The name of the tag will be prefixed with {@code has_structure/}.
     *
     * @param mod  The mod that owns the tag.
     * @param name The name of the tag.
     * @return the corresponding TagKey {@link TagKey}.
     */
    TagKey<Biome> makeStructureTag(ModCore mod, String name);

    /**
     * Get or create a structure {@link TagKey}.
     * <p>
     * This is a special tag that is used to determine if a biome is a structure biome.
     * The name of the tag will be prefixed with {@code has_structure/}.
     *
     * @param baseStructure The key for the structure.
     * @return the corresponding TagKey {@link TagKey}.
     */
    TagKey<Biome> makeStructureTag(ResourceKey<Structure> baseStructure);
}
