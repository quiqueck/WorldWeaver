package org.betterx.wover.biome.impl.modification.predicates;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;

public class Always implements BiomePredicate {
    public static final Always INSTANCE = new Always();
    public static final KeyDispatchDataCodec<Always> CODEC
            = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));


    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        return true;
    }
}
