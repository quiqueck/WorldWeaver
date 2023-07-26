package org.betterx.wover.feature.api.placed;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.builders.FeatureConfigurator;
import org.betterx.wover.feature.impl.configured.InlineBuilderImpl;
import org.betterx.wover.feature.impl.placed.PlacedFeatureManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A key for a {@link PlacedFeature} that can be used to reference the feature as well as
 * use it to place a {@link ConfiguredFeature}.
 */
public class PlacedFeatureKey {
    /**
     * The key for the {@link PlacedFeature} you can use to reference it.
     */
    public final ResourceKey<PlacedFeature> key;

    private GenerationStep.Decoration decoration = GenerationStep.Decoration.SURFACE_STRUCTURES;

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

    public GenerationStep.Decoration getDecoration() {
        return decoration;
    }

    public PlacedFeatureKey setDecoration(GenerationStep.Decoration decoration) {
        this.decoration = decoration;
        return this;
    }

    /**
     * Gets the {@link Holder} for the {@link PlacedFeature} from the given getter.
     *
     * @param getter The getter to get the holder from or {@code null}
     * @return The holder for the {@link PlacedFeature} or {@code null} if it is not present
     */
    @Nullable
    public Holder<PlacedFeature> getHolder(@Nullable HolderGetter<PlacedFeature> getter) {
        return PlacedFeatureManagerImpl.getHolder(getter, key);
    }

    /**
     * Gets the {@link Holder} for the {@link PlacedFeature} from the given getter.
     *
     * <p>
     * This method internally looks up {@link Registries#PLACED_FEATURE}. If you need to retrieve
     * a lot of holders, it is recommended to manually lookup the
     * Registry first and use {@link #getHolder(HolderGetter)} instead.
     *
     * @param context The {@link BootstapContext} to get the holder from
     * @return The holder for the {@link PlacedFeature} or {@code null} if it is not present
     */
    @Nullable
    public Holder<PlacedFeature> getHolder(@NotNull BootstapContext<?> context) {
        return getHolder(context.lookup(Registries.PLACED_FEATURE));
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
         * Places the  {@link ConfiguredFeature} used for creating this instance
         * from the given {@link BootstapContext}.
         * <p>
         * This method internally looks up {@link Registries#CONFIGURED_FEATURE}. If you need to perform
         * a lot of placements, it is recommended to manually lookup the
         * Registry first and use {@link #place(HolderGetter)} instead.
         *
         * @param ctx A {@link BootstapContext} to get the {@link ConfiguredFeature} from
         * @return A {@link FeaturePlacementBuilder} to setup the placement Modifiers
         */
        public FeaturePlacementBuilder place(@NotNull BootstapContext<?> ctx) {
            return this.place(ctx.lookup(Registries.CONFIGURED_FEATURE));
        }

        /**
         * Places the  {@link ConfiguredFeature} used for creating this instance
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

        @Override
        public PlacedFeatureKey.WithConfigured setDecoration(GenerationStep.Decoration decoration) {
            super.setDecoration(decoration);
            return this;
        }
    }
}
