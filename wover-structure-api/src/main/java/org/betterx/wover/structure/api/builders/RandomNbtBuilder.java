package org.betterx.wover.structure.api.builders;

import org.betterx.wover.structure.api.structures.StructurePlacement;
import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructure;

import net.minecraft.resources.ResourceLocation;

/**
 * A {@link BaseStructureBuilder} for {@link RandomNbtStructure}s — structures that, at generation time,
 * pick one of several {@code .nbt} templates (added via {@link #addElement(ResourceLocation, int, double)})
 * at random (weighted) and place it using a {@link StructurePlacement} strategy. Created via
 * {@link org.betterx.wover.structure.api.StructureKey.RandomNbt#bootstrap}.
 */
public interface RandomNbtBuilder extends BaseStructureBuilder<RandomNbtStructure, RandomNbtBuilder> {
    /**
     * Sets the {@link StructurePlacement} strategy used to find a valid generation point for the
     * structure. Defaults to {@link StructurePlacement#SURFACE}.
     *
     * @param value The placement strategy to use
     * @return This builder instance, for chaining
     */
    RandomNbtBuilder placement(StructurePlacement value);

    /**
     * If {@code true}, air blocks in the {@code .nbt} template are preserved instead of being ignored
     * during placement (see {@link net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor}).
     * Defaults to {@code false}.
     *
     * @param value {@code true} to keep air blocks
     * @return This builder instance, for chaining
     */
    RandomNbtBuilder keepAir(boolean value);

    /**
     * Adds a {@code .nbt} template as a possible element for this structure.
     *
     * @param elementId The location of the {@code .nbt} template (as resolved by the active
     *                  {@link net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager})
     * @param yOffset   The vertical offset (in blocks) applied to the found generation point before
     *                  placing this element
     * @param weight    The relative weight used when randomly selecting between multiple elements
     * @return This builder instance, for chaining
     */
    RandomNbtBuilder addElement(ResourceLocation elementId, int yOffset, double weight);
}
