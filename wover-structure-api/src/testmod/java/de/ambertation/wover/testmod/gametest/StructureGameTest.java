package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructurePiece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for wover-structure-api: the structures, structure-sets and structure-types that the
 * testmod declares must all be present in the live registries once the server is up.
 * <p>
 * The testmod contributes structure content two distinct ways, and this test guards both:
 * <ul>
 *   <li><b>Datapack-loaded worldgen objects</b> — the {@link de.ambertation.wover.testmod.structure.datagen.StructureProvider}
 *       datagen writes committed JSON under {@code src/testmod/generated/data/.../worldgen/} which is loaded
 *       as data into the dynamic registries: {@link Registries#STRUCTURE} (test_structure, jigsaw_structure,
 *       rnd_structure) and {@link Registries#STRUCTURE_SET} (test_structure_set, rnd_structure). Their
 *       presence proves datapack loading + processing of the structure registries still works.</li>
 *   <li><b>Programmatically-registered structure type</b> — {@code test_structure} is a custom
 *       {@link Structure} subclass, so {@code StructureKeys.structure(..)} registers a bespoke
 *       {@link StructureType} into {@link Registries#STRUCTURE_TYPE} at mod-init time (not from any
 *       datapack JSON). Its presence proves the programmatic type registration still fires.</li>
 * </ul>
 * A missing entry is a real regression in structure datapack loading or in the type-registration path.
 * The negative assertion guards against the test silently passing on an always-true registry.
 */
public class StructureGameTest {
    private static final String NS = "wover-structure-testmod";

    // Structures loaded from the committed datapack JSON files (worldgen/structure/*.json).
    private static final List<String> DATAPACK_STRUCTURES = List.of(
            NS + ":test_structure",
            NS + ":jigsaw_structure",
            NS + ":rnd_structure"
    );

    // Structure-sets loaded from the committed datapack JSON files (worldgen/structure_set/*.json).
    private static final List<String> DATAPACK_STRUCTURE_SETS = List.of(
            NS + ":test_structure_set",
            NS + ":rnd_structure"
    );

    // Custom StructureType registered programmatically at mod-init (not from a datapack JSON), because
    // test_structure is a bespoke Structure subclass (TestStructure).
    private static final String PROGRAMMATIC_STRUCTURE_TYPE = NS + ":test_structure";

    // A plausible-but-absent structure id: never registered by the testmod. Its presence would mean the
    // containsKey check is meaningless, so this makes the test able to fail on a regression.
    private static final String ABSENT_STRUCTURE = NS + ":does_not_exist_structure";

    @GameTest
    public void structuresAreLoadedAndTypeRegistered(GameTestHelper helper) {
        final Registry<Structure> structures = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.STRUCTURE);
        final Registry<StructureSet> structureSets = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.STRUCTURE_SET);
        final Registry<StructureType<?>> structureTypes = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.STRUCTURE_TYPE);

        final List<String> failures = new ArrayList<>();

        for (String id : DATAPACK_STRUCTURES) {
            if (!structures.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected structure loaded from datapack JSON but it is missing");
            }
        }

        for (String id : DATAPACK_STRUCTURE_SETS) {
            if (!structureSets.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected structure_set loaded from datapack JSON but it is missing");
            }
        }

        if (!structureTypes.containsKey(Identifier.parse(PROGRAMMATIC_STRUCTURE_TYPE))) {
            failures.add(PROGRAMMATIC_STRUCTURE_TYPE
                    + ": expected structure type registered programmatically at mod-init but it is missing");
        }

        // Negative guard: an id the testmod never registers must not be present.
        if (structures.containsKey(Identifier.parse(ABSENT_STRUCTURE))) {
            failures.add(ABSENT_STRUCTURE + ": unexpectedly present - the containsKey assertions are not meaningful");
        }

        if (!failures.isEmpty()) {
            final List<String> presentStructures = new ArrayList<>();
            structures.keySet().forEach(k -> presentStructures.add(k.toString()));
            helper.fail(Component.literal(
                    "Structure registry regression:\n - " + String.join("\n - ", failures)
                            + "\n\nStructures actually present: " + presentStructures
            ));
            return;
        }

        helper.succeed();
    }

    /**
     * Guards {@link RandomNbtStructurePiece#placeDebugMarkers} and the 26.3 port of the marker blocks.
     * <p>
     * Two things this covers that nothing else does:
     * <ul>
     *   <li><b>The gate.</b> Until the 26.3 port the six debug marker blocks were written
     *       unconditionally, so they leaked into every {@code RandomNbtStructure} in normal gameplay.
     *       They are now gated. Nothing else in the build exercises {@code postProcess}, and the markers
     *       are never serialized, so datagen cannot catch a regression here.</li>
     *   <li><b>The block identity.</b> 26.3 folded copper into a {@code WeatheringCopperCollection}, so
     *       the flat {@code Blocks.OXIDIZED_COPPER} constant is gone and the code now navigates
     *       {@code COPPER_BLOCK.weathering().oxidized()}. Picking {@code waxed()} or a different weather
     *       stage compiles perfectly and would silently change what gets placed.</li>
     * </ul>
     * The piece is driven directly rather than through world generation, with a {@code writableBounds}
     * that does not intersect the template's footprint — so the template itself places nothing and the
     * only blocks written are the markers, which
     * {@link RandomNbtStructurePiece#postProcess} writes at the corners of {@code writableBounds}.
     */
    @GameTest
    public void randomNbtDebugMarkersAreGated(GameTestHelper helper) {
        final Block oxidizedCopper = Blocks.COPPER_BLOCK.weathering().oxidized();

        // The weathering()/oxidized() navigation must still land on the exact block the pre-26.3
        // Blocks.OXIDIZED_COPPER constant named.
        final Identifier oxidizedCopperId = BuiltInRegistries.BLOCK.getKey(oxidizedCopper);
        if (!Identifier.withDefaultNamespace("oxidized_copper").equals(oxidizedCopperId)) {
            helper.fail(Component.literal(
                    "Blocks.COPPER_BLOCK.weathering().oxidized() resolved to " + oxidizedCopperId
                            + ", expected minecraft:oxidized_copper - the debug markers would place the"
                            + " wrong block"
            ));
            return;
        }

        final List<String> failures = new ArrayList<>();

        // markers enabled -> all six must be written
        runPiece(helper, new BlockPos(0, 1, 0), true, oxidizedCopper, failures);
        // markers disabled -> none of the six may be written
        runPiece(helper, new BlockPos(4, 1, 4), false, oxidizedCopper, failures);

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "RandomNbtStructurePiece debug-marker regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }

    /**
     * Places a {@link RandomNbtStructurePiece} with {@code placeDebugMarkers} forced to {@code enabled},
     * then checks the six marker positions.
     */
    private static void runPiece(
            GameTestHelper helper,
            BlockPos relativeBase,
            boolean enabled,
            Block oxidizedCopper,
            List<String> failures
    ) {
        final ServerLevel level = helper.getLevel();
        final BlockPos base = helper.absolutePos(relativeBase);

        // A flat 3x1x3 box at base's own Y. The template is anchored three blocks higher, so it lies
        // completely outside this box and super.postProcess() places none of it - every block written
        // below is a debug marker.
        final BoundingBox writableBounds = new BoundingBox(
                base.getX(), base.getY(), base.getZ(),
                base.getX() + 2, base.getY(), base.getZ() + 2
        );
        final BlockPos templatePosition = base.offset(0, 3, 0);
        final BlockPos pivot = new BlockPos(0, 1, 0);

        final RandomNbtStructurePiece piece = new RandomNbtStructurePiece(
                level.getServer().getStructureManager(),
                Identifier.parse("wover-structure-testmod:simple"),
                RandomNbtStructurePiece.settings(Rotation.NONE, Mirror.NONE, pivot, false),
                templatePosition,
                false
        );

        final boolean previous = RandomNbtStructurePiece.placeDebugMarkers;
        RandomNbtStructurePiece.placeDebugMarkers = enabled;
        try {
            piece.postProcess(
                    level,
                    level.structureManager(),
                    level.getChunkSource().getGenerator(),
                    RandomSource.create(42),
                    writableBounds,
                    ChunkPos.containing(base),
                    base
            );
        } finally {
            RandomNbtStructurePiece.placeDebugMarkers = previous;
        }

        // The four bounding-box corners (oxidized copper), the template origin (amethyst) and the
        // rotation pivot (netherite) - see RandomNbtStructurePiece#postProcess.
        record Marker(String what, BlockPos pos, Block block) {}
        final List<Marker> markers = List.of(
                new Marker("corner -X-Z", new BlockPos(base.getX(), base.getY(), base.getZ()), oxidizedCopper),
                new Marker("corner +X-Z", new BlockPos(base.getX() + 2, base.getY(), base.getZ()), oxidizedCopper),
                new Marker("corner -X+Z", new BlockPos(base.getX(), base.getY(), base.getZ() + 2), oxidizedCopper),
                new Marker("corner +X+Z", new BlockPos(base.getX() + 2, base.getY(), base.getZ() + 2), oxidizedCopper),
                new Marker("template origin", templatePosition, Blocks.AMETHYST_BLOCK),
                new Marker("rotation pivot", templatePosition.offset(pivot), Blocks.NETHERITE_BLOCK)
        );

        for (Marker m : markers) {
            final Block actual = level.getBlockState(m.pos()).getBlock();
            if (enabled && actual != m.block()) {
                failures.add("placeDebugMarkers=true: " + m.what() + " at " + m.pos() + " is "
                        + BuiltInRegistries.BLOCK.getKey(actual) + ", expected "
                        + BuiltInRegistries.BLOCK.getKey(m.block()));
            } else if (!enabled && actual == m.block()) {
                failures.add("placeDebugMarkers=false: " + m.what() + " at " + m.pos() + " is "
                        + BuiltInRegistries.BLOCK.getKey(actual)
                        + " - debug markers leaked into a run that disabled them");
            }
        }
    }
}
