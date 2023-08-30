package org.betterx.wover.feature.api.placed;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator;
import org.betterx.wover.feature.impl.placed.PlacedConfiguredFeatureKeyImpl;
import org.betterx.wover.feature.impl.placed.PlacedFeatureKeyImpl;
import org.betterx.wover.feature.impl.placed.PlacedFeatureManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows you to create {@link PlacedFeatureKey}s and register {@link PlacedFeature}s.
 * A {@link PlacedFeatureKey} is (in general) a wrapper around the {@link ResourceKey} for a {@link PlacedFeature}.
 * <p>
 * {@link PlacedFeatureKey} can also be used to bootstrap a {@link PlacedFeature}. Placed Features
 * should be bootstrapped in the data generator whenever possible. However, if you need to bootstrap a
 * {@link PlacedFeature} in code, you can use the {@link #BOOTSTRAP_PLACED_FEATURES} Event.
 */
public class PlacedFeatureManager {
    /**
     * The event that is fired when the Registry for a {@link PlacedFeature}
     * is being bootstrapped. In general, it is best to generate presets
     * in the data generator whenever possible (see WoverRegistryProvider)
     * for Details.
     */
    public static final Event<OnBootstrapRegistry<PlacedFeature>> BOOTSTRAP_PLACED_FEATURES =
            PlacedFeatureManagerImpl.BOOTSTRAP_PLACED_FEATURES;

    /**
     * Creates a {@link PlacedFeatureKey} for the given {@link ResourceLocation}.
     *
     * @param location The location of the {@link PlacedFeature}
     * @return The {@link PlacedFeatureKey}
     */
    public static PlacedFeatureKey createKey(ResourceLocation location) {
        return new PlacedFeatureKeyImpl(location);
    }

    /**
     * Creates a {@link PlacedFeatureKey} for the given {@link ResourceLocation}. The resulting
     * {@link PlacedFeatureKey} will place the {@link ConfiguredFeature} referenced by the given
     * {@link ResourceKey}.
     *
     * @param location   The location of the {@link PlacedFeature}
     * @param configured The {@link ResourceKey} of the {@link ConfiguredFeature} to place
     * @param <FC>       The {@link FeatureConfiguration} of the {@link ConfiguredFeature}
     * @param <F>        The {@link Feature} of the {@link ConfiguredFeature}
     * @param <B>        The {@link FeatureConfigurator} for the {@link ConfiguredFeature}
     * @return The {@link PlacedFeatureKey}
     */
    public static <FC extends FeatureConfiguration, F extends Feature<FC>, B extends FeatureConfigurator<FC, F>> PlacedConfiguredFeatureKey
    createKey(
            ResourceLocation location,
            ResourceKey<ConfiguredFeature<?, ?>> configured
    ) {
        return new PlacedConfiguredFeatureKeyImpl(location, configured);
    }


    /**
     * Creates a {@link PlacedFeatureKey} for the given {@link ResourceLocation}. The resulting
     * {@link PlacedFeatureKey} will place the {@link ConfiguredFeature} referenced by the given
     * {@link ConfiguredFeatureKey}.
     *
     * @param location             The location of the {@link PlacedFeature}
     * @param configuredFeatureKey The {@link ConfiguredFeatureKey} of the {@link ConfiguredFeature} to place
     * @param <B>                  The {@link FeatureConfigurator} for the {@link ConfiguredFeature}
     * @return The {@link PlacedFeatureKey}
     */
    public static <B extends FeatureConfigurator<?, ?>> PlacedConfiguredFeatureKey
    createKey(
            ResourceLocation location,
            ConfiguredFeatureKey<B> configuredFeatureKey
    ) {
        return new PlacedConfiguredFeatureKeyImpl(location, configuredFeatureKey);
    }

    /**
     * Creates a {@link PlacedFeatureKey} with the same location as the passed {@link ConfiguredFeatureKey}.
     * The resulting {@link PlacedFeatureKey} will place the {@link ConfiguredFeature} referenced by the given
     * {@link ConfiguredFeatureKey}.
     *
     * @param configuredFeatureKey The {@link ConfiguredFeatureKey} of the {@link ConfiguredFeature} to place
     * @param <B>                  The {@link FeatureConfigurator} for the {@link ConfiguredFeature}
     * @return The {@link PlacedFeatureKey}
     */
    public static <B extends FeatureConfigurator<?, ?>> PlacedConfiguredFeatureKey
    createKey(
            ConfiguredFeatureKey<B> configuredFeatureKey
    ) {
        return new PlacedConfiguredFeatureKeyImpl(configuredFeatureKey.key.location(), configuredFeatureKey);
    }

    /**
     * Gets the {@link Holder} for a {@link PlacedFeature} from a {@link HolderGetter}.
     *
     * @param getter the getter to get the holder from. You can get this getter from a
     *               {@link net.minecraft.data.worldgen.BootstapContext} {@code ctx} by
     *               calling {@code ctx.lookup(Registries.PLACED_FEATURE)}
     * @param key    the key to get the holder for
     * @return the holder, or null if the holder is not present
     */
    @Nullable
    public static Holder<PlacedFeature> getHolder(
            @Nullable HolderGetter<PlacedFeature> getter,
            @NotNull ResourceKey<PlacedFeature> key
    ) {
        return PlacedFeatureManagerImpl.getHolder(getter, key);
    }

    /**
     * Gets the {@link Holder} for a {@link PlacedFeature} from a {@link BootstapContext}.
     *
     * @param context the context to get registry containing the holder. When you need to
     *                get multiple holders at a time, you might want to use
     *                {@link #getHolder(HolderGetter, ResourceKey)} instead, as it will
     *                be slightly faster.
     * @param key     the key to get the holder for
     * @return the holder, or null if the holder is not present
     */
    @Nullable
    public static Holder<PlacedFeature> getHolder(
            @Nullable BootstapContext<?> context,
            @NotNull ResourceKey<PlacedFeature> key
    ) {
        return PlacedFeatureManagerImpl.getHolder(context.lookup(Registries.PLACED_FEATURE), key);
    }

    private PlacedFeatureManager() {
    }
}
