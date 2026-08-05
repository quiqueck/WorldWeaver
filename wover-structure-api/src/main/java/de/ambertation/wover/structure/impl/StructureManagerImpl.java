package de.ambertation.wover.structure.impl;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.entrypoint.LibWoverStructure;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.structure.api.StructureTypeKey;
import de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructure;
import de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructurePiece;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructureManagerImpl {
    public static final EventImpl<OnBootstrapRegistry<Structure>> BOOTSTRAP_STRUCTURES =
            new EventImpl<>("BOOTSTRAP_STRUCTURES");

    public static final StructureType<RandomNbtStructure> RANDOM_NBT_STRUCTURE_TYPE = registerType(
            LibWoverStructure.C.id("random_nbt_structure"),
            RandomNbtStructure.simpleRandomCodec(RandomNbtStructure::new)
    );

    public static final StructurePieceType RANDOM_NBT_STRUCTURE_PIECE = registerPiece(
            LibWoverStructure.C.id("random_nbt_structure_piece"),
            RandomNbtStructurePiece::new
    );

    @Nullable
    public static Holder<Structure> getHolder(
            @Nullable HolderGetter<Structure> getter,
            @NotNull ResourceKey<Structure> key
    ) {
        if (getter == null) return null;

        final Optional<Holder.Reference<Structure>> h = getter.get(key);
        return h.orElse(null);
    }

    @Nullable
    public static Holder<Structure> getHolder(
            @Nullable HolderLookup.Provider lookup,
            @NotNull ResourceKey<Structure> key
    ) {
        if (lookup == null) return null;

        return lookup.lookup(Registries.STRUCTURE).flatMap(r -> r.get(key)).orElse(null);
    }

    private static boolean didInit = false;

    @ApiStatus.Internal
    public static void initialize() {
        if (didInit) return;
        didInit = true;

        DatapackRegistryBuilder.addBootstrap(
                Registries.STRUCTURE,
                StructureManagerImpl::onBootstrap
        );
    }


    public static boolean isValidBiome(Structure.GenerationContext context) {
        return isValidBiome(context, 5);
    }


    public static boolean isValidBiome(Structure.GenerationContext context, int yPos) {
        BlockPos blockPos = context.chunkPos().getMiddleBlockPosition(yPos);
        return context.validBiome().test(
                context
                        .chunkGenerator()
                        .getBiomeSource()
                        .getNoiseBiome(
                                QuartPos.fromBlock(blockPos.getX()),
                                QuartPos.fromBlock(blockPos.getY()),
                                QuartPos.fromBlock(blockPos.getZ()),
                                context.randomState().sampler()
                        )
        );
    }

    private static void onBootstrap(BootstrapContext<Structure> context) {
        BOOTSTRAP_STRUCTURES.emit(c -> c.bootstrap(context));
    }

    public static <S extends Structure> @NotNull StructureTypeKey<S> registerType(
            @NotNull Identifier location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory,
            @NotNull MapCodec<S> codec
    ) {
        final ResourceKey<StructureType<?>> key = ResourceKey.create(Registries.STRUCTURE_TYPE, location);
        @SuppressWarnings("unchecked") final StructureType<S> type = (StructureType<S>) Registry.register(
                BuiltInRegistries.STRUCTURE_TYPE,
                key,
                () -> (MapCodec<Structure>) codec
        );

        return new StructureTypeKeyImpl<>(key, type, structureFactory);
    }

    public static <S extends Structure> @NotNull StructureType<S> registerType(
            @NotNull Identifier location,
            @NotNull MapCodec<S> codec
    ) {
        final ResourceKey<StructureType<?>> key = ResourceKey.create(Registries.STRUCTURE_TYPE, location);
        @SuppressWarnings("unchecked") final StructureType<S> type = (StructureType<S>) Registry.register(
                BuiltInRegistries.STRUCTURE_TYPE,
                key,
                () -> (MapCodec<Structure>) codec
        );

        return type;
    }

    public static @NotNull StructurePieceType registerPiece(
            @NotNull Identifier location,
            @NotNull StructurePieceType pieceType
    ) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, location, pieceType);
    }
}
