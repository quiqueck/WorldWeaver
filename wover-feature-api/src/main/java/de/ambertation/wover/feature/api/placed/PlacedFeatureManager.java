package de.ambertation.wover.feature.api.placed;

import de.ambertation.wover.events.api.Event;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.FeatureConfigurator;
import de.ambertation.wover.feature.impl.placed.BoundPlacedFeatureKeyImpl;
import de.ambertation.wover.feature.impl.placed.PlacedFeatureKeyImpl;
import de.ambertation.wover.feature.impl.placed.PlacedFeatureManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
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
     * Creates a {@link PlacedFeatureKey} for the given {@link Identifier}.
     *
     * @param location The location of the {@link PlacedFeature}
     * @return The {@link PlacedFeatureKey}
     */
    public static PlacedFeatureKey createKey(Identifier location) {
        return new PlacedFeatureKeyImpl(location);
    }

    /**
     * Creates a {@link BoundPlacedFeatureKey} for the given {@link Identifier}. The resulting
     * key will place the {@link Feature} referenced by the given {@link ResourceKey}.
     *
     * @param location The location of the {@link PlacedFeature}
     * @param feature  The {@link ResourceKey} of the {@link Feature} to place
     * @return The {@link BoundPlacedFeatureKey}
     */
    public static BoundPlacedFeatureKey
    createKey(
            Identifier location,
            ResourceKey<Feature> feature
    ) {
        return new BoundPlacedFeatureKeyImpl(location, feature);
    }


    /**
     * Creates a {@link BoundPlacedFeatureKey} for the given {@link Identifier}. The resulting
     * key will place the {@link Feature} referenced by the given {@link FeatureKey}.
     *
     * @param location   The location of the {@link PlacedFeature}
     * @param featureKey The {@link FeatureKey} of the {@link Feature} to place
     * @param <B>        The {@link FeatureConfigurator} for the {@link Feature}
     * @return The {@link BoundPlacedFeatureKey}
     */
    public static <B extends FeatureConfigurator> BoundPlacedFeatureKey
    createKey(
            Identifier location,
            FeatureKey<B> featureKey
    ) {
        return new BoundPlacedFeatureKeyImpl(location, featureKey);
    }

    /**
     * Creates a {@link BoundPlacedFeatureKey} with the same location as the passed {@link FeatureKey}.
     * The resulting key will place the {@link Feature} referenced by the given {@link FeatureKey}.
     *
     * @param featureKey The {@link FeatureKey} of the {@link Feature} to place
     * @param <B>        The {@link FeatureConfigurator} for the {@link Feature}
     * @return The {@link BoundPlacedFeatureKey}
     */
    public static <B extends FeatureConfigurator> BoundPlacedFeatureKey
    createKey(
            FeatureKey<B> featureKey
    ) {
        return new BoundPlacedFeatureKeyImpl(featureKey.key.identifier(), featureKey);
    }

    /**
     * Gets the {@link Holder} for a {@link PlacedFeature} from a {@link HolderGetter}.
     *
     * @param getter the getter to get the holder from. You can get this getter from a
     *               {@link net.minecraft.data.worldgen.BootstrapContext} {@code ctx} by
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
     * Gets the {@link Holder} for a {@link PlacedFeature} from a {@link BootstrapContext}.
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
            @Nullable BootstrapContext<?> context,
            @NotNull ResourceKey<PlacedFeature> key
    ) {
        return PlacedFeatureManagerImpl.getHolder(context.lookup(Registries.PLACED_FEATURE), key);
    }

    private PlacedFeatureManager() {
    }
}
