package org.betterx.wover.feature.api.placed;

import org.betterx.wover.feature.api.configured.builders.RandomPatch;
import org.betterx.wover.feature.impl.configured.FeatureConfiguratorImpl;
import org.betterx.wover.feature.impl.configured.InlineBuilderImpl;
import org.betterx.wover.feature.impl.configured.RandomPatchImpl;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FeaturePlacementBuilder {
    protected final List<PlacementModifier> modifications = new LinkedList<>();
    @Nullable
    private final ResourceKey<PlacedFeature> key;
    @NotNull
    private final Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder;

    private GenerationStep.Decoration decoration = GenerationStep.Decoration.SURFACE_STRUCTURES;

    //Transitive Members

    @Nullable
    private final ResourceKey<ConfiguredFeature<?, ?>> transitiveConfiguredFeatureKey;

    @Nullable
    private final BiFunction<ResourceKey<ConfiguredFeature<?, ?>>, ResourceKey<PlacedFeature>, RandomPatchImpl> randomPatchBuilder;

    FeaturePlacementBuilder(
            @Nullable ResourceKey<PlacedFeature> key,
            @NotNull Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder
    ) {
        this(key, configuredFeatureHolder, null, null);
    }

    FeaturePlacementBuilder(
            @Nullable ResourceKey<PlacedFeature> key,
            @NotNull Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> transitiveConfiguredFeatureKey,
            @Nullable BiFunction<ResourceKey<ConfiguredFeature<?, ?>>, ResourceKey<PlacedFeature>, RandomPatchImpl> randomPatchBuilder
    ) {
        this.key = key;
        this.configuredFeatureHolder = configuredFeatureHolder;
        this.transitiveConfiguredFeatureKey = transitiveConfiguredFeatureKey;
        this.randomPatchBuilder = randomPatchBuilder;
    }

    @ApiStatus.Internal
    public static FeaturePlacementBuilder withTransitive(
            FeatureConfiguratorImpl<?, ?> configuredFeatureBuilder,
            BiFunction<ResourceKey<ConfiguredFeature<?, ?>>, ResourceKey<PlacedFeature>, RandomPatchImpl> randomPatchBuilder
    ) {
        return new FeaturePlacementBuilder(
                configuredFeatureBuilder.getTransitiveFeatureKey(),
                configuredFeatureBuilder.directHolder(),
                configuredFeatureBuilder.key,
                randomPatchBuilder
        );
    }

    /**
     * Randomize the xz-Coordinates
     *
     * @return same instance.
     */
    public FeaturePlacementBuilder squarePlacement() {
        return modifier(InSquarePlacement.spread());
    }

    /**
     * Only place the feature if the block at the position is air.
     *
     * @return same instance.
     */
    public FeaturePlacementBuilder isEmpty() {
        return modifier(BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE));
    }

    /**
     * Add feature placement modifier.
     * <p>
     * Modifiers are applied in the order they are added.
     * They are used to filter and repostion the possible feature locations.
     *
     * @param modifiers {@link PlacementModifier}s to add.
     * @return the same builder.
     */
    public FeaturePlacementBuilder modifier(PlacementModifier... modifiers) {
        for (var m : modifiers)
            modifications.add(m);
        return this;
    }

    public RandomPatch inRandomPatch() {
        final RandomPatch randomPatch;
        if (randomPatchBuilder != null) {
            randomPatch = randomPatchBuilder.apply(transitiveConfiguredFeatureKey, key);
        } else {
            randomPatch = new InlineBuilderImpl(this.key).randomPatch();
        }

        return randomPatch.featureToPlace(directHolder());
    }


    public Holder<PlacedFeature> register(BootstapContext<PlacedFeature> context) {
        if (key == null) {
            throw new IllegalStateException("A ResourceKey can not be null if it should be registered!");
        }
        PlacedFeature feature = build();
        return context.register(key, feature);
    }

    public Holder<PlacedFeature> directHolder() {
        return Holder.direct(build());
    }


    @NotNull
    private PlacedFeature build() {
        return new PlacedFeature(configuredFeatureHolder, modifications);
    }

    public GenerationStep.Decoration getDecoration() {
        return decoration;
    }

    public void setDecoration(GenerationStep.Decoration decoration) {
        this.decoration = decoration;
    }
}
