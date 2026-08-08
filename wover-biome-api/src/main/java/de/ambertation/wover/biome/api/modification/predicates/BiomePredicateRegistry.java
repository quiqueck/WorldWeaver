package de.ambertation.wover.biome.api.modification.predicates;

import de.ambertation.wover.biome.impl.modification.predicates.BiomePredicateRegistryImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

/**
 * A BuiltinRegistry for {@link BiomePredicate}s.
 */
public class BiomePredicateRegistry {
    /**
     * The Key of the Registry. ({@code wover/biome_predicates})
     */
    public static final ResourceKey<Registry<MapCodec<? extends BiomePredicate>>> BIOME_PREDICATE_REGISTRY =
            BiomePredicateRegistryImpl.BIOME_PREDICATE_REGISTRY;

    /**
     * The Registry itself.
     */
    public static final Registry<MapCodec<? extends BiomePredicate>> BIOME_PREDICATES = BiomePredicateRegistryImpl.BIOME_PREDICATES;

    /**
     * Registers a new {@link BiomePredicate} with the given {@link Identifier} and {@link MapCodec}.
     * <p>
     * 26.3 removed {@code KeyDispatchDataCodec}, which used to be nothing but a wrapper around the
     * {@link MapCodec} this registry actually stores, so the codec is now passed directly.
     *
     * @param location type of the {@link BiomePredicate}.
     * @param codec    The {@link MapCodec} of the {@link BiomePredicate}.
     * @return The registered {@link BiomePredicate}.
     */
    public static MapCodec<? extends BiomePredicate> register(
            Identifier location,
            MapCodec<? extends BiomePredicate> codec
    ) {
        return BiomePredicateRegistryImpl.register(BIOME_PREDICATES, location, codec);
    }

    private BiomePredicateRegistry() {
    }
}
