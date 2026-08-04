package de.ambertation.wover.structure.api.structures.nbt;

import de.ambertation.wover.entrypoint.LibWoverStructure;
import de.ambertation.wover.structure.api.StructureNBT;
import de.ambertation.wover.structure.api.structures.StructurePlacement;
import de.ambertation.wover.structure.impl.StructureManagerImpl;
import de.ambertation.wover.util.RandomizedWeightedList;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

/**
 * A {@link Structure} that, at generation time, picks one of several {@code .nbt} templates
 * ({@link RandomNbtStructureElement}) at random (weighted) and places it using a
 * {@link StructurePlacement} strategy. Built via
 * {@link de.ambertation.wover.structure.api.builders.RandomNbtBuilder}, created through
 * {@link de.ambertation.wover.structure.api.StructureManager#randomNbt(net.minecraft.resources.ResourceLocation)}.
 * <p>
 * Subclasses only need to provide a {@link MapCodec} (see {@link #simpleRandomCodec(Function4)}) and a
 * constructor matching its shape; the actual generation logic lives entirely in this class.
 */
public class RandomNbtStructure extends Structure {
    /**
     * Builds a {@link MapCodec} for a {@link RandomNbtStructure} subclass, adding the
     * {@code placement}, {@code keep_air} and {@code configs} fields (in addition to the base
     * {@link Structure#settingsCodec}) to the standard structure JSON format.
     *
     * @param instancer A factory that creates the subclass instance from the decoded fields, typically a
     *                  method reference to its constructor
     * @param <T>       The {@link RandomNbtStructure} subclass
     * @return The built {@link MapCodec}
     */
    public static <T extends RandomNbtStructure> MapCodec<T> simpleRandomCodec(Function4<StructureSettings, StructurePlacement, Boolean, RandomizedWeightedList<RandomNbtStructureElement>, T> instancer) {
        return RecordCodecBuilder.mapCodec((instance) -> instance
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

    /**
     * Creates a new {@link RandomNbtStructure}. Usually called through a subclass constructor referenced
     * by {@link #simpleRandomCodec(Function4)}, or by
     * {@link de.ambertation.wover.structure.api.builders.RandomNbtBuilder}.
     *
     * @param structureSettings The base {@link StructureSettings}
     * @param placement         The placement strategy used to find a valid generation point
     * @param keepAir           Whether air blocks in the template are preserved during placement
     * @param elements          The weighted list of {@code .nbt} templates to randomly pick from
     */
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


    /**
     * The weighted list of {@code .nbt} templates this structure randomly picks from.
     *
     * @return The elements
     */
    public RandomizedWeightedList<RandomNbtStructureElement> elements() {
        return elements;
    }

    /**
     * The placement strategy used to find a valid generation point for this structure.
     *
     * @return The placement strategy
     */
    public StructurePlacement placement() {
        return placement;
    }

    /**
     * Whether air blocks in the placed template are preserved (instead of being ignored).
     *
     * @return {@code true} if air blocks are kept
     */
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
