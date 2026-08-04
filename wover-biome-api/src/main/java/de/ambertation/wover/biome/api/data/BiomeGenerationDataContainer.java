package de.ambertation.wover.biome.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * The climate parameter points and intended placement tag that were used to generate a Biome.
 * <p>
 * This is the part of {@link BiomeData} that describes <i>where</i> a Biome was intended to be placed, as
 * opposed to fog density and other visual/generation details stored directly on {@link BiomeData}.
 *
 * @param parameterPoints  The climate parameter points added through
 *                         {@link de.ambertation.wover.biome.api.builder.BiomeBuilder#addClimate(Climate.ParameterPoint)}.
 * @param intendedPlacement The tag set through
 *                          {@link de.ambertation.wover.biome.api.builder.BiomeBuilder#intendedPlacement(TagKey)},
 *                          or {@code null} if none was set.
 */
public record BiomeGenerationDataContainer(List<Climate.ParameterPoint> parameterPoints,
                                           @Nullable TagKey<Biome> intendedPlacement) {
    /**
     * A container with no parameter points and no intended placement.
     */
    public static final BiomeGenerationDataContainer EMPTY = new BiomeGenerationDataContainer(List.of(), null);
    /**
     * The {@link Codec} for this container.
     */
    public static Codec<BiomeGenerationDataContainer> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Climate.ParameterPoint.CODEC
                            .listOf()
                            .optionalFieldOf("parameter_points", List.of())
                            .forGetter(BiomeGenerationDataContainer::parameterPoints),
                    TagKey
                            .codec(Registries.BIOME)
                            .optionalFieldOf("intended_placement", null)
                            .forGetter(BiomeGenerationDataContainer::intendedPlacement)
            ).apply(instance, BiomeGenerationDataContainer::new));
}
