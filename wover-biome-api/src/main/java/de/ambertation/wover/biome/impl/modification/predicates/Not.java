package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.util.KeyDispatchDataCodec;

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
    public boolean test(Context ctx) {
        return !predicate().test(ctx);
    }
}
