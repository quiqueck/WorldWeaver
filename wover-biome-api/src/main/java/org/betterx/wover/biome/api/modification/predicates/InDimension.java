package org.betterx.wover.biome.api.modification.predicates;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.dimension.LevelStem;

public record InDimension(ResourceKey<LevelStem> dimensionKey) implements BiomePredicate {
    public static final KeyDispatchDataCodec<InDimension> CODEC = KeyDispatchDataCodec
            .of(ResourceKey.codec(Registries.LEVEL_STEM)
                           .xmap(InDimension::new, InDimension::dimensionKey)
                           .fieldOf("dimension_key")
            );


    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        final LevelStem dimension = ctx.levelStems.get(dimensionKey);
        if (dimension == null) return false;

        return dimension.generator()
                        .getBiomeSource()
                        .possibleBiomes()
                        .stream()
                        .map(Holder::value)
                        .anyMatch(entry -> entry.equals(ctx.biome));
    }
}
