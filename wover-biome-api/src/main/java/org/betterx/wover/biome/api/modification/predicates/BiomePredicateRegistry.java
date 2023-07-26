package org.betterx.wover.biome.api.modification.predicates;

import org.betterx.wover.biome.impl.modification.predicates.BiomePredicateRegistryImpl;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverBiome;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;

/**
 * A BuiltinRegistry for {@link BiomePredicate}s.
 */
public class BiomePredicateRegistry {
    /**
     * The Key of the Registry. ({@code wover/biome_predicates})
     */
    public static final ResourceKey<Registry<Codec<? extends BiomePredicate>>> BIOME_PREDICATE_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(WoverBiome.C.id("wover/biome_predicates"));

    /**
     * The Registry itself.
     */
    public static final Registry<Codec<? extends BiomePredicate>> BIOME_PREDICATES = BiomePredicateRegistryImpl.BIOME_PREDICATES;

    /**
     * Registers a new {@link BiomePredicate} with the given {@link ResourceLocation} and {@link KeyDispatchDataCodec}.
     *
     * @param location             type of the {@link BiomePredicate}.
     * @param keyDispatchDataCodec The {@link KeyDispatchDataCodec} of the {@link BiomePredicate}.
     * @return The registered {@link BiomePredicate}.
     */
    public static Codec<? extends BiomePredicate> register(
            ResourceLocation location,
            KeyDispatchDataCodec<? extends BiomePredicate> keyDispatchDataCodec
    ) {
        return BiomePredicateRegistryImpl.register(BIOME_PREDICATES, location, keyDispatchDataCodec);
    }

    private BiomePredicateRegistry() {
    }
}
