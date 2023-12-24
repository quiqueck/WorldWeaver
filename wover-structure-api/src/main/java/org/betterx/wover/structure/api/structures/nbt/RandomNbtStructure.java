package org.betterx.wover.structure.api.structures.nbt;

import org.betterx.wover.entrypoint.LibWoverStructure;
import org.betterx.wover.structure.api.StructureNBT;
import org.betterx.wover.structure.api.structures.StructurePlacement;
import org.betterx.wover.structure.impl.StructureManagerImpl;
import org.betterx.wover.util.RandomizedWeightedList;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import org.apache.commons.lang3.time.StopWatch;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class RandomNbtStructure extends Structure {
    public static <T extends RandomNbtStructure> Codec<T> simpleRandomCodec(Function4<StructureSettings, StructurePlacement, Boolean, RandomizedWeightedList<RandomNbtStructureElement>, T> instancer) {
        return RecordCodecBuilder.create((instance) -> instance
                .group(
                        Structure.settingsCodec(instance),
                        StructurePlacement.CODEC
                                .fieldOf("placement")
                                .forGetter(RandomNbtStructure::placement),
                        Codec.BOOL
                                .optionalFieldOf("keep_air", false)
                                .forGetter(RandomNbtStructure::keepAir),
                        RandomizedWeightedList
                                .buildCodec(RandomNbtStructureElement.CODEC)
                                .fieldOf("configs")
                                .forGetter(RandomNbtStructure::elements)
                )
                .apply(instance, instancer)
        );
    }

    private final @NotNull RandomizedWeightedList<RandomNbtStructureElement> elements;
    private final StructurePlacement placement;
    private final boolean keepAir;

    public RandomNbtStructure(
            StructureSettings structureSettings,
            StructurePlacement placement,
            boolean keepAir,
            @NotNull RandomizedWeightedList<RandomNbtStructureElement> elements
    ) {
        super(structureSettings);
        this.elements = elements;
        this.keepAir = keepAir;
        this.placement = placement;
    }


    public RandomizedWeightedList<RandomNbtStructureElement> elements() {
        return elements;
    }

    public StructurePlacement placement() {
        return placement;
    }

    public boolean keepAir() {
        return keepAir;
    }


    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        StopWatch sw = StopWatch.createStarted();

        final Rotation rotation = StructureNBT.getRandomRotation(ctx.random());
        final Mirror mirror = StructureNBT.getRandomMirror(ctx.random());
        final RandomNbtStructureElement element = elements.getRandomValue(ctx.random());

        final Optional<GenerationStub> result = placement.placementFunction.find(
                ctx, rotation, mirror, element,
                (pos, structurePiecesBuilder) -> {
                    LibWoverStructure.C.log.debug("Generating RandomNbtStructure: " + pos + " (" + sw.getTime() + "ms)");
                    this.generatePieces(
                            structurePiecesBuilder, ctx,
                            pos, rotation, mirror, element
                    );
                }
        );
        if (result.isEmpty()) {
            LibWoverStructure.C.log.debug("Rejected RandomNbtStructure " + element.nbtLocation() + " in " + sw.getTime() + "ms");
        } else {
            LibWoverStructure.C.log.debug("Accepted RandomNbtStructure " + element.nbtLocation() + " in " + sw.getTime() + "ms");
        }
        return result;
    }


    private void generatePieces(
            StructurePiecesBuilder structurePiecesBuilder,
            Structure.GenerationContext ctx,
            BlockPos pos,
            Rotation rotation,
            Mirror mirror,
            RandomNbtStructureElement element
    ) {
        final StructureTemplate template = ctx.structureTemplateManager().getOrCreate(element.nbtLocation());
        final BlockPos center = StructurePlacement.getCenter(mirror, template);

        structurePiecesBuilder.addPiece(
                new RandomNbtStructurePiece(
                        ctx.structureTemplateManager(),
                        element.nbtLocation(),
                        RandomNbtStructurePiece.settings(rotation, mirror, center, this.keepAir),
                        pos.above(element.yOffset()), this.keepAir
                )
        );
    }


    @Override
    public @NotNull StructureType<?> type() {
        return StructureManagerImpl.RANDOM_NBT_STRUCTURE_TYPE;
    }
}
