package com.betterxlib.impl.biome;

import com.betterxlib.api.biome.EndBiomePlacement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/**
 * BiomeModifier for adding custom End biomes.
 * <p>
 * This modifier is typically used via JSON data files but can also be
 * created programmatically.
 * <p>
 * JSON format:
 * <pre>{@code
 * {
 *   "type": "betterxlib:add_end_biome",
 *   "biome": "mymod:crystal_plains",
 *   "placement": "end_land",
 *   "weight": 0.5
 * }
 * }</pre>
 */
public record AddEndBiomeModifier(
    Holder<Biome> biome,
    EndBiomePlacement placement,
    float weight
) implements BiomeModifier {

    public static final MapCodec<AddEndBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Biome.CODEC.fieldOf("biome").forGetter(AddEndBiomeModifier::biome),
            EndBiomePlacement.CODEC.fieldOf("placement").forGetter(AddEndBiomeModifier::placement),
            Codec.FLOAT.fieldOf("weight").forGetter(AddEndBiomeModifier::weight)
        ).apply(instance, AddEndBiomeModifier::new)
    );

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        // This modifier doesn't actually modify existing biomes
        // Instead, it registers the biome for End generation
        // The actual biome injection happens at the biome source level

        // Registration is handled during Phase.AFTER_EVERYTHING to ensure
        // all biomes are already registered
        if (phase == Phase.AFTER_EVERYTHING && biome.equals(this.biome)) {
            // The biome is registered through the BiomeRegistry system
            // when the modifier is loaded during datapack application
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }

    /**
     * Create a new AddEndBiomeModifier.
     *
     * @param biome the biome to add
     * @param placement where in the End to place the biome
     * @param weight the generation weight
     * @return a new modifier
     */
    public static AddEndBiomeModifier create(Holder<Biome> biome, EndBiomePlacement placement, float weight) {
        return new AddEndBiomeModifier(biome, placement, weight);
    }
}
