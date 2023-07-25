package org.betterx.wover.biome.api.modification.predicates;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

public class Always implements BiomePredicate {
    public static final Always INSTANCE = new Always();
    public static final KeyDispatchDataCodec<Always> CODEC
            = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));


    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(BiomeSelectionContext ctx) {
        return true;
    }
}
