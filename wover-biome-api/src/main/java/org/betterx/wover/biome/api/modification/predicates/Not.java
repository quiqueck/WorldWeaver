package org.betterx.wover.biome.api.modification.predicates;

import net.minecraft.util.KeyDispatchDataCodec;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

public record Not(BiomePredicate predicate) implements BiomePredicate {
    public static final KeyDispatchDataCodec<Not> CODEC = KeyDispatchDataCodec.of(
            BiomePredicate.CODEC
                    .xmap(Not::new, Not::predicate)
                    .fieldOf("predicate"));

    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(BiomeSelectionContext ctx) {
        return !predicate().test(ctx);
    }
}
