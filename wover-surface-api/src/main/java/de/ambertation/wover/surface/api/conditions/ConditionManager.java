package de.ambertation.wover.surface.api.conditions;

import de.ambertation.wover.surface.impl.conditions.MaterialConditionRegistryImpl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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
    public static ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> register(
            Identifier location,
            MapCodec<? extends SurfaceRules.ConditionSource> codec
    ) {
        return MaterialConditionRegistryImpl.register(MaterialConditionRegistryImpl.createKey(location), codec);
    }

    /**
     * Registers a condition.
     *
     * @param key   the key of the condition
     * @param codec the codec of the condition
     * @return the same key that was passed in
     */
    public static ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> register(
            ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> key,
            MapCodec<? extends SurfaceRules.ConditionSource> codec
    ) {
        return MaterialConditionRegistryImpl.register(key, codec);
    }

    /**
     * Creates a {@link ResourceKey} for a condition.
     *
     * @param location the location of the condition
     * @return the key
     */
    @NotNull
    public static ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> createKey(Identifier location) {
        return MaterialConditionRegistryImpl.createKey(location);
    }
}
