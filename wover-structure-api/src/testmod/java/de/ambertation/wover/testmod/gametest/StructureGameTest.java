package de.ambertation.wover.testmod.gametest;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
            if (!structures.containsKey(ResourceLocation.parse(id))) {
                failures.add(id + ": expected structure loaded from datapack JSON but it is missing");
            }
        }

        for (String id : DATAPACK_STRUCTURE_SETS) {
            if (!structureSets.containsKey(ResourceLocation.parse(id))) {
                failures.add(id + ": expected structure_set loaded from datapack JSON but it is missing");
            }
        }

        if (!structureTypes.containsKey(ResourceLocation.parse(PROGRAMMATIC_STRUCTURE_TYPE))) {
            failures.add(PROGRAMMATIC_STRUCTURE_TYPE
                    + ": expected structure type registered programmatically at mod-init but it is missing");
        }

        // Negative guard: an id the testmod never registers must not be present.
        if (structures.containsKey(ResourceLocation.parse(ABSENT_STRUCTURE))) {
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
}
