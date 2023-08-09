package org.betterx.wover.structure.api;

import org.betterx.wover.structure.api.builders.JigsawBuilder;
import org.betterx.wover.structure.api.builders.StructureBuilder;
import org.betterx.wover.structure.api.pools.StructurePoolKey;
import org.betterx.wover.structure.api.pools.StructurePoolManager;
import org.betterx.wover.structure.api.processors.StructureProcessorKey;
import org.betterx.wover.structure.api.processors.StructureProcessorManager;
import org.betterx.wover.structure.api.sets.StructureSetKey;
import org.betterx.wover.structure.api.sets.StructureSetManager;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import org.jetbrains.annotations.NotNull;

/**
 * A collection of methods for creating various structure related keys. This class is just
 * a collection of alias methods.
 */
public class StructureKeys {
    /**
     * Alias for {@link StructureSetManager#createKey(ResourceLocation)}.
     *
     * @param location The location of the {@link StructureSet}
     * @return The {@link StructureSetKey}
     */
    public static StructureSetKey set(ResourceLocation location) {
        return StructureSetManager.createKey(location);
    }

    /**
     * Alias for {@link StructureProcessorManager#createKey(ResourceLocation)}.
     *
     * @param location The location of the {@link StructureProcessorList}
     * @return The {@link StructureProcessorKey}
     */
    public static StructureProcessorKey processor(ResourceLocation location) {
        return StructureProcessorManager.createKey(location);
    }

    /**
     * Alias for {@link StructurePoolManager#createKey(ResourceLocation)}.
     *
     * @param location The location of the {@link StructureTemplatePool}
     * @return The {@link StructureSetKey}
     */
    public static StructurePoolKey pool(
            ResourceLocation location
    ) {
        return StructurePoolManager.createKey(location);
    }

    /**
     * Alias for {@link StructureManager#structure(ResourceLocation, StructureTypeKey.StructureFactory, Codec)}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey<S, StructureBuilder<S>> structure(
            ResourceLocation location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory,
            @NotNull Codec<S> codec
    ) {
        return StructureManager.structure(location, structureFactory, codec);
    }

    /**
     * Alias for {@link StructureManager#structure(ResourceLocation, StructureTypeKey.StructureFactory)}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey<S, StructureBuilder<S>> structure(
            ResourceLocation location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory
    ) {
        return StructureManager.structure(location, structureFactory);
    }

    /**
     * Alias for {@link StructureManager#structure(ResourceLocation, StructureTypeKey)}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey<S, StructureBuilder<S>> structure(
            ResourceLocation location,
            @NotNull StructureTypeKey<S> type
    ) {
        return StructureManager.structure(location, type);
    }

    /**
     * Alias for {@link StructureManager#jigsaw(ResourceLocation)}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey<JigsawStructure, JigsawBuilder> jigsaw(ResourceLocation location) {
        return StructureManager.jigsaw(location);
    }
}
