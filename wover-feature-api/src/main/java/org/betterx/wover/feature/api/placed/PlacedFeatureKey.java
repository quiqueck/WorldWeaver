package org.betterx.wover.feature.api.placed;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.builders.FeatureConfigurator;
import org.betterx.wover.feature.impl.configured.InlineBuilderImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A key for a {@link PlacedFeature} that can be used to reference the feature as well as
 * use it to place a {@link ConfiguredFeature}.
 */
public class PlacedFeatureKey {
    /**
     * The key for the {@link PlacedFeature} you can use to reference it.
     */

    public final ResourceKey<PlacedFeature> key;

    /**
     * Creates a new {@link PlacedFeatureKey} from the given id.
     *
     * @param featureId The id of the {@link PlacedFeature}
     */
    PlacedFeatureKey(ResourceLocation featureId) {
        this.key = ResourceKey.create(Registries.PLACED_FEATURE, featureId);
    }

    /**
     * Creates a new builder for a {@link PlacedFeature}.
     * <p>
     * When configuration is finished, you should call {@link FeaturePlacementBuilder#register(BootstapContext)}
     * to add it to the registry.
     *
     * @param holder The {@link Holder} for the {@link ConfiguredFeature} to place
     * @return A {@link FeaturePlacementBuilder} to setup the placement Modifiers
     */
    public FeaturePlacementBuilder place(Holder<ConfiguredFeature<?, ?>> holder) {
        return new FeaturePlacementBuilder(key, holder);
    }

    /**
     * Starts a new anonymous (or inline) feature configuration.
     * <p>
     * When you finished configuration a feature, you need to call
     * {@link FeatureConfigurator#inlinePlace()} to get to the placement phase.
     * <p>
     * When the Placement is done, you should call {@link FeaturePlacementBuilder#register(BootstapContext)}
     *
     * @return A {@link ConfiguredFeatureManager.InlineBuilder} start the
     * configuration.
     */
    public ConfiguredFeatureManager.InlineBuilder inlineConfiguration() {
        return new InlineBuilderImpl(this.key);
    }

    /**
     * Creates a new {@link PlacedFeatureKey} that will place a {@link ConfiguredFeature}.
     */
    public static class WithConfigured extends PlacedFeatureKey {
        @FunctionalInterface
        private interface HolderProvider {
            Holder<ConfiguredFeature<?, ?>> getHolder(@NotNull HolderGetter<ConfiguredFeature<?, ?>> getter);
        }

        @NotNull
        private final HolderProvider holderProvider;

        /**
         * Creates a new {@link PlacedFeatureKey} that places a  {@link ConfiguredFeature}.
         *
         * @param featureId The id of the {@link PlacedFeature}
         * @param linked    The {@link ConfiguredFeatureKey} to place
         */
        WithConfigured(ResourceLocation featureId, ConfiguredFeatureKey<?> linked) {
            super(featureId);
            this.holderProvider = linked::getHolder;
        }

        /**
         * Creates a new {@link PlacedFeatureKey} that places a {@link ConfiguredFeature}.
         * <p>
         * This is mostly used to place vanilla Features of Features from other Mods
         *
         * @param featureId The id of the {@link PlacedFeature}
         * @param linked    The {@link ConfiguredFeature} to place
         */
        WithConfigured(ResourceLocation featureId, ResourceKey<ConfiguredFeature<?, ?>> linked) {
            super(featureId);
            this.holderProvider = (getter) -> ConfiguredFeatureManager.getHolder(getter, linked);
        }

        /**
         * Places the  {@link ConfiguredFeature} used for creting this instance
         * from the given {@link HolderGetter}.
         * <p>
         * When configuration is finished, you should call {@link FeaturePlacementBuilder#register(BootstapContext)}
         * to add it to the registry.
         *
         * @param getter A {@link HolderGetter} for {@link ConfiguredFeature}s
         * @return A {@link FeaturePlacementBuilder} to setup the placement Modifiers
         */
        public FeaturePlacementBuilder place(@NotNull HolderGetter<ConfiguredFeature<?, ?>> getter) {
            return super.place(holderProvider.getHolder(getter));
        }

        /**
         * This method is not supported for {@link PlacedFeatureKey.WithConfigured}.
         * Use {@link #place(HolderGetter)} instead.
         *
         * @param holder the holder for the {@link ConfiguredFeature}
         * @return Nothing, this method always throws an {@link UnsupportedOperationException}.
         */
        @Override
        @Contract(value = "_ -> fail", pure = true)
        public FeaturePlacementBuilder place(Holder<ConfiguredFeature<?, ?>> holder) {
            throw new UnsupportedOperationException(
                    "Cannot manually select a holder, when Placement is linked to a Configured Feature. (" + key.location() + ")");
        }

        /**
         * This method is not supported for {@link PlacedFeatureKey.WithConfigured}.
         * Use {@link #place(HolderGetter)} instead.
         *
         * @return Nothing, this method always throws an {@link UnsupportedOperationException}.
         */
        @Override
        public ConfiguredFeatureManager.InlineBuilder inlineConfiguration() {
            throw new UnsupportedOperationException(
                    "Cannot use an inline configuration, when Placement is linked to a Configured Feature. (" + key.location() + ")");
        }
    }
}
