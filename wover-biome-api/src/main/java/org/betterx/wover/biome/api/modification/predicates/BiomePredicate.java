package org.betterx.wover.biome.api.modification.predicates;

import org.betterx.wover.biome.impl.api.modification.predicates.BiomePredicateRegistryImpl;

import com.mojang.serialization.Codec;
import net.minecraft.util.KeyDispatchDataCodec;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

import java.util.function.Function;

public interface BiomePredicate {
    /**
     * Codec for a BiomePredicate that delegates to the
     * Codec returned by {@link #codec()}.
     */
    Codec<BiomePredicate> CODEC = BiomePredicateRegistryImpl
            .BIOME_PREDICATES.byNameCodec()
                             .dispatch(
                                     p -> p.codec().codec(),
                                     Function.identity()
                             );
    KeyDispatchDataCodec<? extends BiomePredicate> codec();
    boolean test(BiomeSelectionContext ctx);
}
