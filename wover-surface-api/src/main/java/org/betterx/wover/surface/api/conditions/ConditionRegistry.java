package org.betterx.wover.surface.api.conditions;

import org.betterx.wover.entrypoint.WoverSurface;
import org.betterx.wover.surface.impl.conditions.MaterialConditionRegistryImpl;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.NotNull;

public class ConditionRegistry {
    public static final ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> THRESHOLD_CONDITION
            = createKey(WoverSurface.C.id("threshold_condition"));
    public static final ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> VOLUME_THRESHOLD_CONDITION
            = createKey(WoverSurface.C.id("volume_threshold_condition"));
    public static final ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> ROUGH_NOISE_CONDITION
            = createKey(WoverSurface.C.id("rough_noise_condition"));

    public static ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> register(
            ResourceLocation location,
            Codec<? extends SurfaceRules.ConditionSource> codec
    ) {
        return MaterialConditionRegistryImpl.register(MaterialConditionRegistryImpl.createKey(location), codec, false);
    }

    public static ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> register(
            ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> key,
            Codec<? extends SurfaceRules.ConditionSource> codec
    ) {
        return MaterialConditionRegistryImpl.register(key, codec, false);
    }

    @NotNull
    public static ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> createKey(ResourceLocation location) {
        return MaterialConditionRegistryImpl.createKey(location);
    }
}
