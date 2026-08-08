package de.ambertation.wover.surface.impl.conditions;

import de.ambertation.wover.core.api.registry.BuiltInRegistryManager;
import de.ambertation.wover.entrypoint.LibWoverSurface;
import de.ambertation.wover.surface.api.conditions.ConditionManager;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * 26.3 split the material-condition registries in two: {@code minecraft:material_condition_type}
 * ({@link BuiltInRegistries#MATERIAL_CONDITION_TYPE}) still holds the {@link MapCodec}s that dispatch
 * a serialized condition onto its implementation, while the name
 * {@code minecraft:worldgen/material_condition} ({@link net.minecraft.core.registries.Registries#MATERIAL_CONDITION})
 * was reused for a brand new <i>datapack</i> registry that holds the condition sources themselves
 * (that is where vanilla now keeps {@code minecraft:on_floor} and friends).
 * <p>
 * This class registers codecs, so it belongs on the {@code _TYPE} side.
 */
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
        BuiltInRegistryManager.register(BuiltInRegistries.MATERIAL_CONDITION_TYPE, key, codec);

        return key;
    }

    @NotNull
    public static ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> createKey(Identifier location) {
        return ResourceKey.create(
                BuiltInRegistries.MATERIAL_CONDITION_TYPE.key(),
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
