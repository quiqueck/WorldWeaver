package com.betterxlib.api.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating and registering world generation features.
 * <p>
 * Example usage:
 * <pre>{@code
 * // Create resource keys
 * ResourceKey<ConfiguredFeature<?, ?>> CRYSTAL_FEATURE = FeatureBuilder.configuredKey("mymod", "crystal");
 * ResourceKey<PlacedFeature> CRYSTAL_PLACED = FeatureBuilder.placedKey("mymod", "crystal");
 *
 * // In data generation
 * FeatureBuilder.configuredFeature(context)
 *     .key(CRYSTAL_FEATURE)
 *     .feature(Feature.BLOCK_COLUMN)
 *     .config(new BlockColumnConfiguration(...))
 *     .register();
 *
 * FeatureBuilder.placedFeature(context)
 *     .key(CRYSTAL_PLACED)
 *     .configured(CRYSTAL_FEATURE)
 *     .count(10)
 *     .inSquare()
 *     .heightmap(Heightmap.Types.MOTION_BLOCKING)
 *     .biomeFilter()
 *     .register();
 * }</pre>
 */
public class FeatureBuilder {

    private FeatureBuilder() {}

    /**
     * Create a resource key for a configured feature.
     *
     * @param modId the mod ID
     * @param name the feature name
     * @return the resource key
     */
    public static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String modId, String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(modId, name));
    }

    /**
     * Create a resource key for a placed feature.
     *
     * @param modId the mod ID
     * @param name the feature name
     * @return the resource key
     */
    public static ResourceKey<PlacedFeature> placedKey(String modId, String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(modId, name));
    }

    /**
     * Start building a configured feature.
     *
     * @param context the bootstrap context
     * @return a new ConfiguredFeatureBuilder
     */
    public static ConfiguredFeatureBuilder configuredFeature(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        return new ConfiguredFeatureBuilder(context);
    }

    /**
     * Start building a placed feature.
     *
     * @param context the bootstrap context
     * @return a new PlacedFeatureBuilder
     */
    public static PlacedFeatureBuilder placedFeature(BootstrapContext<PlacedFeature> context) {
        return new PlacedFeatureBuilder(context);
    }

    /**
     * Builder for configured features.
     */
    public static class ConfiguredFeatureBuilder {
        private final BootstrapContext<ConfiguredFeature<?, ?>> context;
        private ResourceKey<ConfiguredFeature<?, ?>> key;
        private Feature<?> feature;
        private FeatureConfiguration config;

        ConfiguredFeatureBuilder(BootstrapContext<ConfiguredFeature<?, ?>> context) {
            this.context = context;
        }

        public ConfiguredFeatureBuilder key(ResourceKey<ConfiguredFeature<?, ?>> key) {
            this.key = key;
            return this;
        }

        public <FC extends FeatureConfiguration> ConfiguredFeatureBuilder feature(Feature<FC> feature) {
            this.feature = feature;
            return this;
        }

        public ConfiguredFeatureBuilder config(FeatureConfiguration config) {
            this.config = config;
            return this;
        }

        @SuppressWarnings("unchecked")
        public void register() {
            if (key == null) throw new IllegalStateException("Key not set");
            if (feature == null) throw new IllegalStateException("Feature not set");
            if (config == null) throw new IllegalStateException("Config not set");

            context.register(key, new ConfiguredFeature<>(
                (Feature<FeatureConfiguration>) feature,
                config
            ));
        }
    }

    /**
     * Builder for placed features.
     */
    public static class PlacedFeatureBuilder {
        private final BootstrapContext<PlacedFeature> context;
        private ResourceKey<PlacedFeature> key;
        private ResourceKey<ConfiguredFeature<?, ?>> configuredKey;
        private Holder<ConfiguredFeature<?, ?>> configuredHolder;
        private final List<PlacementModifier> modifiers = new ArrayList<>();

        PlacedFeatureBuilder(BootstrapContext<PlacedFeature> context) {
            this.context = context;
        }

        public PlacedFeatureBuilder key(ResourceKey<PlacedFeature> key) {
            this.key = key;
            return this;
        }

        public PlacedFeatureBuilder configured(ResourceKey<ConfiguredFeature<?, ?>> configuredKey) {
            this.configuredKey = configuredKey;
            return this;
        }

        public PlacedFeatureBuilder configured(Holder<ConfiguredFeature<?, ?>> holder) {
            this.configuredHolder = holder;
            return this;
        }

        public PlacedFeatureBuilder modifier(PlacementModifier modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        public PlacedFeatureBuilder modifiers(PlacementModifier... modifiers) {
            this.modifiers.addAll(List.of(modifiers));
            return this;
        }

        public PlacedFeatureBuilder modifiers(List<PlacementModifier> modifiers) {
            this.modifiers.addAll(modifiers);
            return this;
        }

        // Common placement modifier shortcuts

        public PlacedFeatureBuilder count(int count) {
            return modifier(net.minecraft.world.level.levelgen.placement.CountPlacement.of(count));
        }

        public PlacedFeatureBuilder rarity(int rarity) {
            return modifier(net.minecraft.world.level.levelgen.placement.RarityFilter.onAverageOnceEvery(rarity));
        }

        public PlacedFeatureBuilder inSquare() {
            return modifier(net.minecraft.world.level.levelgen.placement.InSquarePlacement.spread());
        }

        public PlacedFeatureBuilder heightmap(net.minecraft.world.level.levelgen.Heightmap.Types type) {
            return modifier(net.minecraft.world.level.levelgen.placement.HeightmapPlacement.onHeightmap(type));
        }

        public PlacedFeatureBuilder biomeFilter() {
            return modifier(net.minecraft.world.level.levelgen.placement.BiomeFilter.biome());
        }

        public PlacedFeatureBuilder heightRange(int minY, int maxY) {
            return modifier(net.minecraft.world.level.levelgen.placement.HeightRangePlacement.uniform(
                net.minecraft.world.level.levelgen.VerticalAnchor.absolute(minY),
                net.minecraft.world.level.levelgen.VerticalAnchor.absolute(maxY)
            ));
        }

        public void register() {
            if (key == null) throw new IllegalStateException("Key not set");

            Holder<ConfiguredFeature<?, ?>> holder;
            if (configuredHolder != null) {
                holder = configuredHolder;
            } else if (configuredKey != null) {
                holder = context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configuredKey);
            } else {
                throw new IllegalStateException("Configured feature not set");
            }

            context.register(key, new PlacedFeature(holder, List.copyOf(modifiers)));
        }
    }
}
