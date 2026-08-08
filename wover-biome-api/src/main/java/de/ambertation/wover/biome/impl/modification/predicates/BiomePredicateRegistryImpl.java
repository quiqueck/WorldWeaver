package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;
import de.ambertation.wover.core.api.registry.BuiltInRegistryManager;
import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.entrypoint.LibWoverBiome;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.ApiStatus;

public class BiomePredicateRegistryImpl {
    // Both the registry key and the Registry itself are owned here, and BiomePredicateRegistry aliases
    // them. They used to be split the other way round - the key on the public BiomePredicateRegistry, read
    // from here during this class' static initializer - which is a class-initialization cycle: whichever of
    // the two classes is touched first, the other one observes a half-initialized class. LibWoverBiome
    // calls BiomePredicateRegistryImpl.initialize() first, so BiomePredicateRegistry.BIOME_PREDICATES was
    // in practice always left null, which also made the public
    // BiomePredicateRegistry.register(Identifier, MapCodec) entry point - the documented way for other mods
    // to add their own predicates - throw a NullPointerException. Keeping every constant that participates
    // in the cycle on this side breaks it: nothing here refers back to BiomePredicateRegistry.
    public static final ResourceKey<Registry<MapCodec<? extends BiomePredicate>>> BIOME_PREDICATE_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(LibWoverBiome.C.id("wover/biome_predicates"));

    public static final Registry<MapCodec<? extends BiomePredicate>> BIOME_PREDICATES = BuiltInRegistryManager.createRegistry(
            BIOME_PREDICATE_REGISTRY,
            BiomePredicateRegistryImpl::onBootstrap
    );

    public static MapCodec<? extends BiomePredicate> register(
            Registry<MapCodec<? extends BiomePredicate>> registry,
            Identifier location,
            MapCodec<? extends BiomePredicate> codec
    ) {
        return Registry.register(registry, location, codec);
    }

    @ApiStatus.Internal
    public static void initialize() {
        onBootstrap(BIOME_PREDICATES);

//        BiomeModifications.addFeature(
//                ctx -> ctx.canGenerateIn(LevelStem.OVERWORLD),
//                GenerationStep.Decoration.LAKES,
//                MiscOverworldPlacements.BLUE_ICE
//        );
    }

    private static MapCodec<? extends BiomePredicate> onBootstrap(Registry<MapCodec<? extends BiomePredicate>> registry) {
        final var all = LibWoverBiome.C.id("all");
        if (registry.containsKey(all)) {
            return registry.get(all).orElseThrow().value();
        }
        register(registry, LibWoverBiome.C.id("not"), Not.CODEC);
        register(registry, LibWoverBiome.C.id("and"), And.CODEC);
        register(registry, LibWoverBiome.C.id("or"), Or.CODEC);

        register(registry, LibWoverBiome.C.id("is_biome"), IsBiome.CODEC);
        register(registry, LibWoverBiome.C.id("has_tag"), HasTag.CODEC);
        register(registry, LibWoverBiome.C.id("in_dimension"), InDimension.CODEC);
        register(registry, LibWoverBiome.C.id("is_namespace"), IsNamespace.CODEC);
        register(registry, LibWoverBiome.C.id("location_path_contains"), LocationPathContains.CODEC);
        register(registry, LibWoverBiome.C.id("spawns"), Spawns.CODEC);
        register(registry, LibWoverBiome.C.id("has_structure"), HasStructure.CODEC);
        register(registry, LibWoverBiome.C.id("has_placed_feature"), HasPlacedFeature.CODEC);
        register(registry, LibWoverBiome.C.id("has_feature"), HasFeature.CODEC);
        register(registry, LibWoverBiome.C.id("config_is"), ConfigIs.CODEC);

        return register(registry, all, Always.CODEC);
    }


}
