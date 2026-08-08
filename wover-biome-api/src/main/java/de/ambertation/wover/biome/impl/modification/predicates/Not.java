package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.MapCodec;

public record Not(BiomePredicate predicate) implements BiomePredicate {
    public static final MapCodec<Not> CODEC = BiomePredicate.CODEC
            .xmap(Not::new, Not::predicate)
            .fieldOf("predicate");

    @Override
    public MapCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        return !predicate().test(ctx);
    }
}
