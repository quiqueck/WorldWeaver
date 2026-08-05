package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.pottable.api.PottablePlant;
import de.ambertation.wover.pottable.api.PottablePlantRegistry;
import de.ambertation.wover.pottable.api.PottableSoil;
import de.ambertation.wover.pottable.api.PottableSoilRegistry;
import de.ambertation.wover.pottable.api.trait.PottablePlantBlockTrait;
import de.ambertation.wover.pottable.api.trait.PottableSoilBlockTrait;
import de.ambertation.wover.testmod.block.TestPottableBlocks;
import de.ambertation.wover.testmod.entrypoint.TestModWoverPottable;

import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for the pottable-api: "a plant/soil marked pottable actually resolves at runtime".
 * <p>
 * Unlike vanilla flower pots (which use {@code FlowerPotBlock}'s static potted-block map), this
 * module stores pottability in two datapack-driven registries - {@code wover/pottable_plant} and
 * {@code wover/pottable_soil}. The testmod contributes one entry to each at bootstrap
 * (see {@code TestModPottableDatapackRegistryEntrypoint}, via
 * {@code DatapackRegistryBuilder.addBootstrap}): {@code test-plant} -> {@link Blocks#FERN} on any
 * soil, and {@code test-soil} -> {@link Blocks#PODZOL}.
 * <p>
 * Once the server is up this test reads both registries back from {@code registryAccess()} and
 * asserts:
 * <ul>
 *   <li>the entries resolve by their {@code ResourceKey};</li>
 *   <li>the stored plant->block mapping is preserved ({@code test-plant} really maps to {@code FERN},
 *       {@code test-soil} to {@code PODZOL});</li>
 *   <li>the plant->soil relation behaves ({@code test-plant} with empty {@code validSoils} accepts
 *       the {@code PODZOL} soil).</li>
 * </ul>
 * The failable negative case asserts a plausible-but-never-registered id
 * ({@code wover-pottable-testmod:absent-plant}) is <em>not</em> present, so a registry that silently
 * accepted everything would fail. A missing entry is a real regression in datapack-registry bootstrap
 * or in the pottable registries themselves.
 */
public class PottableGameTest {
    // An id that is never registered anywhere - the registry must NOT contain it.
    private static final Identifier ABSENT_PLANT =
            Identifier.parse("wover-pottable-testmod:absent-plant");

    @GameTest
    public void pottablePlantAndSoilResolveAtRuntime(GameTestHelper helper) {
        final var access = helper.getLevel().registryAccess();
        final Registry<PottablePlant> plants = access.lookupOrThrow(PottablePlantRegistry.POTTABLE_PLANT_REGISTRY);
        final Registry<PottableSoil> soils = access.lookupOrThrow(PottableSoilRegistry.POTTABLE_SOIL_REGISTRY);

        final List<String> failures = new ArrayList<>();

        // Positive: the injected plant resolves and still maps to the FERN block.
        final Optional<PottablePlant> plant = plants.getOptional(TestModWoverPottable.TEST_PLANT);
        if (plant.isEmpty()) {
            failures.add(TestModWoverPottable.TEST_PLANT.identifier()
                    + ": expected pottable plant registered via addBootstrap but it is missing");
        } else {
            if (!plant.get().block.equals(Blocks.FERN.builtInRegistryHolder().key())) {
                failures.add(TestModWoverPottable.TEST_PLANT.identifier()
                        + ": plant->block mapping drifted, expected minecraft:fern but was " + plant.get().block.identifier());
            }
            // Empty validSoils means "any registered soil" - the PODZOL soil must be accepted.
            if (!plant.get().isValidSoil(Blocks.PODZOL)) {
                failures.add(TestModWoverPottable.TEST_PLANT.identifier()
                        + ": expected plant with empty validSoils to accept the PODZOL soil, but it did not");
            }
        }

        // Positive: the injected soil resolves and still maps to the PODZOL block.
        final Optional<PottableSoil> soil = soils.getOptional(TestModWoverPottable.TEST_SOIL);
        if (soil.isEmpty()) {
            failures.add(TestModWoverPottable.TEST_SOIL.identifier()
                    + ": expected pottable soil registered via addBootstrap but it is missing");
        } else if (!soil.get().block.equals(Blocks.PODZOL.builtInRegistryHolder().key())) {
            failures.add(TestModWoverPottable.TEST_SOIL.identifier()
                    + ": soil->block mapping drifted, expected minecraft:podzol but was " + soil.get().block.identifier());
        }

        // Negative: an id nobody registered must not be present.
        if (plants.containsKey(ABSENT_PLANT)) {
            failures.add(ABSENT_PLANT + ": unregistered plant id unexpectedly present in the registry");
        }

        if (!failures.isEmpty()) {
            final List<String> presentPlants = new ArrayList<>();
            plants.keySet().forEach(k -> presentPlants.add(k.toString()));
            final List<String> presentSoils = new ArrayList<>();
            soils.keySet().forEach(k -> presentSoils.add(k.toString()));
            helper.fail(Component.literal(
                    "Pottable-registry regression:\n - " + String.join("\n - ", failures)
                            + "\n\nPlants present: " + presentPlants
                            + "\nSoils present: " + presentSoils
            ));
            return;
        }

        helper.succeed();
    }

    /**
     * Covers the trait-driven path: a block carrying a {@link PottablePlantBlockTrait}/
     * {@link PottableSoilBlockTrait} must end up as a registry entry keyed by the block's own id, and a
     * block carrying its trait <em>twice</em> must produce exactly one entry, built from the trait added
     * last.
     * <p>
     * The double-trait case is the interesting one, and it fails <em>quietly</em> when broken. The registry
     * key of a {@code PottablePlant} comes only from the block id, so before
     * {@link PottablePlantBlockTrait#keepLatestOnly()} was declared,
     * {@code .addTrait(any()).addTrait(withSoils(tag))} kept both traits and the sweep registered the same
     * key twice. Nothing crashed: {@code DatapackRegistryBuilder}'s {@code BootstrapContext} returns the
     * already-bound holder for a key it has seen, so the <em>first</em> registration won and the later,
     * narrower one was dropped without a log line. Note {@code BlockTraitKey.ofUnique} does not prevent it -
     * it only makes the key <em>object</em> unique.
     * <p>
     * That is why "latest wins" is asserted in both directions ({@code DOUBLE_TRAIT_PLANT} must accept
     * {@code COARSE_DIRT} and must reject {@code PODZOL}) rather than just checking the entry exists: the
     * regression leaves a perfectly valid entry in place, it is merely pottable on <em>any</em> soil instead
     * of the tag the author last asked for. A presence-only check passes straight through it - verified by
     * reverting the fix, which leaves all four entries present and fails only on the {@code PODZOL} half.
     */
    @GameTest
    public void traitDrivenEntriesAreRegisteredOncePerBlock(GameTestHelper helper) {
        final var access = helper.getLevel().registryAccess();
        final Registry<PottablePlant> plants = access.lookupOrThrow(PottablePlantRegistry.POTTABLE_PLANT_REGISTRY);
        final Registry<PottableSoil> soils = access.lookupOrThrow(PottableSoilRegistry.POTTABLE_SOIL_REGISTRY);

        final List<String> failures = new ArrayList<>();

        // --- keepLatestOnly() itself, asserted directly on the block ----------------------------
        // The registry assertions further down are also satisfied by bootstrapPottablePlants' own
        // "take the last match" guard, so they would still pass if only keepLatestOnly() regressed.
        // Checking the attached trait list pins that layer down on its own.
        expectSingleTrait(failures, TestPottableBlocks.DOUBLE_TRAIT_PLANT, PottablePlantBlockTrait.KEY,
                "DOUBLE_TRAIT_PLANT");
        expectSingleTrait(failures, TestPottableBlocks.DOUBLE_TRAIT_SOIL, PottableSoilBlockTrait.KEY,
                "DOUBLE_TRAIT_SOIL");

        // --- the plain, single-trait case -------------------------------------------------------
        expectPlantForBlock(failures, plants, TestPottableBlocks.TRAIT_PLANT, "TRAIT_PLANT")
                .ifPresent(plant -> {
                    // any() means no restriction at all
                    if (plant.validSoils.isPresent()) {
                        failures.add("TRAIT_PLANT: expected PottablePlantBlockTrait.any() to leave validSoils"
                                + " empty, but it was " + plant.validSoils.get());
                    }
                });
        expectSoilForBlock(failures, soils, TestPottableBlocks.TRAIT_SOIL, "TRAIT_SOIL");

        // --- the double-trait case: exactly one entry, built from the LAST trait -----------------
        expectPlantForBlock(failures, plants, TestPottableBlocks.DOUBLE_TRAIT_PLANT, "DOUBLE_TRAIT_PLANT")
                .ifPresent(plant -> {
                    if (plant.validSoils.isEmpty()) {
                        failures.add("DOUBLE_TRAIT_PLANT: the trait added last restricts soils to"
                                + " minecraft:dirt, but validSoils is empty - de-duplication kept the FIRST"
                                + " trait (any()) instead of the latest, widening the allowed soils");
                    } else {
                        if (!plant.isValidSoil(Blocks.COARSE_DIRT)) {
                            failures.add("DOUBLE_TRAIT_PLANT: expected COARSE_DIRT (in minecraft:dirt) to be"
                                    + " a valid soil, but it was rejected");
                        }
                        if (plant.isValidSoil(Blocks.PODZOL)) {
                            failures.add("DOUBLE_TRAIT_PLANT: PODZOL is not in minecraft:dirt and must be"
                                    + " rejected - the latest trait's restriction is not in effect");
                        }
                    }
                });
        expectSoilForBlock(failures, soils, TestPottableBlocks.DOUBLE_TRAIT_SOIL, "DOUBLE_TRAIT_SOIL");

        if (!failures.isEmpty()) {
            final List<String> presentPlants = new ArrayList<>();
            plants.keySet().forEach(k -> presentPlants.add(k.toString()));
            final List<String> presentSoils = new ArrayList<>();
            soils.keySet().forEach(k -> presentSoils.add(k.toString()));
            helper.fail(Component.literal(
                    "Pottable trait-sweep regression:\n - " + String.join("\n - ", failures)
                            + "\n\nPlants present: " + presentPlants
                            + "\nSoils present: " + presentSoils
            ));
            return;
        }

        helper.succeed();
    }

    /**
     * Asserts {@code block} carries exactly one runtime trait under {@code key}, i.e. that
     * {@code keepLatestOnly()} collapsed the two {@code addTrait(...)} calls at definition time.
     */
    private static void expectSingleTrait(
            List<String> failures,
            Block block,
            BlockTraitKey key,
            String name
    ) {
        final var traits = BlockTrait.<Block, GenericBlockTrait>getRuntimeTraits(block, key);
        if (traits == null || traits.isEmpty()) {
            failures.add(name + ": expected one runtime trait under " + key + ", but the block carries none");
        } else if (traits.size() != 1) {
            failures.add(name + ": expected keepLatestOnly() to collapse the two addTrait(...) calls into a"
                    + " single trait under " + key + ", but the block carries " + traits.size());
        }
    }

    /** Looks up the plant entry the trait sweep should have created for {@code block}. */
    private static Optional<PottablePlant> expectPlantForBlock(
            List<String> failures,
            Registry<PottablePlant> plants,
            Block block,
            String name
    ) {
        final var blockKey = block.builtInRegistryHolder().key();
        final Optional<PottablePlant> plant =
                plants.getOptional(PottablePlantRegistry.createKey(blockKey.identifier()));
        if (plant.isEmpty()) {
            failures.add(name + ": expected the PottablePlantBlockTrait sweep to register an entry for "
                    + blockKey.identifier() + ", but none is present");
        } else if (!plant.get().block.equals(blockKey)) {
            failures.add(name + ": entry points at " + plant.get().block.identifier()
                    + " instead of " + blockKey.identifier());
        }
        return plant;
    }

    /** Looks up the soil entry the trait sweep should have created for {@code block}. */
    private static void expectSoilForBlock(
            List<String> failures,
            Registry<PottableSoil> soils,
            Block block,
            String name
    ) {
        final var blockKey = block.builtInRegistryHolder().key();
        final Optional<PottableSoil> soil =
                soils.getOptional(PottableSoilRegistry.createKey(blockKey.identifier()));
        if (soil.isEmpty()) {
            failures.add(name + ": expected the PottableSoilBlockTrait sweep to register an entry for "
                    + blockKey.identifier() + ", but none is present");
        } else if (!soil.get().block.equals(blockKey)) {
            failures.add(name + ": entry points at " + soil.get().block.identifier()
                    + " instead of " + blockKey.identifier());
        }
    }

    /**
     * Asserts the module's <em>documented on-disk contract</em>: that a plain datapack JSON file
     * actually deserializes into the two registries.
     * <p>
     * The sibling test above only covers the in-code {@code DatapackRegistryBuilder.addBootstrap}
     * path, which never touches {@link de.ambertation.wover.pottable.impl.PottablePlantImpl#CODEC}
     * nor the directory {@code RegistryDataLoader} derives from the registry key. Both of those are
     * exactly the things a Minecraft update can move silently: the codec still compiles when a field
     * is dropped, and the elements directory is computed from the registry's {@code Identifier}
     * rather than being written down anywhere. So this test reads back the two entries shipped as
     * real files under this testmod's {@code data/} directory:
     * <ul>
     *   <li>{@code json-plant} -&gt; {@link Blocks#DEAD_BUSH}, restricted to the {@code minecraft:dirt}
     *       soil tag;</li>
     *   <li>{@code json-soil} -&gt; {@link Blocks#COARSE_DIRT}.</li>
     * </ul>
     * The {@code valid_soils} assertions are deliberately two-sided. {@code minecraft:dirt} contains
     * {@code COARSE_DIRT} but <em>not</em> {@code PODZOL}, and an absent {@code valid_soils} means
     * "any soil". So a codec that silently dropped the optional tag would still produce a resolvable
     * entry and still pass a positive-only check - it would only show up as {@code PODZOL} suddenly
     * being accepted. Checking both directions is what makes this catch that.
     */
    @GameTest
    public void pottableEntriesLoadFromDatapackJson(GameTestHelper helper) {
        final var access = helper.getLevel().registryAccess();
        final Registry<PottablePlant> plants = access.lookupOrThrow(PottablePlantRegistry.POTTABLE_PLANT_REGISTRY);
        final Registry<PottableSoil> soils = access.lookupOrThrow(PottableSoilRegistry.POTTABLE_SOIL_REGISTRY);

        final List<String> failures = new ArrayList<>();

        final Optional<PottablePlant> plant = plants.getOptional(TestModWoverPottable.JSON_PLANT);
        if (plant.isEmpty()) {
            failures.add(TestModWoverPottable.JSON_PLANT.identifier()
                    + ": expected the plant shipped as a datapack JSON file to be loaded, but it is missing."
                    + " Either the elements directory derived from the registry key moved, or the file is"
                    + " no longer picked up as a datapack at all");
        } else {
            if (!plant.get().block.equals(Blocks.DEAD_BUSH.builtInRegistryHolder().key())) {
                failures.add(TestModWoverPottable.JSON_PLANT.identifier()
                        + ": plant->block mapping drifted, expected minecraft:dead_bush but was "
                        + plant.get().block.identifier());
            }
            if (plant.get().validSoils.isEmpty()) {
                failures.add(TestModWoverPottable.JSON_PLANT.identifier()
                        + ": the optional valid_soils field was dropped while decoding, expected the"
                        + " minecraft:dirt tag but got an empty Optional");
            } else {
                if (!plant.get().isValidSoil(Blocks.COARSE_DIRT)) {
                    failures.add(TestModWoverPottable.JSON_PLANT.identifier()
                            + ": expected COARSE_DIRT (a member of minecraft:dirt) to be a valid soil");
                }
                // The negative half: PODZOL is NOT in minecraft:dirt, so a plant that had lost its
                // valid_soils restriction would wrongly accept it.
                if (plant.get().isValidSoil(Blocks.PODZOL)) {
                    failures.add(TestModWoverPottable.JSON_PLANT.identifier()
                            + ": PODZOL is not in minecraft:dirt, so it must NOT be a valid soil -"
                            + " the valid_soils restriction is not being honoured");
                }
            }
        }

        final Optional<PottableSoil> soil = soils.getOptional(TestModWoverPottable.JSON_SOIL);
        if (soil.isEmpty()) {
            failures.add(TestModWoverPottable.JSON_SOIL.identifier()
                    + ": expected the soil shipped as a datapack JSON file to be loaded, but it is missing");
        } else if (!soil.get().block.equals(Blocks.COARSE_DIRT.builtInRegistryHolder().key())) {
            failures.add(TestModWoverPottable.JSON_SOIL.identifier()
                    + ": soil->block mapping drifted, expected minecraft:coarse_dirt but was "
                    + soil.get().block.identifier());
        }

        if (!failures.isEmpty()) {
            final List<String> presentPlants = new ArrayList<>();
            plants.keySet().forEach(k -> presentPlants.add(k.toString()));
            final List<String> presentSoils = new ArrayList<>();
            soils.keySet().forEach(k -> presentSoils.add(k.toString()));
            helper.fail(Component.literal(
                    "Pottable datapack-JSON regression:\n - " + String.join("\n - ", failures)
                            + "\n\nPlants present: " + presentPlants
                            + "\nSoils present: " + presentSoils
            ));
            return;
        }

        helper.succeed();
    }
}
