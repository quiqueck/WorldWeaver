package org.betterx.wover.generator.impl.chunkgenerator;

import org.betterx.wover.entrypoint.WoverWorldGenerator;
import org.betterx.wover.state.api.WorldState;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DimensionsWrapper {
    public static final Codec<DimensionsWrapper> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.unboundedMap(
                                 ResourceKey.codec(Registries.LEVEL_STEM),
                                 ChunkGenerator.CODEC
                         )
                         .optionalFieldOf(WorldGeneratorConfigImpl.TAG_DIMENSIONS, new HashMap<>())
                         .forGetter(o -> o.dimensions),
                    Codec.unboundedMap(
                                 ResourceKey.codec(Registries.LEVEL_STEM),
                                 ResourceKey.codec(Registries.WORLD_PRESET)
                         )
                         .optionalFieldOf(WorldGeneratorConfigImpl.TAG_DIMENSION_PRESETS, new HashMap<>())
                         .forGetter(DimensionsWrapper::getDimensionPresets)
            )
            .apply(instance, DimensionsWrapper::fromCodec));
    final Map<ResourceKey<LevelStem>, ChunkGenerator> dimensions;

    static Map<ResourceKey<LevelStem>, ChunkGenerator> build(Registry<LevelStem> dimensions) {
        Map<ResourceKey<LevelStem>, ChunkGenerator> map = new HashMap<>();
        for (var entry : dimensions.entrySet()) {
            ResourceKey<LevelStem> key = entry.getKey();
            LevelStem stem = entry.getValue();
            map.put(key, stem.generator());
        }
        return map;
    }

    public static @Nullable Registry<LevelStem> getDimensions(ResourceKey<WorldPreset> key) {
        RegistryAccess access = WorldState.allStageRegistryAccess();
        if (access == null) {
            WoverWorldGenerator.C.log.error("No valid registry found!");
            return null;
        }
        final Optional<Holder.Reference<WorldPreset>> preset = access.registryOrThrow(Registries.WORLD_PRESET)
                                                                     .getHolder(key);
        return preset
                .map(worldPresetReference -> worldPresetReference
                        .value()
                        .createWorldDimensions()
                        .dimensions())
                .orElse(null);
    }

    public static @Nullable Registry<LevelStem> getDimensions(RegistryAccess access, ResourceKey<WorldPreset> key) {
        if (access == null) {
            WoverWorldGenerator.C.log.error("No valid registry found!");
            return null;
        }
        var preset = access.registryOrThrow(Registries.WORLD_PRESET).getHolder(key);
        if (preset.isEmpty()) return null;
        return preset
                .get()
                .value()
                .createWorldDimensions()
                .dimensions();
    }

    public static @NotNull Map<ResourceKey<LevelStem>, ChunkGenerator> getDimensionsMap(
            RegistryAccess access,
            ResourceKey<WorldPreset> key
    ) {
        Registry<LevelStem> reg = getDimensions(access, key);
        if (reg == null) return new HashMap<>();
        return DimensionsWrapper.build(reg);
    }

    public static @NotNull Map<ResourceKey<LevelStem>, ChunkGenerator> getDimensionsMap(ResourceKey<WorldPreset> key) {
        Registry<LevelStem> reg = getDimensions(key);
        if (reg == null) return new HashMap<>();
        return DimensionsWrapper.build(reg);
    }

    private static DimensionsWrapper fromCodec(
            Map<ResourceKey<LevelStem>, ChunkGenerator> dimensions,
            Map<ResourceKey<LevelStem>, ResourceKey<WorldPreset>> presets
    ) {
        for (Map.Entry<ResourceKey<LevelStem>, ChunkGenerator> dimEntry : dimensions.entrySet()) {
            final var preset = presets.get(dimEntry.getKey());
            if (preset != null && dimEntry.getValue() instanceof ConfiguredChunkGenerator cfg) {
                cfg.wover_setConfiguredWorldPreset(preset);
            }
        }
        return new DimensionsWrapper(dimensions);
    }


    DimensionsWrapper(Registry<LevelStem> dimensions) {
        this(build(dimensions));
    }

    DimensionsWrapper(Map<ResourceKey<LevelStem>, ChunkGenerator> dimensions) {
        this.dimensions = dimensions;
    }

    public Map<ResourceKey<LevelStem>, ResourceKey<WorldPreset>> getDimensionPresets() {
        final Map<ResourceKey<LevelStem>, ResourceKey<WorldPreset>> map = new HashMap<>();
        for (Map.Entry<ResourceKey<LevelStem>, ChunkGenerator> dimEntry : dimensions.entrySet()) {
            if (dimEntry.getValue() instanceof ConfiguredChunkGenerator cfg
                    && cfg.wover_getConfiguredWorldPreset() != null) {
                map.put(dimEntry.getKey(), cfg.wover_getConfiguredWorldPreset());
            }
        }
        return map;
    }
}
