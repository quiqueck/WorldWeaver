package com.betterxlib.impl.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/**
 * BiomeModifier for adding custom Nether biomes.
 * <p>
 * This modifier is typically used via JSON data files but can also be
 * created programmatically.
 * <p>
 * JSON format:
 * <pre>{@code
 * {
 *   "type": "betterxlib:add_nether_biome",
 *   "biome": "mymod:crimson_caves",
 *   "weight": 0.3
 * }
 * }</pre>
 */
public record AddNetherBiomeModifier(
    Holder<Biome> biome,
    float weight
) implements BiomeModifier {

    public static final MapCodec<AddNetherBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Biome.CODEC.fieldOf("biome").forGetter(AddNetherBiomeModifier::biome),
            Codec.FLOAT.fieldOf("weight").forGetter(AddNetherBiomeModifier::weight)
        ).apply(instance, AddNetherBiomeModifier::new)
    );

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        // This modifier doesn't modify existing biomes directly
        // Instead, it registers the biome for Nether generation
        // The actual biome injection happens at the biome source level
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }

    /**
     * Create a new AddNetherBiomeModifier.
     *
     * @param biome the biome to add
     * @param weight the generation weight
     * @return a new modifier
     */
    public static AddNetherBiomeModifier create(Holder<Biome> biome, float weight) {
        return new AddNetherBiomeModifier(biome, weight);
    }
}
