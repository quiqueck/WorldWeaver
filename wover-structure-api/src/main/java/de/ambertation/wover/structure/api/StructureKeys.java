package de.ambertation.wover.structure.api;

import de.ambertation.wover.structure.api.pools.StructurePoolKey;
import de.ambertation.wover.structure.api.pools.StructurePoolManager;
import de.ambertation.wover.structure.api.processors.StructureProcessorKey;
import de.ambertation.wover.structure.api.processors.StructureProcessorManager;
import de.ambertation.wover.structure.api.sets.StructureSetKey;
import de.ambertation.wover.structure.api.sets.StructureSetManager;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import org.jetbrains.annotations.NotNull;

/**
 * A collection of methods for creating various structure related keys. This class is just
 * a collection of alias methods.
 */
public class StructureKeys {
    /**
     * Alias for {@link StructureSetManager#createKey(Identifier)}.
     *
     * @param location The location of the {@link StructureSet}
     * @return The {@link StructureSetKey}
     */
    public static StructureSetKey set(Identifier location) {
        return StructureSetManager.createKey(location);
    }

    /**
     * Alias for {@link StructureSetManager#createKey(Identifier)}.
     *
     * @param structure The structure this set is for
     * @return The {@link StructureSetKey}
     */
    public static StructureSetKey set(StructureKey<?, ?, ?> structure) {
        return set(structure.key().identifier());
    }

    /**
     * Alias for {@link StructureProcessorManager#createKey(Identifier)}.
     *
     * @param location The location of the {@link StructureProcessorList}
     * @return The {@link StructureProcessorKey}
     */
    public static StructureProcessorKey processor(Identifier location) {
        return StructureProcessorManager.createKey(location);
    }

    /**
     * Alias for {@link StructurePoolManager#createKey(Identifier)}.
     *
     * @param location The location of the {@link StructureTemplatePool}
     * @return The {@link StructureSetKey}
     */
    public static StructurePoolKey pool(
            Identifier location
    ) {
        return StructurePoolManager.createKey(location);
    }

    /**
     * Alias for {@link StructureManager#structure(Identifier, StructureTypeKey.StructureFactory, MapCodec)}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey.Simple<S> structure(
            Identifier location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory,
            @NotNull MapCodec<S> codec
    ) {
        return StructureManager.structure(location, structureFactory, codec);
    }

    /**
     * Alias for {@link StructureManager#structure(Identifier, StructureTypeKey.StructureFactory)}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey.Simple<S> structure(
            Identifier location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory
    ) {
        return StructureManager.structure(location, structureFactory);
    }

    /**
     * Alias for {@link StructureManager#structure(Identifier, StructureTypeKey)}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey.Simple<S> structure(
            Identifier location,
            @NotNull StructureTypeKey<S> type
    ) {
        return StructureManager.structure(location, type);
    }

    /**
     * Alias for {@link StructureManager#jigsaw(Identifier)}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey.Jigsaw jigsaw(Identifier location) {
        return StructureManager.jigsaw(location);
    }

    /**
     * Alias for {@link StructureManager#randomNbt(Identifier)}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey.RandomNbt randomNbt(Identifier location) {
        return StructureManager.randomNbt(location);
    }

    /**
     * Alias for {@link StructureManager#registerType(Identifier, StructureTypeKey.StructureFactory)}.
     *
     * @param location         The location of the {@link StructureTypeKey}
     * @param structureFactory The {@link StructureTypeKey.StructureFactory}
     * @param <S>              The {@link Structure} type
     * @return The {@link StructureTypeKey}
     */
    public static <S extends Structure> @NotNull StructureTypeKey<S> registerType(
            @NotNull Identifier location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory
    ) {
        return StructureManager.registerType(location, structureFactory);
    }

    /**
     * Alias for {@link StructureManager#registerType(Identifier, StructureTypeKey.StructureFactory, MapCodec)}.
     *
     * @param location         The location of the {@link StructureTypeKey}
     * @param structureFactory The {@link StructureTypeKey.StructureFactory}
     * @param codec            The {@link Codec} for Structure class
     * @param <S>              The {@link Structure} type
     * @return The {@link StructureTypeKey}
     */
    public static <S extends Structure> @NotNull StructureTypeKey<S> registerType(
            @NotNull Identifier location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory,
            @NotNull MapCodec<S> codec
    ) {
        return StructureManager.registerType(location, structureFactory, codec);
    }

    /**
     * Alias for {@link StructureManager#registerPiece(Identifier, StructurePieceType)}.
     *
     * @param location  The location of the {@link net.minecraft.world.level.levelgen.structure.StructurePiece}
     * @param pieceType The {@link StructurePieceType} to register
     * @return The {@link StructurePieceType}
     */
    public static @NotNull StructurePieceType registerPiece(
            @NotNull Identifier location,
            @NotNull StructurePieceType pieceType
    ) {
        return StructureManager.registerPiece(location, pieceType);
    }
}
