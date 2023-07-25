package org.betterx.wover.biome.api.modification.predicates;

import org.betterx.wover.biome.impl.api.modification.predicates.BiomePredicateRegistryImpl;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverBiome;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

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
}
