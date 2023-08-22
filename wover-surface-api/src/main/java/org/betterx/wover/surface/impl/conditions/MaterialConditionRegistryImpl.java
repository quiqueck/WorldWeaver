package org.betterx.wover.surface.impl.conditions;

import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.entrypoint.WoverSurface;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.surface.api.conditions.ConditionManager;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class MaterialConditionRegistryImpl {
    public static final ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> THRESHOLD_CONDITION
            = ConditionManager.createKey(WoverSurface.C.id("threshold_condition"));
    public static final ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> VOLUME_THRESHOLD_CONDITION
            = ConditionManager.createKey(WoverSurface.C.id("volume_threshold_condition"));
    public static final ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> ROUGH_NOISE_CONDITION
            = ConditionManager.createKey(WoverSurface.C.id("rough_noise_condition"));

    public static ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> register(
            ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> key,
            Codec<? extends SurfaceRules.ConditionSource> codec,
            boolean withBCLibLegacy
    ) {
        BuiltInRegistryManager.register(BuiltInRegistries.MATERIAL_CONDITION, key, codec);
        if (withBCLibLegacy) {
            BuiltInRegistryManager.register(
                    BuiltInRegistries.MATERIAL_CONDITION,
                    LegacyHelper.BCLIB_CORE.convertNamespace(key.location()),
                    LegacyHelper.wrap(codec)
            );
        }

        return key;
    }

    @NotNull
    public static ResourceKey<Codec<? extends SurfaceRules.ConditionSource>> createKey(ResourceLocation location) {
        return ResourceKey.create(
                BuiltInRegistries.MATERIAL_CONDITION.key(),
                location
        );
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        register(THRESHOLD_CONDITION, ThresholdConditionImpl.CODEC, true);
        register(VOLUME_THRESHOLD_CONDITION, VolumeThresholdConditionImpl.CODEC, true);
        register(ROUGH_NOISE_CONDITION, RoughNoiseConditionImpl.CODEC, true);
    }
}
