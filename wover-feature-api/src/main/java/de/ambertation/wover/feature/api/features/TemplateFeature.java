package de.ambertation.wover.feature.api.features;

import de.ambertation.wover.feature.impl.features.FeatureTemplateImpl;
import de.ambertation.wover.structure.api.StructureNBT;
import de.ambertation.wover.util.RandomizedWeightedList;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Places a structure (<b>{@code wover:template}</b>).
 * <p>
 * The feature holds a weighted list of {@link FeatureTemplate}s and places one of them at random.
 * Structures are identified by {@link Identifier}s and loaded from a datapack at
 * {@code data/<namespace>/structure/<path>.nbt}.
 * <p>
 * 26.3 collapsed {@code ConfiguredFeature} into {@link Feature}, so the former {@code TemplateFeatureConfig}
 * (including its nested {@link FeatureTemplate} interface) was merged into this class. The feature is no
 * longer generic over its configuration, and the serialized {@code structures} field sits at the top level of
 * the feature JSON instead of inside a {@code config} object.
 *
 * @see de.ambertation.wover.feature.api.Features#TEMPLATE
 */
public class TemplateFeature implements Feature {
    /**
     * The codec for this feature.
     */
    public static final MapCodec<TemplateFeature> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
            .group(
                    RandomizedWeightedList
                            .buildCodec(FeatureTemplateImpl.CODEC)
                            .fieldOf("structures")
                            .forGetter((TemplateFeature cfg) -> cfg.structures)
            )
            .apply(instance, TemplateFeature::of)
    );

    private final RandomizedWeightedList<FeatureTemplate> structures;

    /**
     * Creates a new {@link TemplateFeature} from the given structures
     *
     * @param structures The weighted list of structures.
     * @return A new {@link TemplateFeature}.
     */
    public static TemplateFeature of(RandomizedWeightedList<FeatureTemplate> structures) {
        return new TemplateFeature(structures);
    }

    private TemplateFeature(RandomizedWeightedList<FeatureTemplate> structures) {
        this.structures = structures;
    }

    /**
     * The codec that serializes this feature.
     *
     * @return {@link #CODEC}
     */
    @Override
    public MapCodec<TemplateFeature> codec() {
        return CODEC;
    }

    /**
     * Returns a random structure from the weighted list.
     *
     * @param random The random source to use.
     * @return A random structure.
     */
    public FeatureTemplate randomStructure(RandomSource random) {
        return structures.getRandomValue(random);
    }

    /**
     * Places a structure with a random rotation and mirror.
     *
     * @param level     the level to place in
     * @param generator the chunk generator
     * @param random    the random source
     * @param pos       the position to place at
     * @return {@code true} if the structure was placed
     */
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos) {
        final FeatureTemplate structure = randomStructure(random);
        return structure.generateIfPlaceable(
                level,
                pos,
                StructureNBT.getRandomRotation(random),
                StructureNBT.getRandomMirror(random)
        );
    }

    /**
     * A feature template is a structure loaded from an nbt that can be placed in the world.
     */
    public static interface FeatureTemplate {
        /**
         * Placement offset in y direction.
         * <p>
         * When a structure is loaded, the bounding box will get calculated.
         * The bottom center of the bounding box will be placed on th eposition supplied to {@link #generateIfPlaceable(ServerLevelAccessor, BlockPos, Rotation, Mirror)}.
         * The value from {@link #getOffsetY()} is added to the y coordinate.
         *
         * @return the offset
         */
        int getOffsetY();

        /**
         * The location of the template.
         * <p>
         * The structure is loaded from a resource location in a data pack from the
         * file {@code data/<namespace>/structure/<path>.nbt}.
         *
         * @return the location
         */
        Identifier getLocation();

        /**
         * Places the structure in the world. The structure is placed with the given rotation and mirror. The bottom
         * center of the bounding box will be placed on the supplied position. And {@link #getOffsetY()} is added to
         * the y coordinate.
         *
         * @param level The level to place the structure in
         * @param pos   The position to place the structure at. {@link #getOffsetY()} is added to the y coordinate.
         * @param r     The rotation to place the structure with
         * @param m     The mirror to place the structure with
         * @return true if the structure was placed, false otherwise
         */
        boolean generateIfPlaceable(
                ServerLevelAccessor level,
                BlockPos pos,
                Rotation r,
                Mirror m
        );

        /**
         * {@code true} if the structure is already loaded, {@code false} otherwise.
         *
         * @return {@code true} if the structure is already loaded, {@code false} otherwise.
         */
        boolean loaded();
    }
}
