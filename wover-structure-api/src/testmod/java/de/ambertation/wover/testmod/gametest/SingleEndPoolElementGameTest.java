package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.structure.impl.pools.SingleEndPoolElement;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * A structure piece must not read blocks outside the 3x3 chunks around the chunk being decorated (26.3 logs
 * "Detected unsafe terrain read during worldgen", 1.21.x reads silently). {@link SingleEndPoolElement} used to look at the blocks at
 * its origin before placing, no matter which chunk it was placing into - two chunks away for a long village
 * street - and could therefore also decide differently from chunk to chunk.
 * <p>
 * This places a one-block piece into chunks that do not contain its origin, through an in-memory
 * {@link WorldGenLevel} that records every position read. The terrain comes from a flat generator (end
 * stone up to {@link #SURFACE_Y}), so the "is the piece buried" decision is checked as well: it must not
 * read any block, must be the same for every chunk, and must still reject pieces inside the terrain.
 */
public class SingleEndPoolElementGameTest {
    /** First air block above the flat terrain. */
    private static final int SURFACE_Y = 16;

    @GameTest
    public void placeReadsNoTerrainAndDecidesTheSameInEveryChunk(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final ChunkGenerator generator = flatGenerator(level, SURFACE_Y - level.getMinY());

        final BlockPos templatePos = helper.absolutePos(new BlockPos(1, 1, 1));
        helper.setBlock(new BlockPos(1, 1, 1), Blocks.STONE);
        final StructureTemplate template = new StructureTemplate();
        template.fillFromWorld(level, templatePos, new Vec3i(1, 1, 1), false, List.of());
        final SingleEndPoolElement element = new SingleEndPoolElement(
                Either.right(template),
                Holder.direct(new StructureProcessorList(List.of())),
                StructureTemplatePool.Projection.RIGID
        );

        final List<String> failures = new ArrayList<>();
        // origin y -> expected outcome: buried, partially buried (air two above), on the surface, too low
        check(helper, element, generator, SURFACE_Y - 6, false, failures);
        check(helper, element, generator, SURFACE_Y - 2, true, failures);
        check(helper, element, generator, SURFACE_Y, true, failures);
        check(helper, element, generator, 3, false, failures);

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(String.join("; ", failures)));
        }
        helper.succeed();
    }

    private static void check(
            GameTestHelper helper,
            SingleEndPoolElement element,
            ChunkGenerator generator,
            int y,
            boolean expected,
            List<String> failures
    ) {
        final BlockPos origin = new BlockPos(0, y, 0);
        // one and two chunks east of the origin, as for the far end of a long street piece
        for (int chunkX = 1; chunkX <= 2; chunkX++) {
            final BoundingBox chunkBox = new BoundingBox(chunkX * 16, -64, 0, chunkX * 16 + 15, 319, 15);
            final Set<BlockPos> reads = new HashSet<>();
            final boolean placed = element.place(
                    helper.getLevel().getServer().getStructureManager(),
                    recordingLevel(helper.getLevel(), reads),
                    helper.getLevel().structureManager(),
                    generator,
                    origin,
                    origin,
                    Rotation.NONE,
                    chunkBox,
                    RandomSource.create(0),
                    LiquidSettings.IGNORE_WATERLOGGING,
                    false
            );
            if (placed != expected) {
                failures.add("origin " + origin + " in chunk [" + chunkX + ", 0]: placed=" + placed + ", expected " + expected);
            }
            if (!reads.isEmpty()) {
                failures.add("origin " + origin + " in chunk [" + chunkX + ", 0]: read " + reads.iterator().next());
            }
        }
    }

    private static ChunkGenerator flatGenerator(ServerLevel level, int layers) {
        final FlatLevelGeneratorSettings defaults = FlatLevelGeneratorSettings.getDefault(
                level.registryAccess().lookupOrThrow(Registries.BIOME),
                level.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET),
                level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE)
        );
        return new FlatLevelSource(defaults.withBiomeAndLayers(
                List.of(new FlatLayerInfo(layers, Blocks.END_STONE)),
                Optional.empty(),
                defaults.getBiome()
        ));
    }

    /**
     * Forwards everything to {@code level} except block and fluid reads, which are recorded in {@code reads}
     * and answered as air, and writes, which are dropped.
     */
    private static WorldGenLevel recordingLevel(ServerLevel level, Set<BlockPos> reads) {
        final InvocationHandler handler = (proxy, method, args) -> {
            final int argc = args == null ? 0 : args.length;
            switch (method.getName()) {
                case "getBlockState", "getFluidState", "isEmptyBlock", "getBlockEntity" -> {
                    reads.add(((BlockPos) args[0]).immutable());
                    return switch (method.getName()) {
                        case "getBlockState" -> Blocks.AIR.defaultBlockState();
                        case "getFluidState" -> Blocks.AIR.defaultBlockState().getFluidState();
                        case "isEmptyBlock" -> true;
                        default -> null;
                    };
                }
                case "setBlock" -> {
                    return true;
                }
                case "getLevel" -> {
                    return level;
                }
                case "equals" -> {
                    return proxy == (argc > 0 ? args[0] : null);
                }
                case "hashCode" -> {
                    return System.identityHashCode(proxy);
                }
                case "toString" -> {
                    return "RecordingWorldGenLevel";
                }
                default -> {
                }
            }
            return method.invoke(level, args);
        };
        return (WorldGenLevel) Proxy.newProxyInstance(
                SingleEndPoolElementGameTest.class.getClassLoader(),
                new Class<?>[]{WorldGenLevel.class},
                handler
        );
    }
}
