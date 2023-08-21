package org.betterx.wover.generator.impl.chunkgenerator;

import org.betterx.wover.entrypoint.WoverWorldGenerator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class DimensionsWrapper {
    public static final Codec<DimensionsWrapper> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.unboundedMap(
                                ResourceKey.codec(Registries.LEVEL_STEM),
                                ChunkGenerator.CODEC
                        )
                        .optionalFieldOf("dimensions", new HashMap<>())
                        .forGetter(o -> o.dimensions))
            .apply(instance, DimensionsWrapper::new));
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

    static Map<ResourceKey<LevelStem>, ChunkGenerator> build(Map<ResourceKey<LevelStem>, LevelStem> input) {
        Map<ResourceKey<LevelStem>, ChunkGenerator> map = new HashMap<>();
        for (var entry : input.entrySet()) {
            ResourceKey<LevelStem> key = entry.getKey();
            LevelStem stem = entry.getValue();
            map.put(key, stem.generator());
        }
        return map;
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


    DimensionsWrapper(Registry<LevelStem> dimensions) {
        this(build(dimensions));
    }

    DimensionsWrapper(Map<ResourceKey<LevelStem>, ChunkGenerator> dimensions) {
        this.dimensions = dimensions;
    }
}
