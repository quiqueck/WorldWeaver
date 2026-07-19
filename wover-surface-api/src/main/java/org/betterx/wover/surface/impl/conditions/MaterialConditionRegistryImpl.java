package org.betterx.wover.surface.impl.conditions;

import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.entrypoint.LibWoverSurface;
import org.betterx.wover.surface.api.conditions.ConditionManager;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class MaterialConditionRegistryImpl {
    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> THRESHOLD_CONDITION
            = ConditionManager.createKey(LibWoverSurface.C.id("threshold_condition"));
    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> VOLUME_THRESHOLD_CONDITION
            = ConditionManager.createKey(LibWoverSurface.C.id("volume_threshold_condition"));
    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> ROUGH_NOISE_CONDITION
            = ConditionManager.createKey(LibWoverSurface.C.id("rough_noise_condition"));

    public static ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> register(
            ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> key,
            MapCodec<? extends SurfaceRules.ConditionSource> codec
    ) {
        BuiltInRegistryManager.register(BuiltInRegistries.MATERIAL_CONDITION, key, codec);

        return key;
    }

    @NotNull
    public static ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> createKey(ResourceLocation location) {
        return ResourceKey.create(
                BuiltInRegistries.MATERIAL_CONDITION.key(),
                location
        );
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        register(THRESHOLD_CONDITION, ThresholdConditionImpl.CODEC);
        register(VOLUME_THRESHOLD_CONDITION, VolumeThresholdConditionImpl.CODEC);
        register(ROUGH_NOISE_CONDITION, RoughNoiseConditionImpl.CODEC);
    }
}
