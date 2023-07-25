package org.betterx.wover.feature.api;

import org.betterx.wover.feature.impl.FeatureManagerImpl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;

import org.jetbrains.annotations.NotNull;

/**
 * Helper class for registering custom features in
 * {@link net.minecraft.core.registries.BuiltInRegistries#FEATURE}.
 */
public class FeatureManager {
    /**
     * Registers a new {@link Feature}.
     *
     * @param location the location of the feature
     * @param feature  the feature
     * @return the new key of the feature
     */
    public static ResourceKey<Feature<?>> register(
            ResourceLocation location,
            Feature<?> feature
    ) {
        return FeatureManagerImpl.register(FeatureManagerImpl.createKey(location), feature, false);
    }


    /**
     * Registers a new {@link Feature}.
     *
     * @param key     the key of the feature
     * @param feature the feature
     * @return the same key that was passed in
     */
    public static ResourceKey<Feature<?>> register(
            ResourceKey<Feature<?>> key,
            Feature<?> feature
    ) {
        return FeatureManagerImpl.register(key, feature, false);
    }

    /**
     * Creates a {@link ResourceKey} for a feature.
     *
     * @param location the location of the feature
     * @return the key
     */
    @NotNull
    public static ResourceKey<Feature<?>> createKey(ResourceLocation location) {
        return FeatureManagerImpl.createKey(location);
    }
}
