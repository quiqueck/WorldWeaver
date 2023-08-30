package org.betterx.wover.feature.api.placed;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

/**
 * A key for a {@link PlacedFeature} that can be used to reference the feature as well as
 * use it to place a {@link ConfiguredFeature}.
 * <p>
 * If you do not call {@link #setDecoration(GenerationStep.Decoration)}, the feature is assigned
 * to the {@link GenerationStep.Decoration#VEGETAL_DECORATION} step.
 */
public interface PlacedFeatureKey extends BasePlacedFeatureKey<PlacedFeatureKey> {


    /**
     * Creates a new builder for a {@link PlacedFeature}.
     * <p>
     * When configuration is finished, you should call {@link FeaturePlacementBuilder#register()}
     * to add it to the registry.
     *
     * @param bootstrapContext The {@link BootstapContext} to use
     * @param key              The {@link ResourceKey} for the {@link ConfiguredFeature} to place
     * @return A {@link FeaturePlacementBuilder} to setup the placement Modifiers
     */
    public FeaturePlacementBuilder place(
            BootstapContext<PlacedFeature> bootstrapContext,
            ResourceKey<ConfiguredFeature<?, ?>> key
    );

    /**
     * Creates a new builder for a {@link PlacedFeature}.
     * <p>
     * When configuration is finished, you should call {@link FeaturePlacementBuilder#register()}
     * to add it to the registry.
     *
     * @param bootstrapContext The {@link BootstapContext} to use
     * @param holder           The {@link Holder} for the {@link ConfiguredFeature} to place
     * @return A {@link FeaturePlacementBuilder} to setup the placement Modifiers
     */
    public FeaturePlacementBuilder place(
            BootstapContext<PlacedFeature> bootstrapContext,
            Holder<ConfiguredFeature<?, ?>> holder
    );

    /**
     * Starts a new anonymous (or inline) feature configuration.
     * <p>
     * When you finished configuration a feature, you need to call
     * {@link FeatureConfigurator#inlinePlace()} to get to the placement phase.
     * <p>
     * When the Placement is done, you should call {@link FeaturePlacementBuilder#register()}
     *
     * @param bootstrapContext The {@link BootstapContext} to use
     * @return A {@link ConfiguredFeatureManager.InlineBuilder} start the
     * configuration.
     */
    ConfiguredFeatureManager.InlineBuilder inlineConfiguration(@NotNull BootstapContext<PlacedFeature> bootstrapContext);
}
