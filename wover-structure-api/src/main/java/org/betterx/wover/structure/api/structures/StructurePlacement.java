package org.betterx.wover.structure.api.structures;

import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructureElement;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class NoiseColumnWithState {
    public final NoiseColumn noiseColumn;
    public BlockState lastState;

    NoiseColumnWithState(NoiseColumn noiseColumn, int initY) {
        this.noiseColumn = noiseColumn;
        this.lastState = noiseColumn.getBlock(initY);
    }

    public BlockState getBlock(int y) {
        return noiseColumn.getBlock(y);
    }
}

public enum StructurePlacement implements StringRepresentable {
    LAVA(StructurePlacement::findGenerationPointNetherLava),
    SURFACE(StructurePlacement::findGenerationPointSurface),
    LEGACY_FLOOR("floor", StructurePlacement::findGenerationPointNetherFloor),
    LEGACY_CEIL("ceil", StructurePlacement::findGenerationPointNetherFloor),
    NETHER_CEIL(StructurePlacement::findGenerationPointNetherCeil),
    NETHER_SURFACE(StructurePlacement::findGenerationPointNetherFloor),
    NETHER_SURFACE_FLAT_0((a, b, c, d, e) -> StructurePlacement.findGenerationPointNetherFloorFlat(a, b, c, d, e, 0)),
    NETHER_SURFACE_FLAT_2((a, b, c, d, e) -> StructurePlacement.findGenerationPointNetherFloorFlat(a, b, c, d, e, 2)),
    NETHER_SURFACE_FLAT_4((a, b, c, d, e) -> StructurePlacement.findGenerationPointNetherFloorFlat(a, b, c, d, e, 4));


    public static final Codec<StructurePlacement> CODEC = StringRepresentable.fromEnum(StructurePlacement::values);
    protected static final int FAIL_HEIGHT = Integer.MIN_VALUE;

    private final String name;
    public final PlacementFunction placementFunction;

    StructurePlacement(String name, PlacementFunction placementFunction) {
        this.name = name;
        this.placementFunction = placementFunction;
    }

    StructurePlacement(PlacementFunction placementFunction) {
        this(null, placementFunction);
    }

    @Override
    public @NotNull String getSerializedName() {
        return name == null ? this.name().toLowerCase() : this.name.toLowerCase();
    }


    @FunctionalInterface
    public interface PlacementFunction {
        Optional<Structure.GenerationStub> find(
                Structure.GenerationContext ctx,
                Rotation rotation,
                Mirror mirror,
                RandomNbtStructureElement element,
                BiConsumer<BlockPos, StructurePiecesBuilder> consumer
        );
    }

    public static Optional<Structure.GenerationStub> onNetherSurface(
            int x, int startY, int z,
            Structure.GenerationContext ctx,
            BiConsumer<BlockPos, StructurePiecesBuilder> consumer
    ) {
        final int y = findYDownward(
                startY,
                List.of(new BlockPos(x, startY, z)),
                ctx,
                BlockBehaviour.BlockStateBase::isAir,
                state -> Heightmap.Types.WORLD_SURFACE_WG.isOpaque().test(state) && !state.liquid(),
                1, 0
        );

        if (y == FAIL_HEIGHT) {
            return Optional.empty();
        }

        final BlockPos pos = new BlockPos(x, y, z);
        return Optional.of(new Structure.GenerationStub(pos, builder -> consumer.accept(pos, builder)));
    }

    public static Optional<Structure.GenerationStub> onNetherCeiling(
            int x, int startY, int z,
            Structure.GenerationContext ctx,
            BiConsumer<BlockPos, StructurePiecesBuilder> consumer
    ) {
        final int y = findYDownward(
                startY,
                List.of(new BlockPos(x, startY, z)),
                ctx,
                state -> Heightmap.Types.WORLD_SURFACE_WG.isOpaque().test(state),
                BlockBehaviour.BlockStateBase::isAir,
                1, 0
        );

        if (y == FAIL_HEIGHT) {
            return Optional.empty();
        }

        final BlockPos pos = new BlockPos(x, y, z);
        return Optional.of(new Structure.GenerationStub(pos, builder -> consumer.accept(pos, builder)));
    }

    public static Optional<Structure.GenerationStub> onMinHeightNetherSurface(
            int x, int startY, int z,
            List<BlockPos> positions,
            int maxDeltaY,
            int airAtOffset,
            Structure.GenerationContext ctx,
            BiConsumer<BlockPos, StructurePiecesBuilder> consumer
    ) {
        int minFoundY = Integer.MAX_VALUE;
        int maxFoundY = Integer.MIN_VALUE;
        for (BlockPos testPos : positions) {
            final int y = findYDownward(
                    startY,
                    List.of(testPos),
                    ctx,
                    BlockBehaviour.BlockStateBase::isAir,
                    state -> Heightmap.Types.WORLD_SURFACE_WG.isOpaque().test(state) && !state.liquid(),
                    1, airAtOffset
            );

            if (y == FAIL_HEIGHT) {
                return Optional.empty();
            }
            if (y < minFoundY) minFoundY = y;
            if (y > maxFoundY) maxFoundY = y;
        }

        //failed due to large height difference
        if (maxFoundY - minFoundY > maxDeltaY) {
            return Optional.empty();
        }

        final BlockPos pos = new BlockPos(x, minFoundY, z);
        return Optional.of(new Structure.GenerationStub(pos, builder -> consumer.accept(pos, builder)));
    }

    public static Optional<Structure.GenerationStub> onChunkCenterWorldSurface(
            Structure.GenerationContext generationContext,
            Heightmap.Types types,
            BiConsumer<BlockPos, StructurePiecesBuilder> consumer
    ) {
        ChunkPos chunkPos = generationContext.chunkPos();
        int x = chunkPos.getMiddleBlockX();
        int z = chunkPos.getMiddleBlockZ();
        int y = generationContext.chunkGenerator()
                                 .getFirstOccupiedHeight(
                                         x, z, types,
                                         generationContext.heightAccessor(),
                                         generationContext.randomState()
                                 );
        final BlockPos pos = new BlockPos(x, y, z);
        return Optional.of(new Structure.GenerationStub(pos, builder -> consumer.accept(pos, builder)));
    }

    public static @Nullable int findYDownward(
            int startY,
            List<BlockPos> testColumns,
            Structure.GenerationContext ctx,
            Predicate<BlockState> testAir,
            Predicate<BlockState> testSurface,
            int minMatches,
            int airAtOffset
    ) {
        return findYDownward(
                startY, ctx.heightAccessor().getMinBuildHeight() + 4,
                testColumns, ctx,
                testAir, testSurface, minMatches, airAtOffset
        );
    }

    public static @Nullable int findYDownward(
            int startY,
            int stopY,
            List<BlockPos> testColumns,
            Structure.GenerationContext ctx,
            Predicate<BlockState> testAir,
            Predicate<BlockState> testSurface,
            int minMatches,
            int airAtOffset
    ) {
        final List<NoiseColumnWithState> noiseColumns = testColumns
                .stream()
                .map(p -> new NoiseColumnWithState(ctx
                        .chunkGenerator()
                        .getBaseColumn(
                                p.getX(), p.getZ(),
                                ctx.heightAccessor(),
                                ctx.randomState()
                        ), startY)
                ).toList();

        int res = FAIL_HEIGHT;
        outerLoop:
        for (int y = startY - 1; y > stopY; y--) {
            int matchCount = 0;
            for (NoiseColumnWithState noiseColumn : noiseColumns) {
                BlockState state = noiseColumn.getBlock(y);
                if (testAir.test(noiseColumn.lastState)) {
                    if (testSurface.test(state)) {
                        if (++matchCount == minMatches) {
                            res = y;
                            break outerLoop;
                        }
                    }
                }
                noiseColumn.lastState = state;
            }
        }

        if (airAtOffset != 0) {
            int matchCount = 0;
            for (NoiseColumnWithState noiseColumn : noiseColumns) {
                if (testAir.test(noiseColumn.getBlock(res + airAtOffset))) {
                    if (++matchCount == minMatches) {
                        return res;
                    }
                }
            }
            return FAIL_HEIGHT;
        }

        return res;
    }

    public static boolean hasValidBiomeAtRandomHeight(Structure.GenerationContext ctx, int x, int z) {
        final int randomY = ctx.random()
                               .nextIntBetweenInclusive(
                                       ctx.heightAccessor().getMinBuildHeight(),
                                       ctx.heightAccessor().getMaxBuildHeight()
                               );

        return hasValidBiomeAt(ctx, x, randomY, z);
    }

    public static boolean hasValidBiomeAt(Structure.GenerationContext ctx, int x, int y, int z) {
        return ctx
                .validBiome()
                .test(ctx.chunkGenerator()
                         .getBiomeSource()
                         .getNoiseBiome(
                                 QuartPos.fromBlock(x),
                                 QuartPos.fromBlock(y),
                                 QuartPos.fromBlock(z),
                                 ctx.randomState().sampler()
                         )
                );
    }

    @NotNull
    public static BlockPos getCenter(Mirror mirror, StructureTemplate template) {
        final int sx = mirror == Mirror.FRONT_BACK ? -1 : 1;
        final int sz = mirror == Mirror.LEFT_RIGHT ? -1 : 1;

        return new BlockPos(
                sx * template.getSize().getX() / 2,
                0,
                sz * template.getSize().getZ() / 2
        );
    }

    private static @NotNull Optional<Structure.GenerationStub> findGenerationPointSurface(
            Structure.GenerationContext ctx,
            Rotation rotation,
            Mirror mirror,
            RandomNbtStructureElement element,
            BiConsumer<BlockPos, StructurePiecesBuilder> consumer
    ) {
        return onChunkCenterWorldSurface(ctx, Heightmap.Types.WORLD_SURFACE_WG, consumer);
    }

    private static @NotNull Optional<Structure.GenerationStub> findGenerationPointNetherCeil(
            Structure.GenerationContext ctx,
            Rotation rotation,
            Mirror mirror,
            RandomNbtStructureElement element,
            BiConsumer<BlockPos, StructurePiecesBuilder> consumer
    ) {
        final ChunkPos chunkPos = ctx.chunkPos();
        final int x = chunkPos.getMiddleBlockX();
        final int z = chunkPos.getMiddleBlockZ();

        if (!StructurePlacement.hasValidBiomeAtRandomHeight(ctx, x, z))
            return Optional.empty();

        final ChunkGenerator generator = ctx.chunkGenerator();
        final int maxHeight = generator.getGenDepth() - 20;
        final int seaLevel = generator.getSeaLevel();

        return onNetherCeiling(
                x, Mth.randomBetweenInclusive(ctx.random(), seaLevel, maxHeight), z,
                ctx, consumer
        );
    }

    private static @NotNull Optional<Structure.GenerationStub> findGenerationPointNetherFloor(
            Structure.GenerationContext ctx,
            Rotation rotation,
            Mirror mirror,
            RandomNbtStructureElement element,
            BiConsumer<BlockPos, StructurePiecesBuilder> consumer
    ) {
        final ChunkPos chunkPos = ctx.chunkPos();
        final int x = chunkPos.getMiddleBlockX();
        final int z = chunkPos.getMiddleBlockZ();

        if (!StructurePlacement.hasValidBiomeAtRandomHeight(ctx, x, z))
            return Optional.empty();

        final ChunkGenerator generator = ctx.chunkGenerator();
        final int maxHeight = generator.getGenDepth() - 20;
        final int seaLevel = generator.getSeaLevel();

        return onNetherSurface(
                x, Mth.randomBetweenInclusive(ctx.random(), seaLevel, maxHeight), z,
                ctx, consumer
        );
    }

    private static @NotNull Optional<Structure.GenerationStub> findGenerationPointNetherFloorFlat(
            Structure.GenerationContext ctx,
            Rotation rotation,
            Mirror mirror,
            RandomNbtStructureElement element,
            BiConsumer<BlockPos, StructurePiecesBuilder> consumer,
            int maxDeltaY
    ) {
        final ChunkPos chunkPos = ctx.chunkPos();
        final int x = chunkPos.getMiddleBlockX();
        final int z = chunkPos.getMiddleBlockZ();

        if (!StructurePlacement.hasValidBiomeAtRandomHeight(ctx, x, z))
            return Optional.empty();

        final ChunkGenerator generator = ctx.chunkGenerator();
        final int maxHeight = generator.getGenDepth() - 20;
        final int seaLevel = generator.getSeaLevel();

        BlockPos startPos = new BlockPos(x, maxHeight, z);

        final StructureTemplate template = ctx.structureTemplateManager().getOrCreate(element.nbtLocation());
        final BlockPos center = getCenter(mirror, template);
        BoundingBox boundingBox = template.getBoundingBox(startPos, rotation, center, mirror);
        List<BlockPos> list = List.of(
                new BlockPos(boundingBox.minX(), 0, boundingBox.minZ()),
                new BlockPos(boundingBox.maxX(), 0, boundingBox.minZ()),
                new BlockPos(boundingBox.minX(), 0, boundingBox.maxZ()),
                new BlockPos(boundingBox.maxX(), 0, boundingBox.maxZ())
        );

        return onMinHeightNetherSurface(
                x, Mth.randomBetweenInclusive(ctx.random(), seaLevel, maxHeight), z,
                list,
                maxDeltaY, (int) (boundingBox.getYSpan() * 0.8), ctx, consumer
        );
    }

    private static @NotNull Optional<Structure.GenerationStub> findGenerationPointNetherLava(
            Structure.GenerationContext ctx,
            Rotation rotation,
            Mirror mirror,
            RandomNbtStructureElement element,
            BiConsumer<BlockPos, StructurePiecesBuilder> consumer
    ) {
        final ChunkPos chunkPos = ctx.chunkPos();
        final int x = chunkPos.getMiddleBlockX();
        final int z = chunkPos.getMiddleBlockZ();

        final ChunkGenerator generator = ctx.chunkGenerator();
        final int seaLevel = generator.getSeaLevel();

        if (!StructurePlacement.hasValidBiomeAt(ctx, x, seaLevel, z))
            return Optional.empty();

        BlockPos startPos = new BlockPos(x, seaLevel, z);

        final StructureTemplate template = ctx.structureTemplateManager().getOrCreate(element.nbtLocation());
        final BlockPos center = getCenter(mirror, template);
        BoundingBox boundingBox = template.getBoundingBox(startPos, rotation, center, mirror);
        List<BlockPos> list = List.of(
                new BlockPos(boundingBox.minX(), 0, boundingBox.minZ()),
                new BlockPos(boundingBox.maxX(), 0, boundingBox.minZ()),
                new BlockPos(boundingBox.minX(), 0, boundingBox.maxZ()),
                new BlockPos(boundingBox.maxX(), 0, boundingBox.maxZ())
        );

        final int y = findYDownward(
                seaLevel,
                seaLevel - 2,
                list, ctx,
                BlockBehaviour.BlockStateBase::isAir,
                state -> state.is(Blocks.LAVA),
                list.size(), 0
        );

        if (y == FAIL_HEIGHT)
            return Optional.empty();

        final BlockPos pos = new BlockPos(x, y, z);
        return Optional.of(new Structure.GenerationStub(pos, builder -> consumer.accept(pos, builder)));
    }
}
