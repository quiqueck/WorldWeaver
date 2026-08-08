package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;

public record LocationPathContains(String needle) implements BiomePredicate {
    public static final MapCodec<LocationPathContains> CODEC = Codec.STRING
            .xmap(LocationPathContains::new, LocationPathContains::needle)
            .fieldOf("needle");


    @Override
    public MapCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        return ctx.biomeKey.identifier().getPath().contains(needle);
    }
}
