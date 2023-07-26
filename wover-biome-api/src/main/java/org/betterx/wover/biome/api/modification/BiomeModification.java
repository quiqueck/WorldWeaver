package org.betterx.wover.biome.api.modification;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.biome.impl.modification.BiomeModificationImpl;
import org.betterx.wover.biome.impl.modification.FeatureMap;
import org.betterx.wover.biome.impl.modification.GenerationSettingsWorker;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

/**
 * Biome Modifications are used to modify some aspects of a Biome after a world was loaded.
 * <p>
 * The Modification API uses the {@link org.betterx.wover.events.api.WorldLifecycle#MINECRAFT_SERVER_READY}
 * event to inject the modifications into the world.
 * <p>
 * Modifications are currently injected by directly altering some constant fields in
 * {@link net.minecraft.world.level.biome.BiomeGenerationSettings}
 */
public interface BiomeModification {
    /**
     * The codec for a {@link BiomeModification}
     */
    Codec<BiomeModification> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomePredicate.CODEC.fieldOf("predicate").forGetter(BiomeModification::predicate),
                    FeatureMap.CODEC.fieldOf("features")
                                    .orElse(new ArrayList<>())
                                    .forGetter(BiomeModification::features)
            ).apply(instance, BiomeModificationImpl::new)
    );

    /**
     * A predicate that determines if this modification should be applied to a biome.
     *
     * @return The predicate.
     */
    BiomePredicate predicate();

    /**
     * The features that should be added to the biome.
     * The index represents a {@link GenerationStep.Decoration}.
     *
     * @return The features.
     */
    List<List<Holder<PlacedFeature>>> features();

    /**
     * For <b>internal</b> use only! Called when the modification should be applied to a biome.
     *
     * @param worker The worker that can be used to apply the modification to the biome
     */
    @ApiStatus.Internal
    void apply(GenerationSettingsWorker worker);

    /**
     * Creates a new {@link Builder} for a {@link BiomeModification}.
     *
     * @param location The location of the modification.
     * @return The builder.
     */
    static Builder build(ResourceLocation location) {
        return new Builder(ResourceKey.create(BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY, location));
    }

    /**
     * Creates a new {@link Builder} for a {@link BiomeModification}.
     *
     * @param key The key of the modification.
     * @return The builder.
     */
    static Builder build(ResourceKey<BiomeModification> key) {
        return new Builder(key);
    }

    /**
     * A builder for {@link BiomeModification}s.
     * <p>
     * When the modification is finished, it can be registered using {@link #register(BootstapContext)}.
     * You can also build a {@link Holder.Direct} using {@link #build()}.
     */
    final class Builder {
        private BiomePredicate predicate;
        private final FeatureMap features;

        private final ResourceKey<BiomeModification> key;

        private Builder(ResourceKey<BiomeModification> key) {
            this.key = key;
            this.predicate = BiomePredicate.always();
            this.features = FeatureMap.of(new ArrayList<>(GenerationStep.Decoration.values().length));
        }

        /**
         * Sets the predicate for this modification.
         *
         * @param p The predicate.
         * @return This builder.
         */
        public Builder predicate(BiomePredicate p) {
            this.predicate = p;
            return this;
        }

        /**
         * Sets a predicate that tests if a biome matches the passed key.
         *
         * @param key The key of the biome.
         * @return This builder.
         * @see BiomePredicate#isBiome(ResourceKey)
         */
        public Builder isBiome(ResourceKey<Biome> key) {
            predicate(BiomePredicate.isBiome(key));
            return this;
        }

        /**
         * Sets a predicate that tests if a biome matches any of the passed keys.
         *
         * @param keys The keys of the biomes.
         * @return This builder.
         * @see BiomePredicate#inBiomes(ResourceKey[])
         */
        @SafeVarargs
        public final Builder inBiomes(ResourceKey<Biome>... keys) {
            return predicate(BiomePredicate.inBiomes(keys));
        }


        /**
         * Sets a predicate that will return {@code true} when the biome does not match any of the passed keys.
         *
         * @param keys The keys of the biomes.
         * @return This builder.
         * @see BiomePredicate#notInBiomes(ResourceKey[])
         */
        @SafeVarargs
        public final Builder notInBiomes(ResourceKey<Biome>... keys) {
            return predicate(BiomePredicate.notInBiomes(keys));
        }

        /**
         * Sets a predicate that tests if a biome is in the passed dimension.
         *
         * @param key The key of the dimension.
         * @return This builder.
         * @see BiomePredicate#inDimension(ResourceKey)
         */
        public Builder inDimension(ResourceKey<LevelStem> key) {
            return predicate(BiomePredicate.inDimension(key));
        }

        /**
         * Sets a predicate that tests if a biome is in the overworld.
         *
         * @return This builder.
         * @see BiomePredicate#inOverworld()
         */
        public Builder inOverworld() {
            return predicate(BiomePredicate.inOverworld());
        }

        /**
         * Sets a predicate that tests if a biome is in the end.
         *
         * @return This builder.
         * @see BiomePredicate#inEnd()
         */
        public Builder inEnd() {
            return predicate(BiomePredicate.inEnd());
        }

        /**
         * Sets a predicate that tests if a biome is in the nether.
         *
         * @return This builder.
         * @see BiomePredicate#inNether()
         */
        public Builder inNether() {
            return predicate(BiomePredicate.inNether());
        }

        /**
         * Sets a predicate that tests if a biome is in the specified namespace.
         *
         * @param tag The tag.
         * @return This builder.
         * @see BiomePredicate#hasTag(TagKey)
         **/
        public Builder hasTag(TagKey<Biome> tag) {
            return predicate(BiomePredicate.hasTag(tag));
        }


        /**
         * Sets a predicate that tests if an entity can spawn in the biome
         *
         * @param type The entity type
         * @return This builder.
         * @see BiomePredicate#spawns(EntityType)
         */
        public Builder spawns(EntityType<?> type) {
            return predicate(BiomePredicate.spawns(type));
        }

        /**
         * Sets a predicate that tests if the passed structure can generate in the biome.
         *
         * @param key The key of the structure.
         * @return This builder.
         * @see BiomePredicate#hasStructure(ResourceKey)
         */
        public Builder hasStructure(ResourceKey<Structure> key) {
            return predicate(BiomePredicate.hasStructure(key));
        }

        /**
         * Sets a predicate that tests if the passed placed feature can generate in the biome.
         *
         * @param key The key of the feature.
         * @return This builder.
         * @see BiomePredicate#hasPlacedFeature(ResourceKey)
         */
        public Builder hasPlacedFeature(ResourceKey<PlacedFeature> key) {
            return predicate(BiomePredicate.hasPlacedFeature(key));
        }

        /**
         * Sets a predicate that tests if the passed configured feature can generate in the biome.
         *
         * @param key The key of the feature.
         * @return This builder.
         * @see BiomePredicate#hasConfiguredFeature(ResourceKey)
         */
        public Builder hasConfiguredFeature(ResourceKey<ConfiguredFeature<?, ?>> key) {
            return predicate(BiomePredicate.hasConfiguredFeature(key));
        }

        /**
         * Sets a predicate that is {@code true} when any one of the passed predicates is {@code true}.
         * <p>
         * This predicate is lazy, meaning that it will stop testing predicates once one of them returns
         * {@code true}.
         *
         * @param predicates The predicates.
         * @return This builder.
         * @see BiomePredicate#or(BiomePredicate...)
         */
        public Builder anyOf(BiomePredicate... predicates) {
            return predicate(BiomePredicate.or(predicates));
        }

        /**
         * Sets a predicate that is {@code true} when all of the passed predicates are {@code true}.
         * <p>
         * This predicate is lazy, meaning that it will stop testing predicates once one of them returns
         * {@code false}.
         *
         * @param predicates The predicates.
         * @return This builder.
         * @see BiomePredicate#and(BiomePredicate...)
         */
        public Builder allOf(BiomePredicate... predicates) {
            return predicate(BiomePredicate.and(predicates));
        }

        /**
         * Sets  a predicate that negates the pass predicate.
         *
         * @param predicate The predicate to negate
         * @return This builder.
         * @see BiomePredicate#not(BiomePredicate)
         */
        public Builder not(BiomePredicate predicate) {
            return predicate(BiomePredicate.not(predicate));
        }

        /**
         * Sets a predicate that tests if the biome is in the vanilla (<i>minecraft</i>) namespace.
         *
         * @return This builder.
         * @see BiomePredicate#isVanilla()
         */
        public Builder isVanilla() {
            return predicate(BiomePredicate.isVanilla());
        }

        /**
         * Sets a predicate that tests if a biome is in the specified namespace.
         *
         * @param namespace The namespace.
         * @return This builder.
         * @see BiomePredicate#inNamespace(String)
         */
        public Builder inNamespace(String namespace) {
            return predicate(BiomePredicate.inNamespace(namespace));
        }


        /**
         * Sets a predicate that tests if a biome is in the specified namespace.
         *
         * @param core The namespace.
         * @return This builder.
         * @see BiomePredicate#inNamespace(ModCore)
         */
        public Builder inNamespace(ModCore core) {
            return predicate(BiomePredicate.inNamespace(core));
        }

        /**
         * Sets a predicate that tests if a biome is not in the specified namespace.
         *
         * @param namespace The namespace.
         * @return This builder.
         * @see BiomePredicate#notInNamespace(String)
         */
        public Builder notInNamespace(String namespace) {
            return predicate(BiomePredicate.notInNamespace(namespace));
        }

        /**
         * Sets a predicate that tests if a biome is not in the specified namespace.
         *
         * @param core The namespace.
         * @return This builder.
         * @see BiomePredicate#notInNamespace(ModCore)
         */
        public Builder notInNamespace(ModCore core) {
            return predicate(BiomePredicate.inNamespace(core));
        }

        /**
         * Adds a feature to the modification. This feature will be added to the {@link GenerationStep.Decoration}
         * of all Biomes that match the {@link #predicate}.
         *
         * @param decoration The decoration step.
         * @param holder     The holder of the feature.
         * @return This builder.
         */
        public Builder addFeature(
                GenerationStep.Decoration decoration,
                Holder<PlacedFeature> holder
        ) {
            this.features.getFeatures(decoration).add(holder);
            return this;
        }

        /**
         * Adds a feature to the modification. This feature will be added to the {@link GenerationStep.Decoration}
         * specified in the PlacedFeatureKey of all Biomes that match the {@link #predicate}.
         * <p>
         * Internally this will  call {@link #addFeature(GenerationStep.Decoration, Holder)}. The
         * Holder Object is created using {@link PlacedFeatureKey#getHolder(BootstapContext)}.
         * If you want to add a lot of features, it is recommended to use
         * {@link #addFeature(GenerationStep.Decoration, Holder)}
         * instead, as it will be faster.
         *
         * @param ctx     The bootstrap context.
         * @param feature The feature.
         * @return This builder.
         */
        public Builder addFeature(BootstapContext<?> ctx, PlacedFeatureKey feature) {
            return this.addFeature(feature.getDecoration(), feature.getHolder(ctx));
        }

        /**
         * Creates a {@link Holder.Direct} for the {@link BiomeModification}.
         *
         * @return The holder.
         */
        public Holder<BiomeModification> build() {
            return Holder.direct(new BiomeModificationImpl(predicate, features.generic()));
        }

        /**
         * Registers the {@link BiomeModification} to the {@link BiomeModificationRegistry}.
         *
         * @param ctx The bootstrap context of the registry
         * @return The holder created in the registry
         */
        public Holder<BiomeModification> register(BootstapContext<BiomeModification> ctx) {
            return ctx.register(key, new BiomeModificationImpl(predicate, features.generic()));
        }
    }
}
