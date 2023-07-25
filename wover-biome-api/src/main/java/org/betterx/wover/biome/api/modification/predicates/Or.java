package org.betterx.wover.biome.api.modification.predicates;

import net.minecraft.util.KeyDispatchDataCodec;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

import java.util.List;

public record Or(List<BiomePredicate> predicates) implements BiomePredicate {
    public static final KeyDispatchDataCodec<Or> CODEC = KeyDispatchDataCodec.of(
            BiomePredicate.CODEC
                    .listOf()
                    .xmap(Or::new, Or::predicates)
                    .fieldOf("predicates"));

    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(BiomeSelectionContext ctx) {
        return predicates().stream().anyMatch(p -> p.test(ctx));
    }
}
