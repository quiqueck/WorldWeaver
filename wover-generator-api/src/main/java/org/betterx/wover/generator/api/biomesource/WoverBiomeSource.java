package org.betterx.wover.generator.api.biomesource;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public abstract class WoverBiomeSource extends BiomeSource {
    @Override
    protected @NotNull Codec<? extends BiomeSource> codec() {
        return null;
    }

    @Override
    protected @NotNull Stream<Holder<Biome>> collectPossibleBiomes() {
        return null;
    }

    @Override
    public @NotNull Holder<Biome> getNoiseBiome(int i, int j, int k, Climate.Sampler sampler) {
        return null;
    }
}
