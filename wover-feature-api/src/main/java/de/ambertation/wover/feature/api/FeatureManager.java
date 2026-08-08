package de.ambertation.wover.feature.api;

import de.ambertation.wover.feature.impl.FeatureManagerImpl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;

import org.jetbrains.annotations.NotNull;

/**
 * Helper class for registering custom feature <em>types</em> in
 * {@link net.minecraft.core.registries.BuiltInRegistries#FEATURE_TYPE}.
 * <p>
 * 26.3 reshaped this registry. It used to be {@code BuiltInRegistries.FEATURE}, a
 * {@code Registry<Feature<?>>} holding stateless feature singletons that were paired with a
 * {@code FeatureConfiguration} by a {@code ConfiguredFeature}. In 26.3 a {@link Feature} carries its own
 * configuration, so the registry became {@code FEATURE_TYPE}, a
 * {@code Registry<MapCodec<? extends Feature>>}: you register the type's {@link MapCodec}, and every
 * configured instance is created with {@code new}.
 * <p>
 * The class keeps its name even though it now deals in feature types rather than features, because
 * {@code FeatureTypeManager} would collide confusingly with
 * {@link de.ambertation.wover.feature.api.configured.FeatureContentManager}, which manages the content of the
 * (differently purposed) {@code minecraft:feature} datapack registry.
 */
public class FeatureManager {
    /**
     * Registers a new {@link Feature} type.
     *
     * @param location the location of the feature type
     * @param codec    the codec of the feature type
     * @param <F>      the feature type
     * @return the same codec that was passed in
     */
    public static <F extends Feature> MapCodec<F> register(
            Identifier location,
            MapCodec<F> codec
    ) {
        return FeatureManagerImpl.register(FeatureManagerImpl.createKey(location), codec);
    }

    /**
     * Registers a new {@link Feature} type.
     *
     * @param key   the key of the feature type
     * @param codec the codec of the feature type
     * @param <F>   the feature type
     * @return the same codec that was passed in
     */
    public static <F extends Feature> MapCodec<F> register(
            ResourceKey<MapCodec<? extends Feature>> key,
            MapCodec<F> codec
    ) {
        return FeatureManagerImpl.register(key, codec);
    }

    /**
     * Creates a {@link ResourceKey} for a feature type.
     *
     * @param location the location of the feature type
     * @return the key
     */
    @NotNull
    public static ResourceKey<MapCodec<? extends Feature>> createKey(Identifier location) {
        return FeatureManagerImpl.createKey(location);
    }

    private FeatureManager() {
    }
}
