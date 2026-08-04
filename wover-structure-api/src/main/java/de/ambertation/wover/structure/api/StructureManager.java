package de.ambertation.wover.structure.api;

import de.ambertation.wover.events.api.Event;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructure;
import de.ambertation.wover.structure.impl.JigsawKeyImpl;
import de.ambertation.wover.structure.impl.RandomNbtKeyImpl;
import de.ambertation.wover.structure.impl.SimpleStructureKeyImpl;
import de.ambertation.wover.structure.impl.StructureManagerImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main entry point for creating and registering {@link Structure}s, {@link StructureType}s and
 * {@link StructurePieceType}s.
 * <p>
 * Use {@link #structure(ResourceLocation, StructureTypeKey)} (and its overloads) to create a
 * {@link StructureKey.Simple} for a custom {@link Structure} subclass, {@link #jigsaw(ResourceLocation)}
 * to create a {@link StructureKey.Jigsaw} for a {@link net.minecraft.world.level.levelgen.structure.structures.JigsawStructure},
 * or {@link #randomNbt(ResourceLocation)} to create a {@link StructureKey.RandomNbt} for a
 * {@link RandomNbtStructure}. {@link StructureKeys} provides shorter aliases for all methods in this class.
 */
public class StructureManager {
    private StructureManager() {
    }

    /**
     * The event that is fired when the Registry for a {@link StructureSet}
     * is being bootstrapped. In general, it is best to generate presets
     * in the data generator whenever possible (see WoverRegistryProvider)
     * for Details.
     */
    public static final Event<OnBootstrapRegistry<Structure>> BOOTSTRAP_STRUCTURES
            = StructureManagerImpl.BOOTSTRAP_STRUCTURES;

    /**
     * The {@link StructureType} for {@link RandomNbtStructure}. This is registered internally
     * by WoVer and is needed to implement {@link RandomNbtStructure#type()}.
     */
    public static final StructureType<RandomNbtStructure> RANDOM_NBT_STRUCTURE_TYPE
            = StructureManagerImpl.RANDOM_NBT_STRUCTURE_TYPE;

    /**
     * The {@link StructurePieceType} used by {@link de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructurePiece}.
     * This is registered internally by WoVer.
     */
    public static final StructurePieceType RANDOM_NBT_STRUCTURE_PIECE
            = StructureManagerImpl.RANDOM_NBT_STRUCTURE_PIECE;

    /**
     * Creates a {@link StructureKey} for the given {@link ResourceLocation}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey.Simple<S> structure(
            ResourceLocation location,
            @NotNull StructureTypeKey<S> type
    ) {
        return new SimpleStructureKeyImpl<>(location, type);
    }

    /**
     * Creates a {@link StructureKey} and {@link StructureTypeKey} for the given {@link ResourceLocation}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey.Simple<S> structure(
            ResourceLocation location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory,
            @NotNull MapCodec<S> codec
    ) {
        return new SimpleStructureKeyImpl<>(location, registerType(location, structureFactory, codec));
    }

    /**
     * Creates a {@link StructureKey} and {@link StructureTypeKey} for the given {@link ResourceLocation}.
     * This method will create a {@link Codec} for the {@link Structure} using the given {@link Structure#simpleCodec}.
     * This codec assumes, that the <b>{@link Structure} has no additional data</b>. If this assumption is not
     * valid you should use {@link #structure(ResourceLocation, StructureTypeKey.StructureFactory, MapCodec)}
     * instead.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey.Simple<S> structure(
            ResourceLocation location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory
    ) {
        return new SimpleStructureKeyImpl<>(
                location,
                registerType(
                        location,
                        structureFactory,
                        Structure.simpleCodec(structureFactory::create)
                )
        );
    }

    /**
     * Creates a {@link StructureKey} for a {@link net.minecraft.world.level.levelgen.structure.structures.JigsawStructure}
     * at the given {@link ResourceLocation}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey.Jigsaw jigsaw(ResourceLocation location) {
        return new JigsawKeyImpl(location);
    }

    /**
     * Creates a {@link StructureKey} for the given {@link ResourceLocation}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey.RandomNbt randomNbt(ResourceLocation location) {
        return new RandomNbtKeyImpl(location);
    }

    /**
     * Gets the {@link Holder} for a {@link Structure} from a {@link HolderGetter}.
     *
     * @param getter the getter to get the holder from. You can get this getter from a
     *               {@link net.minecraft.data.worldgen.BootstrapContext} {@code ctx} by
     *               calling {@code ctx.lookup(Registries.STRUCTURE)}
     * @param key    the key to get the holder for
     * @return the holder, or null if the holder is not present
     */
    @Nullable
    public static Holder<Structure> getHolder(
            @Nullable HolderGetter<Structure> getter,
            @NotNull ResourceKey<Structure> key
    ) {
        return StructureManagerImpl.getHolder(getter, key);
    }

    /**
     * Gets the {@link Holder} for a {@link Structure} from a {@link BootstrapContext}.
     *
     * @param context the context to get registry containing the holder. When you need to
     *                get multiple holders at a time, you might want to use
     *                {@link #getHolder(HolderGetter, ResourceKey)} instead, as it will
     *                be slightly faster.
     * @param key     the key to get the holder for
     * @return the holder, or null if the holder is not present
     */
    @Nullable
    public static Holder<Structure> getHolder(
            @Nullable BootstrapContext<?> context,
            @NotNull ResourceKey<Structure> key
    ) {
        return StructureManagerImpl.getHolder(context.lookup(Registries.STRUCTURE), key);
    }

    /**
     * Registers a new  {@link net.minecraft.world.level.levelgen.structure.StructureType}
     * for the given {@link ResourceLocation}. This method will create a {@link Codec}
     * for the {@link Structure} using the given {@link Structure#simpleCodec}. The codec
     * assumes, that the <b>{@link Structure} has no additional data</b>. If this assumption is not
     * valid you should use {@link #registerType(ResourceLocation, StructureTypeKey.StructureFactory, MapCodec)}
     * instead.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> @NotNull StructureTypeKey<S> registerType(
            @NotNull ResourceLocation location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory
    ) {
        return StructureManagerImpl.registerType(
                location,
                structureFactory,
                Structure.simpleCodec(structureFactory::create)
        );
    }

    /**
     * Registers a new {@link net.minecraft.world.level.levelgen.structure.StructureType}
     * for the given {@link ResourceLocation}, using the given {@link MapCodec} to (de)serialize
     * the {@link Structure}.
     *
     * @param location         The location of the {@link StructureTypeKey}
     * @param structureFactory The {@link StructureTypeKey.StructureFactory} used to create new instances
     *                         of the {@link Structure}
     * @param codec            The {@link Codec} used to (de)serialize the {@link Structure}
     * @param <S>              The {@link Structure} type
     * @return The {@link StructureTypeKey}
     */
    public static <S extends Structure> @NotNull StructureTypeKey<S> registerType(
            @NotNull ResourceLocation location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory,
            @NotNull MapCodec<S> codec
    ) {
        return StructureManagerImpl.registerType(location, structureFactory, codec);
    }

    /**
     * Registers a new {@link StructurePieceType} for the given {@link ResourceLocation}. A
     * {@link StructurePieceType} is needed if you implement a custom
     * {@link net.minecraft.world.level.levelgen.structure.StructurePiece}.
     *
     * @param location  The location of the {@link StructurePieceType}
     * @param pieceType The {@link StructurePieceType} to register
     * @return The registered {@link StructurePieceType}
     */
    public static @NotNull StructurePieceType registerPiece(
            @NotNull ResourceLocation location,
            @NotNull StructurePieceType pieceType
    ) {
        return StructureManagerImpl.registerPiece(location, pieceType);
    }
}
