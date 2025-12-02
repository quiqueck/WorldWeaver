package com.betterxlib.impl.biome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/**
 * BiomeModifier for adding features to existing biomes.
 * <p>
 * This modifier adds a placed feature to all biomes in a biome tag or holder set.
 * <p>
 * JSON format:
 * <pre>{@code
 * {
 *   "type": "betterxlib:add_feature_to_biome",
 *   "biomes": "#minecraft:is_end",
 *   "feature": "mymod:crystal_spikes",
 *   "step": "vegetal_decoration"
 * }
 * }</pre>
 */
public record AddFeatureToBiomeModifier(
    HolderSet<Biome> biomes,
    Holder<PlacedFeature> feature,
    GenerationStep.Decoration step
) implements BiomeModifier {

    public static final MapCodec<AddFeatureToBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddFeatureToBiomeModifier::biomes),
            PlacedFeature.CODEC.fieldOf("feature").forGetter(AddFeatureToBiomeModifier::feature),
            GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddFeatureToBiomeModifier::step)
        ).apply(instance, AddFeatureToBiomeModifier::new)
    );

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && biomes.contains(biome)) {
            builder.getGenerationSettings().addFeature(step, feature);
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }

    /**
     * Create a new AddFeatureToBiomeModifier.
     *
     * @param biomes the biomes to add the feature to
     * @param feature the feature to add
     * @param step the generation step
     * @return a new modifier
     */
    public static AddFeatureToBiomeModifier create(
        HolderSet<Biome> biomes,
        Holder<PlacedFeature> feature,
        GenerationStep.Decoration step
    ) {
        return new AddFeatureToBiomeModifier(biomes, feature, step);
    }
}
