package org.betterx.wover.feature.impl;

import org.betterx.wover.legacy.api.LegacyHelper;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;

import org.jetbrains.annotations.NotNull;

public class FeatureManagerImpl {
    public static ResourceKey<Feature<?>> register(
            @NotNull ResourceKey<Feature<?>> key,
            @NotNull Feature<?> feature,
            boolean withBCLibLegacy
    ) {
        Registry.register(BuiltInRegistries.FEATURE, key, feature);
        if (withBCLibLegacy) {
            Registry.register(
                    BuiltInRegistries.FEATURE,
                    LegacyHelper.BCLIB_CORE.convertNamespace(key.location()),
                    feature
            );
        }

        return key;
    }

    @NotNull
    public static ResourceKey<Feature<?>> createKey(ResourceLocation location) {
        return ResourceKey.create(
                BuiltInRegistries.FEATURE.key(),
                location
        );
    }
}
