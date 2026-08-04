package de.ambertation.wover.biome.api.modification.predicates;

import de.ambertation.wover.biome.impl.modification.predicates.BiomePredicateRegistryImpl;
import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.entrypoint.LibWoverBiome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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
    public static final ResourceKey<Registry<MapCodec<? extends BiomePredicate>>> BIOME_PREDICATE_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(LibWoverBiome.C.id("wover/biome_predicates"));

    /**
     * The Registry itself.
     */
    public static final Registry<MapCodec<? extends BiomePredicate>> BIOME_PREDICATES = BiomePredicateRegistryImpl.BIOME_PREDICATES;

    /**
     * Registers a new {@link BiomePredicate} with the given {@link ResourceLocation} and {@link KeyDispatchDataCodec}.
     *
     * @param location             type of the {@link BiomePredicate}.
     * @param keyDispatchDataCodec The {@link KeyDispatchDataCodec} of the {@link BiomePredicate}.
     * @return The registered {@link BiomePredicate}.
     */
    public static MapCodec<? extends BiomePredicate> register(
            ResourceLocation location,
            KeyDispatchDataCodec<? extends BiomePredicate> keyDispatchDataCodec
    ) {
        return BiomePredicateRegistryImpl.register(BIOME_PREDICATES, location, keyDispatchDataCodec);
    }

    private BiomePredicateRegistry() {
    }
}
