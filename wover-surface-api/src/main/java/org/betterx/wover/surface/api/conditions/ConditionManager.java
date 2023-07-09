package org.betterx.wover.surface.api.conditions;

import org.betterx.wover.surface.impl.conditions.MaterialConditionRegistryImpl;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.NotNull;

/**
 * Helper class for registering custom conditions in
 * {@link net.minecraft.core.registries.BuiltInRegistries#MATERIAL_CONDITION}.
 */
public class ConditionManager {
    /**
     * Registers a condition.
     *
     * @param location the location of the condition
     * @param codec    the codec of the condition
     * @return the new key of the condition
     */
    public static ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> register(
            ResourceLocation location,
            Codec<? extends SurfaceRules.ConditionSource> codec
    ) {
        return MaterialConditionRegistryImpl.register(MaterialConditionRegistryImpl.createKey(location), codec, false);
    }

    /**
     * Registers a condition.
     *
     * @param key   the key of the condition
     * @param codec the codec of the condition
     * @return the same key that was passed in
     */
    public static ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> register(
            ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> key,
            Codec<? extends SurfaceRules.ConditionSource> codec
    ) {
        return MaterialConditionRegistryImpl.register(key, codec, false);
    }

    /**
     * Creates a {@link ResourceKey} for a condition.
     *
     * @param location the location of the condition
     * @return the key
     */
    @NotNull
    public static ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> createKey(ResourceLocation location) {
        return MaterialConditionRegistryImpl.createKey(location);
    }
}
