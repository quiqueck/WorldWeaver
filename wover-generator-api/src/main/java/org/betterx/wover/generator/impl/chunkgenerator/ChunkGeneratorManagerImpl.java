package org.betterx.wover.generator.impl.chunkgenerator;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.generator.api.chunkgenerator.WoverChunkGenerator;
import org.betterx.wover.legacy.api.LegacyHelper;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;

public class ChunkGeneratorManagerImpl {
    private static final ResourceLocation LEGACY_ID = LegacyHelper.BCLIB_CORE.convertNamespace(WoverChunkGenerator.ID);

    private static final List<String> GENERATOR_IDS = new ArrayList<>(1);

    /**
     * We need this for mods that use the DSL system to actually fix the generator data.
     * The DSL will check if the generator type is actually known, so we need to inject
     * our own generator into the DSL system.
     *
     * @param map A DSL map, that could contain anything. The Method checks if it contains
     *            the flat generator ("minecraft:flat") and if so, it will inject the
     *            Wover generator into the map
     * @return The altered map
     */
    @ApiStatus.Internal
    public static Map<String, Supplier<TypeTemplate>> addGeneratorDSL(Map<String, Supplier<TypeTemplate>> map) {
        if (map.containsKey("minecraft:flat") && !ModCore.isDatagen()) {
            Map<String, Supplier<TypeTemplate>> nMap = new HashMap<>(map);
            GENERATOR_IDS.forEach(id -> nMap.put(id, DSL::remainder));
            return ImmutableMap.copyOf(nMap);
        }
        return map;
    }

    @ApiStatus.Internal
    public static void initialize() {
        register(WoverChunkGenerator.ID, WoverChunkGenerator.CODEC);
        register(LEGACY_ID, LegacyHelper.wrap(WoverChunkGenerator.CODEC));
    }

    public static void register(ResourceLocation location, Codec<? extends ChunkGenerator> codec) {
        final String idString = location.toString();
        if (GENERATOR_IDS.contains(idString)) {
            throw new IllegalStateException("Duplicate generator id: " + idString);
        }
        GENERATOR_IDS.add(idString);
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, location, codec);
    }
}
