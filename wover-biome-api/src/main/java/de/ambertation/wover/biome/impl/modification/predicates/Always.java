package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.MapCodec;

public class Always implements BiomePredicate {
    public static final Always INSTANCE = new Always();
    public static final MapCodec<Always> CODEC = MapCodec.unit(INSTANCE);


    @Override
    public MapCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        return true;
    }
}
