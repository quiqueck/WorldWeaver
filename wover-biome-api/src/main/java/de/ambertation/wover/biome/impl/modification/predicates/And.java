package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.MapCodec;

import java.util.List;

public record And(List<BiomePredicate> predicates) implements BiomePredicate {
    public static final MapCodec<And> CODEC = BiomePredicate.CODEC
            .listOf()
            .xmap(And::new, And::predicates)
            .fieldOf("predicates");

    @Override
    public MapCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        return predicates().stream().allMatch(p -> p.test(ctx));
    }
}
