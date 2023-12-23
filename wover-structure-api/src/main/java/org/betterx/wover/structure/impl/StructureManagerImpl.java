package org.betterx.wover.structure.impl;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverStructure;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.structure.api.StructureTypeKey;
import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructure;
import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructurePiece;

import com.mojang.serialization.Codec;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
            WoverStructure.C.id("random_nbt_structure"),
            RandomNbtStructure.simpleRandomCodec(RandomNbtStructure::new)
    );

    public static final StructurePieceType RANDOM_NBT_STRUCTURE_PIECE = registerPiece(
            WoverStructure.C.id("random_nbt_structure_piece"),
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

    @ApiStatus.Internal
    public static void initialize() {
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

    private static void onBootstrap(BootstapContext<Structure> context) {
        BOOTSTRAP_STRUCTURES.emit(c -> c.bootstrap(context));
    }

    public static <S extends Structure> @NotNull StructureTypeKey<S> registerType(
            @NotNull ResourceLocation location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory,
            @NotNull Codec<S> codec
    ) {
        final ResourceKey<StructureType<?>> key = ResourceKey.create(Registries.STRUCTURE_TYPE, location);
        @SuppressWarnings("unchecked") final StructureType<S> type = (StructureType<S>) Registry.register(
                BuiltInRegistries.STRUCTURE_TYPE,
                key,
                () -> (Codec<Structure>) codec
        );

        return new StructureTypeKeyImpl<>(key, type, structureFactory);
    }

    public static <S extends Structure> @NotNull StructureType<S> registerType(
            @NotNull ResourceLocation location,
            @NotNull Codec<S> codec
    ) {
        final ResourceKey<StructureType<?>> key = ResourceKey.create(Registries.STRUCTURE_TYPE, location);
        @SuppressWarnings("unchecked") final StructureType<S> type = (StructureType<S>) Registry.register(
                BuiltInRegistries.STRUCTURE_TYPE,
                key,
                () -> (Codec<Structure>) codec
        );

        return type;
    }

    public static @NotNull StructurePieceType registerPiece(
            @NotNull ResourceLocation location,
            @NotNull StructurePieceType pieceType
    ) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, location, pieceType);
    }

    static {
        if (LegacyHelper.isLegacyEnabled()) {
            registerPiece(
                    LegacyHelper.BCLIB_CORE.id("template_piece"),
                    RandomNbtStructurePiece::new
            );
        }
    }
}
