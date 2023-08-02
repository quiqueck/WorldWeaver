package org.betterx.wover.biome.api.modification.predicates;

import org.betterx.wover.biome.impl.modification.predicates.*;
import org.betterx.wover.core.api.ModCore;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A predicate that can be used to test if a biome matches certain conditions.
 * <p>
 * Predicates can be serialized using {@link #CODEC}, and are managed in the
 * {@link BiomePredicateRegistry}.
 * <p>
 * If you need to, you can add custom predicates as well using
 * {@link BiomePredicateRegistry#register(ResourceLocation, KeyDispatchDataCodec)}.
 */
public interface BiomePredicate {
    /**
     * Creates a predicate that is {@code true} when any one of the passed predicates is {@code true}.
     * <p>
     * This predicate is lazy, meaning that it will stop testing predicates once one of them returns
     * {@code true}.
     *
     * @param predicates the predicates to test
     * @return the or predicate
     */
    static BiomePredicate or(BiomePredicate... predicates) {
        return new Or(List.of(predicates));
    }

    /**
     * Creates a predicate that is {@code true} when all of the passed predicates are {@code true}.
     * <p>
     * This predicate is lazy, meaning that it will stop testing predicates once one of them returns
     * {@code false}.
     *
     * @param predicates the predicates to test
     * @return the and predicate
     */
    static BiomePredicate and(BiomePredicate... predicates) {
        return new And(List.of(predicates));
    }

    /**
     * Alias name for {@link #or(BiomePredicate...)}.
     *
     * @param predicates the predicates to test
     * @return the or predicate
     * @see #or(BiomePredicate...)
     */
    static BiomePredicate anyOf(BiomePredicate... predicates) {
        return or(predicates);
    }
    /**
     * Alias name for {@link #and(BiomePredicate...)}.
     *
     * @param predicates the predicates to test
     * @return the and predicate
     * @see #and(BiomePredicate...)
     */
    static BiomePredicate allOf(BiomePredicate... predicates) {
        return and(predicates);
    }

    /**
     * Creates a predicate that negates the pass predicate.
     *
     * @param predicate the predicate to negate
     * @return the negated predicate
     */
    static BiomePredicate not(BiomePredicate predicate) {
        return new Not(predicate);
    }

    /**
     * Creates a predicate that is always {@code true}.
     *
     * @return the always true predicate
     */
    static BiomePredicate always() {
        return Always.INSTANCE;
    }

    /**
     * Creates a predicate that tests if a biome matches the passed key.
     *
     * @param key the biome key to compare with
     * @return the predicate
     */
    static BiomePredicate isBiome(ResourceKey<Biome> key) {
        return new IsBiome(key);
    }

    /**
     * Creates a predicate that tests if a biome matches any of the passed keys.
     *
     * @param keys the biome keys to compare with
     * @return the predicate
     */
    @SafeVarargs
    static BiomePredicate inBiomes(ResourceKey<Biome>... keys) {
        return new Or(Arrays.stream(keys).map(biomeKey -> (BiomePredicate) new IsBiome(biomeKey)).toList());
    }

    /**
     * Creates a predicate that will return {@code true} when the biome does not match any of the passed keys.
     *
     * @param keys the biome keys to compare with
     * @return the predicate
     */
    @SafeVarargs
    static BiomePredicate notInBiomes(ResourceKey<Biome>... keys) {
        return new Not(inBiomes(keys));
    }

    /**
     * Creates a predicate that tests if a biome is in the passed dimension.
     *
     * @param key the dimension key
     * @return the predicate
     */
    static BiomePredicate inDimension(ResourceKey<LevelStem> key) {
        return new InDimension(key);
    }
    /**
     * Creates a predicate that tests if a biome is in the overworld.
     *
     * @return the predicate
     */
    static BiomePredicate inOverworld() {
        return InDimension.OVERWORLD;
    }

    /**
     * Creates a predicate that tests if a biome is in the nether.
     *
     * @return the predicate
     */
    static BiomePredicate inEnd() {
        return InDimension.END;
    }

    /**
     * Creates a predicate that tests if a biome is in the nether.
     *
     * @return
     */
    static BiomePredicate inNether() {
        return InDimension.NETHER;
    }

    /**
     * Creates a predicate that tests if a biome has the passed tag.
     *
     * @param tag the tag to test
     * @return the predicate
     */
    static BiomePredicate hasTag(TagKey<Biome> tag) {
        return new HasTag(tag);
    }

    /**
     * Creates a predicate that tests if an entity can spawn in the biome
     *
     * @param type the entity type
     * @return the predicate
     */
    static BiomePredicate spawns(EntityType<?> type) {
        return new Spawns(type);
    }

    /**
     * Creates a predicate that tests if the passed structure can generate in the biome.
     *
     * @param key the structure key
     * @return the predicate
     */
    static BiomePredicate hasStructure(ResourceKey<Structure> key) {
        return new HasStructure(key);
    }

    /**
     * Creates a predicate that tests if the passed feature can generate in the biome.
     *
     * @param key the feature key
     * @return the predicate
     */
    static BiomePredicate hasPlacedFeature(ResourceKey<PlacedFeature> key) {
        return new HasPlacedFeature(key);
    }

    /**
     * Creates a predicate that tests if the passed configured feature can generate in the biome.
     *
     * @param key the configured feature key
     * @return the predicate
     */
    static BiomePredicate hasConfiguredFeature(ResourceKey<ConfiguredFeature<?, ?>> key) {
        return new HasConfiguredFeature(key);
    }

    /**
     * Creates a predicate that tests if the biome is in the vanilla (<i>minecraft</i>) namespace.
     *
     * @return the predicate
     */
    static BiomePredicate isVanilla() {
        return new IsNamespace("minecraft");
    }

    /**
     * Creates a predicate that tests if the biome is in the passed mod-namespace.
     *
     * @param namespace the namespace
     * @return the predicate
     */
    static BiomePredicate inNamespace(String namespace) {
        return new IsNamespace(namespace);
    }

    /**
     * Creates a predicate that tests if the biome is in the namespace of the passed mod.
     *
     * @param core the mod core
     * @return the predicate
     */
    static BiomePredicate inNamespace(ModCore core) {
        return new IsNamespace(core.namespace);
    }

    /**
     * Creates a predicate that returns {@code true} when the biome is not in the passed namespace.
     *
     * @param namespace the namespace
     * @return the predicate
     */
    static BiomePredicate notInNamespace(String namespace) {
        return not(new IsNamespace(namespace));
    }

    /**
     * Creates a predicate that returns {@code true} when the biome is not in the namespace of the passed mod.
     *
     * @param core the mod core
     * @return the predicate
     */
    static BiomePredicate notInNamespace(ModCore core) {
        return not(new IsNamespace(core.namespace));
    }

    /**
     * The context object for a {@link BiomePredicate}.
     * <p>
     * This object contains all the information that is available to a predicate when it is tested.
     */
    final class Context {
        /**
         * The registry access object.
         */
        @NotNull
        public final RegistryAccess registryAccess;
        /**
         * The key for the Biome that is being tested.
         */
        @NotNull
        public final ResourceKey<Biome> biomeKey;
        /**
         * The Biome that is being tested.
         */
        @NotNull
        public final Biome biome;
        /**
         * The holder for the Biome that is being tested.
         */
        @NotNull
        public final Holder<Biome> biomeHolder;

        /**
         * The biome registry from the {@link #registryAccess}
         */
        @NotNull
        public final Registry<Biome> biomes;

        /**
         * The level stem registry from the {@link #registryAccess}
         */
        @NotNull
        public final Registry<LevelStem> levelStems;
        /**
         * The structure registry from the {@link #registryAccess}
         */
        @NotNull
        public final Registry<Structure> structures;

        /**
         * The placed feature registry from the {@link #registryAccess}
         */
        @NotNull
        public final Registry<PlacedFeature> placedFeatures;
        /**
         * The configured feature registry from the {@link #registryAccess}
         */
        @NotNull
        public final Registry<ConfiguredFeature<?, ?>> configuredFeatures;

        private Context(
                @NotNull RegistryAccess registryAccess,
                @NotNull Registry<Biome> biomes,
                @NotNull ResourceKey<Biome> biomeKey,
                @NotNull Biome biome
        ) {
            this.registryAccess = registryAccess;
            this.biomeKey = biomeKey;
            this.biomes = biomes;

            this.levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
            this.structures = registryAccess.registryOrThrow(Registries.STRUCTURE);
            this.placedFeatures = registryAccess.registryOrThrow(Registries.PLACED_FEATURE);
            this.configuredFeatures = registryAccess.registryOrThrow(Registries.CONFIGURED_FEATURE);

            this.biome = biome;
            this.biomeHolder = biomes.getHolderOrThrow(biomeKey);
        }

        /**
         * Creates a new context object for the passed biome key.
         *
         * @param registryAccess the registry access object
         * @param biomeKey       the biome key
         * @return the context object, or {@code null} if the biome key is not present in the biome registry
         */
        @ApiStatus.Internal
        public static @Nullable Context of(
                @Nullable RegistryAccess registryAccess,
                @NotNull ResourceKey<Biome> biomeKey
        ) {
            if (registryAccess == null) return null;
            var biomes = registryAccess.registryOrThrow(Registries.BIOME);
            return of(registryAccess, biomes, biomeKey);
        }

        /**
         * Creates a new context object for the passed biome key.
         *
         * @param registryAccess the registry access object
         * @param biomeKey       the biome key
         * @param biomes         the biome registry
         * @return the context object, or {@code null} if the biome key is not present in the biome registry
         */
        @ApiStatus.Internal
        @Nullable
        public static Context of(
                @Nullable RegistryAccess registryAccess,
                @Nullable Registry<Biome> biomes,
                @NotNull ResourceKey<Biome> biomeKey

        ) {
            if (biomes == null || registryAccess == null) return null;
            var biome = biomes.get(biomeKey);
            if (biome == null) return null;

            return new Context(registryAccess, biomes, biomeKey, biome);
        }
    }

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
    /**
     * The codec for a predicate class.
     *
     * @return the codec
     */
    @ApiStatus.Internal
    KeyDispatchDataCodec<? extends BiomePredicate> codec();

    /**
     * Tests if the predicate is {@code true} for the passed context.
     *
     * @param ctx the context object
     * @return {@code true} if the predicate does pass the test
     */
    boolean test(Context ctx);
}
