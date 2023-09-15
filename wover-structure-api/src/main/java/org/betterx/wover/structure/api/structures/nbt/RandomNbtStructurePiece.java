package org.betterx.wover.structure.api.structures.nbt;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.structure.impl.StructureManagerImpl;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class RandomNbtStructurePiece extends TemplateStructurePiece {
    private final boolean keepAir;

    public RandomNbtStructurePiece(
            StructureTemplateManager manager,
            ResourceLocation nbtLocation,
            StructurePlaceSettings placeSettings,
            BlockPos templatePosition,
            boolean keepAir
    ) {
        super(
                StructureManagerImpl.RANDOM_NBT_STRUCTURE_PIECE, 0,
                manager, nbtLocation, nbtLocation.toString(),
                placeSettings, templatePosition
        );
        this.keepAir = keepAir;
    }

    public RandomNbtStructurePiece(StructurePieceSerializationContext context, CompoundTag compoundTag) {
        this(
                context, compoundTag,
                compoundTag.contains("A") && compoundTag.getBoolean("A")
        );

    }

    public RandomNbtStructurePiece(
            StructurePieceSerializationContext context,
            CompoundTag compoundTag,
            boolean keepAir
    ) {
        super(
                StructureManagerImpl.RANDOM_NBT_STRUCTURE_PIECE,
                compoundTag,
                context.structureTemplateManager(),
                loc -> fromNbt(compoundTag, keepAir)
        );

        this.keepAir = keepAir;
    }

    private static StructurePlaceSettings fromNbt(CompoundTag compoundTag, boolean keepAir) {
        return settings(
                Rotation.valueOf(compoundTag.getString("R")),
                Mirror.valueOf(compoundTag.getString("M")),
                new BlockPos(compoundTag.getInt("RX"), compoundTag.getInt("RY"), compoundTag.getInt("RZ")),
                keepAir
        );

    }

    public static StructurePlaceSettings settings(
            Rotation rotation,
            Mirror mirror,
            BlockPos halfSize,
            boolean keepAir
    ) {
        return new StructurePlaceSettings().setRotation(rotation)
                                           .setRotationPivot(halfSize)
                                           .setMirror(mirror)
                                           .addProcessor(keepAir
                                                   ? BlockIgnoreProcessor.STRUCTURE_BLOCK
                                                   : BlockIgnoreProcessor.STRUCTURE_AND_AIR);
    }

    @Override
    protected void addAdditionalSaveData(
            StructurePieceSerializationContext structurePieceSerializationContext,
            CompoundTag tag
    ) {
        super.addAdditionalSaveData(structurePieceSerializationContext, tag);
        tag.putString("R", this.placeSettings.getRotation().name());
        tag.putString("M", this.placeSettings.getMirror().name());
        tag.putInt("RX", this.placeSettings.getRotationPivot().getX());
        tag.putInt("RY", this.placeSettings.getRotationPivot().getY());
        tag.putInt("RZ", this.placeSettings.getRotationPivot().getZ());

        if (this.keepAir)
            tag.putBoolean("A", this.keepAir);
    }

    @Override
    protected void handleDataMarker(
            String string,
            BlockPos blockPos,
            ServerLevelAccessor serverLevelAccessor,
            RandomSource randomSource,
            BoundingBox boundingBox
    ) {

    }

    @Override
    public void postProcess(
            WorldGenLevel worldGenLevel,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            RandomSource randomSource,
            BoundingBox writableBounds,
            ChunkPos chunkPos,
            BlockPos blockPos
    ) {
        super.postProcess(
                worldGenLevel,
                structureManager,
                chunkGenerator,
                randomSource,
                writableBounds,
                chunkPos,
                blockPos
        );
        List<BlockPos> list2 = ImmutableList.of(
                new BlockPos(writableBounds.minX(), blockPos.getY(), writableBounds.minZ()),
                new BlockPos(writableBounds.maxX(), blockPos.getY(), writableBounds.minZ()),
                new BlockPos(writableBounds.minX(), blockPos.getY(), writableBounds.maxZ()),
                new BlockPos(writableBounds.maxX(), blockPos.getY(), writableBounds.maxZ())
        );

        list2.forEach(pos -> worldGenLevel.setBlock(
                pos,
                Blocks.OXIDIZED_COPPER.defaultBlockState(),
                BlockHelper.SET_SILENT
        ));

        worldGenLevel.setBlock(templatePosition, Blocks.AMETHYST_BLOCK.defaultBlockState(), BlockHelper.SET_SILENT);
        worldGenLevel.setBlock(
                templatePosition.offset(this.placeSettings.getRotationPivot()),
                Blocks.NETHERITE_BLOCK.defaultBlockState(),
                BlockHelper.SET_SILENT
        );
    }
}
