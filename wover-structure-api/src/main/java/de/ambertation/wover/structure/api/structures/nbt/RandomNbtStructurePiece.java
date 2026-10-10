package de.ambertation.wover.structure.api.structures.nbt;

import de.ambertation.wover.structure.impl.StructureManagerImpl;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/**
 * The {@link TemplateStructurePiece} placed by {@link de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructure}.
 * It behaves like a normal template piece; the rotation, mirror, pivot and keep-air flag are persisted
 * with the piece so it can be re-placed identically after a reload.
 */
public class RandomNbtStructurePiece extends TemplateStructurePiece {
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
}
