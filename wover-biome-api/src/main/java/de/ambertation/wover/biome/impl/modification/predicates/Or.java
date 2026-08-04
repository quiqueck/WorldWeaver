package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.util.KeyDispatchDataCodec;

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
    public boolean test(Context ctx) {
        return predicates().stream().anyMatch(p -> p.test(ctx));
    }
}
