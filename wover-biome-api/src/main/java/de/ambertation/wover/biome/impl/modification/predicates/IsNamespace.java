package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;

public record IsNamespace(String namespace) implements BiomePredicate {
    public static final MapCodec<IsNamespace> CODEC = Codec.STRING
            .xmap(IsNamespace::new, IsNamespace::namespace)
            .fieldOf("namespace");


    @Override
    public MapCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        return ctx.biomeKey.identifier().getNamespace().equals(namespace);
    }
}
