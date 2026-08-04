package de.ambertation.wover.structure.api.structures;

import de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructureElement;

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

/**
 * The strategies a {@link de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructure} can use to
 * find a valid generation point for one of its elements. Set on a structure via
 * {@link de.ambertation.wover.structure.api.builders.RandomNbtBuilder#placement(StructurePlacement)}, and
 * serialized to datapacks as the {@code placement} field of the {@code random_nbt_structure} JSON format
 * (see {@link #CODEC}).
 */
public enum StructurePlacement implements StringRepresentable {
    /**
     * Finds a lava surface near sea level in the Nether (searching downward from sea level) and places
     * the structure on top of it.
     */
    LAVA(StructurePlacement::findGenerationPointNetherLava),
    /**
     * Places the structure on the overworld's world surface heightmap, at the center of the chunk.
     */
    SURFACE(StructurePlacement::findGenerationPointSurface),
    /**
     * Legacy alias (serialized as {@code "floor"}) for a Nether-floor placement, kept for backwards
     * compatibility with older datapacks.
     */
    LEGACY_FLOOR("floor", StructurePlacement::findGenerationPointNetherFloor),
    /**
     * Legacy alias (serialized as {@code "ceil"}); note this currently resolves to the same
     * floor-placement function as {@link #LEGACY_FLOOR}, kept for backwards compatibility with older
     * datapacks.
     */
    LEGACY_CEIL("ceil", StructurePlacement::findGenerationPointNetherFloor),
    /**
     * Finds the Nether's bedrock ceiling (searching downward from a random height) and places the
     * structure hanging from it.
     */
    NETHER_CEIL(StructurePlacement::findGenerationPointNetherCeil),
    /**
     * Finds the Nether's floor (searching downward from a random height) and places the structure on it.
     */
    NETHER_SURFACE(StructurePlacement::findGenerationPointNetherFloor),
    /**
     * Like {@link #NETHER_SURFACE}, but additionally rejects placements where the four corners of the
     * structure's bounding box differ in height by more than {@code 0} blocks.
     */
    NETHER_SURFACE_FLAT_0((a, b, c, d, e) -> StructurePlacement.findGenerationPointNetherFloorFlat(a, b, c, d, e, 0)),
    /**
     * Like {@link #NETHER_SURFACE}, but additionally rejects placements where the four corners of the
     * structure's bounding box differ in height by more than {@code 2} blocks.
     */
    NETHER_SURFACE_FLAT_2((a, b, c, d, e) -> StructurePlacement.findGenerationPointNetherFloorFlat(a, b, c, d, e, 2)),
    /**
     * Like {@link #NETHER_SURFACE}, but additionally rejects placements where the four corners of the
     * structure's bounding box differ in height by more than {@code 4} blocks.
     */
    NETHER_SURFACE_FLAT_4((a, b, c, d, e) -> StructurePlacement.findGenerationPointNetherFloorFlat(a, b, c, d, e, 4));


    /**
     * The {@link Codec} used to (de)serialize a {@link StructurePlacement} to/from its datapack name.
     */
    public static final Codec<StructurePlacement> CODEC = StringRepresentable.fromEnum(StructurePlacement::values);
    protected static final int FAIL_HEIGHT = Integer.MIN_VALUE;

    private final String name;
    /**
     * The function that locates a valid generation point for this placement strategy.
     */
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


    /**
     * Locates a valid generation point (and the {@link Structure.GenerationStub} that generates the
     * pieces once accepted) for a {@link RandomNbtStructureElement}.
     */
    @FunctionalInterface
    public interface PlacementFunction {
        /**
         * Attempts to find a valid generation point.
         *
         * @param ctx      The generation context to test with
         * @param rotation The randomly picked rotation for the element
         * @param mirror   The randomly picked mirror for the element
         * @param element  The element to place
         * @param consumer Called with the found position and a {@link StructurePiecesBuilder} to add the
         *                 structure's pieces to, once/if the generation point is accepted
         * @return The {@link Structure.GenerationStub}, or {@link Optional#empty()} if no valid point was
         * found
         */
        Optional<Structure.GenerationStub> find(
                Structure.GenerationContext ctx,
                Rotation rotation,
                Mirror mirror,
                RandomNbtStructureElement element,
                BiConsumer<BlockPos, StructurePiecesBuilder> consumer
        );
    }

    /**
     * Searches downward from {@code startY} for a solid, non-liquid Nether floor at {@code (x, z)} and, if
     * found, calls {@code consumer} with the found position.
     *
     * @param x        The x coordinate to search at
     * @param startY   The y coordinate to start searching downward from
     * @param z        The z coordinate to search at
     * @param ctx      The generation context to test with
     * @param consumer Called with the found position and a {@link StructurePiecesBuilder}
     * @return The {@link Structure.GenerationStub}, or {@link Optional#empty()} if no floor was found
     */
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

    /**
     * Searches downward from {@code startY} for the Nether's solid bedrock ceiling at {@code (x, z)} and,
     * if found, calls {@code consumer} with the air block position just below it.
     *
     * @param x        The x coordinate to search at
     * @param startY   The y coordinate to start searching downward from
     * @param z        The z coordinate to search at
     * @param ctx      The generation context to test with
     * @param consumer Called with the found position and a {@link StructurePiecesBuilder}
     * @return The {@link Structure.GenerationStub}, or {@link Optional#empty()} if no ceiling was found
     */
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

    /**
     * Like {@link #onNetherSurface(int, int, int, Structure.GenerationContext, BiConsumer)}, but tests
     * several {@code positions} at once and only accepts the location if their found floor heights differ
     * by at most {@code maxDeltaY} — used to reject placements on very uneven Nether floors. On success,
     * calls {@code consumer} with the lowest of the found heights, at {@code (x, z)}.
     *
     * @param x           The x coordinate of the accepted position
     * @param startY      The y coordinate to start searching downward from
     * @param z           The z coordinate of the accepted position
     * @param positions   The columns (typically the corners of the structure's bounding box) to test
     * @param maxDeltaY   The maximum allowed height difference between the tested columns
     * @param airAtOffset If non-zero, additionally requires air at this vertical offset from the found
     *                    floor
     * @param ctx         The generation context to test with
     * @param consumer    Called with the found position and a {@link StructurePiecesBuilder}
     * @return The {@link Structure.GenerationStub}, or {@link Optional#empty()} if no valid floor was
     * found
     */
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

    /**
     * Places the structure at the world-surface height (per {@code types}) at the center of the current
     * chunk.
     *
     * @param generationContext The generation context to test with
     * @param types             The heightmap type used to find the surface height
     * @param consumer          Called with the found position and a {@link StructurePiecesBuilder}
     * @return The {@link Structure.GenerationStub} (always present)
     */
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

    /**
     * Searches downward from {@code startY} to {@code ctx.heightAccessor().getMinY() + 4} for the first Y
     * level where at least {@code minMatches} of {@code testColumns} transition from a block matching
     * {@code testAir} to one matching {@code testSurface}. See
     * {@link #findYDownward(int, int, List, Structure.GenerationContext, Predicate, Predicate, int, int)}
     * for the full parameter description.
     *
     * @return The found Y level, or {@link #FAIL_HEIGHT} if none was found
     */
    public static int findYDownward(
            int startY,
            List<BlockPos> testColumns,
            Structure.GenerationContext ctx,
            Predicate<BlockState> testAir,
            Predicate<BlockState> testSurface,
            int minMatches,
            int airAtOffset
    ) {
        return findYDownward(
                startY, ctx.heightAccessor().getMinY() + 4,
                testColumns, ctx,
                testAir, testSurface, minMatches, airAtOffset
        );
    }

    /**
     * Searches downward from {@code startY} to {@code stopY} for the first Y level where at least
     * {@code minMatches} of {@code testColumns} transition from a block matching {@code testAir} to one
     * matching {@code testSurface}.
     *
     * @param startY       The y coordinate to start searching downward from
     * @param stopY        The y coordinate to stop searching at (exclusive)
     * @param testColumns  The columns to test
     * @param ctx          The generation context to test with
     * @param testAir      The predicate a column's previous (higher) block must match
     * @param testSurface  The predicate a column's current block must match, once {@code testAir} matched
     *                     the block above it
     * @param minMatches   The minimum number of {@code testColumns} that must match at the same Y level
     * @param airAtOffset  If non-zero, additionally requires at least {@code minMatches} columns to have
     *                     air at this vertical offset from the found level
     * @return The found Y level, or {@link #FAIL_HEIGHT} if none was found
     */
    public static int findYDownward(
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
                .map(p -> new NoiseColumnWithState(
                                ctx
                                        .chunkGenerator()
                                        .getBaseColumn(
                                                p.getX(), p.getZ(),
                                                ctx.heightAccessor(),
                                                ctx.randomState()
                                        ), startY
                        )
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

    /**
     * Tests {@link #hasValidBiomeAt(Structure.GenerationContext, int, int, int)} at a random height within
     * the world's build limits, at {@code (x, z)}. Useful when the eventual placement height is not yet
     * known, but an early, cheap biome rejection is desired.
     *
     * @param ctx The generation context to test with
     * @param x   The x coordinate to test at
     * @param z   The z coordinate to test at
     * @return {@code true} if the biome at the randomly picked height is a valid biome for this structure
     */
    public static boolean hasValidBiomeAtRandomHeight(Structure.GenerationContext ctx, int x, int z) {
        final int randomY = ctx.random()
                               .nextIntBetweenInclusive(
                                       ctx.heightAccessor().getMinY(),
                                       ctx.heightAccessor().getMaxY()
                               );

        return hasValidBiomeAt(ctx, x, randomY, z);
    }

    /**
     * Tests whether the biome at the given position matches {@link Structure.GenerationContext#validBiome()},
     * i.e. whether the structure is allowed to generate in that biome.
     *
     * @param ctx The generation context to test with
     * @param x   The x coordinate to test at
     * @param y   The y coordinate to test at
     * @param z   The z coordinate to test at
     * @return {@code true} if the biome at the given position is a valid biome for this structure
     */
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

    /**
     * Computes the pivot point used to rotate/mirror {@code template} in place around its own center,
     * taking {@code mirror} into account (mirroring flips the sign of the corresponding half-extent).
     *
     * @param mirror   The mirror that will be applied together with this pivot
     * @param template The template whose size is used to compute the center
     * @return The center/pivot position, relative to the template's origin
     */
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
