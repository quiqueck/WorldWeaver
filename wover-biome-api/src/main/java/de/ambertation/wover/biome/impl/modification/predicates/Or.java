package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.MapCodec;

import java.util.List;

public record Or(List<BiomePredicate> predicates) implements BiomePredicate {
    public static final MapCodec<Or> CODEC = BiomePredicate.CODEC
            .listOf()
            .xmap(Or::new, Or::predicates)
            .fieldOf("predicates");

    @Override
    public MapCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        return predicates().stream().anyMatch(p -> p.test(ctx));
    }
}
