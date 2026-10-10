package de.ambertation.wover.structure.impl.pools;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

public class SingleEndPoolElement extends SinglePoolElement {
    public static final MapCodec<SingleEndPoolElement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SingleEndPoolElement.templateCodec(),
            SingleEndPoolElement.processorsCodec(),
            SingleEndPoolElement.projectionCodec()
    ).apply(instance, SingleEndPoolElement::new));


    public SingleEndPoolElement(
            Either<ResourceLocation, StructureTemplate> either,
            Holder<StructureProcessorList> holder,
            StructureTemplatePool.Projection projection
    ) {
        this(either, holder, projection, Optional.empty());
    }

    public SingleEndPoolElement(
            Either<ResourceLocation, StructureTemplate> either,
            Holder<StructureProcessorList> holder,
            StructureTemplatePool.Projection projection,
            LiquidSettings liquidSettings
    ) {
        this(either, holder, projection, Optional.of(liquidSettings));
    }

    protected SingleEndPoolElement(
            Either<ResourceLocation, StructureTemplate> either,
            Holder<StructureProcessorList> holder,
            StructureTemplatePool.Projection projection,
            Optional<LiquidSettings> liquidSettings
    ) {
        super(either, holder, projection, liquidSettings);
    }

    @Override
    public boolean place(
            StructureTemplateManager structureTemplateManager,
            WorldGenLevel worldGenLevel,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            BlockPos blockPos,
            BlockPos blockPos2,
            Rotation rotation,
            BoundingBox boundingBox,
            RandomSource randomSource,
            LiquidSettings liquidSettings,
            boolean bl
    ) {
        //in the end, we don't want to generate anything below y=5
        if (blockPos.getY() < 5) return false;
        //don't place pieces that are buried inside the terrain (quiqueck/BetterEnd#246)
        if (!hasEmptySpace(worldGenLevel, chunkGenerator, blockPos))
            return false;

        return super.place(
                structureTemplateManager,
                worldGenLevel,
                structureManager,
                chunkGenerator,
                blockPos,
                blockPos2,
                rotation,
                boundingBox,
                randomSource,
                liquidSettings,
                bl
        );
    }

    /**
     * Whether the piece origin (or the block two above it) is air in the generated terrain.
     * <p>
     * A piece is placed chunk by chunk, and its origin can lie two chunks away from the chunk that is
     * currently decorated (village streets are up to 22 blocks long). Reading the world there is outside
     * the region worldgen may access, and since the world looks different depending on which chunk is
     * being generated, the piece could be placed in some chunks and cut off in others. The base terrain
     * column of the chunk generator is the same no matter which chunk asks, so the decision is too.
     */
    public static boolean hasEmptySpace(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, BlockPos blockPos) {
        final NoiseColumn column = chunkGenerator.getBaseColumn(
                blockPos.getX(),
                blockPos.getZ(),
                worldGenLevel,
                worldGenLevel.getLevel().getChunkSource().randomState()
        );
        return column.getBlock(blockPos.getY() + 2).isAir() || column.getBlock(blockPos.getY()).isAir();
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementTypeManagerImpl.END;
    }

    @Override
    public String toString() {
        return "SingleEnd[" + this.template + "]";
    }
}
