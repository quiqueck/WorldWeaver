package de.ambertation.wover.structure.api.structures.nbt;

import de.ambertation.wover.block.api.BlockHelper;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.structure.impl.StructureManagerImpl;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * The {@link TemplateStructurePiece} placed by {@link de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructure}.
 * It behaves like a normal template piece, but can additionally mark its four bounding-box corners, its
 * origin and its rotation pivot with oxidized copper/amethyst/netherite debug blocks during
 * {@link #postProcess} (useful while tuning a
 * {@link de.ambertation.wover.structure.api.structures.StructurePlacement}). See {@link #placeDebugMarkers}.
 */
public class RandomNbtStructurePiece extends TemplateStructurePiece {
    /**
     * Whether {@link #postProcess} marks this piece's four bounding-box corners (oxidized copper), its
     * origin (amethyst) and its rotation pivot (netherite) with debug blocks.
     * <p>
     * These markers are a placement-tuning aid, but until now they were written <em>unconditionally</em>,
     * so every {@link de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructure} placed by any
     * mod using this API got six foreign blocks baked into it in normal gameplay as well. That was a
     * shipped debugging leftover, not intended behaviour — the marker blocks are not part of any
     * {@code .nbt} template and overwrite whatever the template (or the terrain) put there.
     * <p>
     * It therefore now defaults to {@link ModCore#isDevEnvironment()}, i.e. on in a dev/test run and off
     * in production. It stays writable so a mod (or a test) can force either behaviour explicitly.
     */
    public static boolean placeDebugMarkers = ModCore.isDevEnvironment();

    private final boolean keepAir;

    /**
     * Creates a new piece that places the {@code .nbt} template at {@code nbtLocation}.
     *
     * @param manager          The {@link StructureTemplateManager} used to resolve the template
     * @param nbtLocation      The location of the {@code .nbt} template
     * @param placeSettings    The place settings (rotation, mirror, pivot, processors) to use
     * @param templatePosition The position to place the template's origin at
     * @param keepAir          Whether air blocks in the template are preserved during placement
     */
    public RandomNbtStructurePiece(
            StructureTemplateManager manager,
            Identifier nbtLocation,
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

    /**
     * Deserializes a piece previously saved with {@link #addAdditionalSaveData}.
     *
     * @param context     The serialization context
     * @param compoundTag The saved NBT data
     */
    public RandomNbtStructurePiece(StructurePieceSerializationContext context, CompoundTag compoundTag) {
        this(
                context, compoundTag,
                compoundTag.getBoolean("A").orElse(false)
        );

    }

    /**
     * Deserializes a piece previously saved with {@link #addAdditionalSaveData}, using an explicit
     * {@code keepAir} value instead of reading it from {@code compoundTag}.
     *
     * @param context     The serialization context
     * @param compoundTag The saved NBT data
     * @param keepAir     Whether air blocks in the template are preserved during placement
     */
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
                Rotation.valueOf(compoundTag.getString("R").orElse(Rotation.NONE.name())),
                Mirror.valueOf(compoundTag.getString("M").orElse(Mirror.NONE.name())),
                new BlockPos(
                        compoundTag.getInt("RX").orElse(0),
                        compoundTag.getInt("RY").orElse(0),
                        compoundTag.getInt("RZ").orElse(0)
                ),
                keepAir
        );

    }

    /**
     * Builds the {@link StructurePlaceSettings} used to place a {@link RandomNbtStructurePiece}: applies
     * the given rotation/mirror around {@code halfSize}, and adds a
     * {@link BlockIgnoreProcessor#STRUCTURE_AND_AIR}/{@link BlockIgnoreProcessor#STRUCTURE_BLOCK}
     * processor depending on {@code keepAir}.
     *
     * @param rotation The rotation to apply
     * @param mirror   The mirror to apply
     * @param halfSize The pivot to rotate/mirror around (typically the template's center, see
     *                 {@link de.ambertation.wover.structure.api.structures.StructurePlacement#getCenter})
     * @param keepAir  Whether air blocks in the template are preserved during placement
     * @return The built place settings
     */
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
        if (!placeDebugMarkers) return;

        List<BlockPos> corners = ImmutableList.of(
                new BlockPos(writableBounds.minX(), blockPos.getY(), writableBounds.minZ()),
                new BlockPos(writableBounds.maxX(), blockPos.getY(), writableBounds.minZ()),
                new BlockPos(writableBounds.minX(), blockPos.getY(), writableBounds.maxZ()),
                new BlockPos(writableBounds.maxX(), blockPos.getY(), writableBounds.maxZ())
        );

        // 26.3 folded the four oxidation stages of each copper block into a WeatheringCopperCollection,
        // so the flat Blocks.OXIDIZED_COPPER constant is gone. weathering() selects the unwaxed variants
        // (the waxed() set is the other half of the collection), oxidized() the stage we used before.
        final BlockState oxidizedCopper = Blocks.COPPER_BLOCK.weathering().oxidized().defaultBlockState();
        corners.forEach(pos -> worldGenLevel.setBlock(pos, oxidizedCopper, BlockHelper.SET_SILENT));

        worldGenLevel.setBlock(templatePosition, Blocks.AMETHYST_BLOCK.defaultBlockState(), BlockHelper.SET_SILENT);
        worldGenLevel.setBlock(
                templatePosition.offset(this.placeSettings.getRotationPivot()),
                Blocks.NETHERITE_BLOCK.defaultBlockState(),
                BlockHelper.SET_SILENT
        );
    }
}
