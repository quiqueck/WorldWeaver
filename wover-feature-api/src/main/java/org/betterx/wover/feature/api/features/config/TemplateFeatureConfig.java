package org.betterx.wover.feature.api.features.config;

import org.betterx.wover.feature.impl.features.FeatureTemplateImpl;
import org.betterx.wover.util.RandomizedWeightedList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Config for a {@link org.betterx.wover.feature.api.features.TemplateFeature}. The Configuration
 * holds a weighted list of {@link FeatureTemplateImpl}s that will be placed at random.
 * <p>
 * Structures are identified by {@link ResourceLocation}s. The structure
 * is loaded from a datapack at {@code data/<namespace>/structures/<path>.nbt}.
 */
public class TemplateFeatureConfig implements FeatureConfiguration {
    /**
     * Codec for {@link TemplateFeatureConfig}.
     */
    public static final Codec<TemplateFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    RandomizedWeightedList
                            .buildCodec(FeatureTemplateImpl.CODEC)
                            .fieldOf("structures")
                            .forGetter((TemplateFeatureConfig cfg) -> cfg.structures)
            )
            .apply(instance, TemplateFeatureConfig::of)
    );

    private final RandomizedWeightedList<FeatureTemplate> structures;

    /**
     * Creates a new {@link TemplateFeatureConfig} from the given structures
     *
     * @param structures The weighted list of structures.
     * @return A new {@link TemplateFeatureConfig}.
     */
    public static TemplateFeatureConfig of(RandomizedWeightedList<FeatureTemplate> structures) {
        return new TemplateFeatureConfig(structures);
    }

    private TemplateFeatureConfig(RandomizedWeightedList<FeatureTemplate> structures) {
        this.structures = structures;
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
         * file {@code data/<namespace>/structures/<path>.nbt}.
         *
         * @return the location
         */
        ResourceLocation getLocation();

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
