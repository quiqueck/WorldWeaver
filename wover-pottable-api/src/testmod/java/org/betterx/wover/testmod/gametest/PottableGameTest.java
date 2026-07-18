package org.betterx.wover.testmod.gametest;

import org.betterx.wover.pottable.api.PottablePlant;
import org.betterx.wover.pottable.api.PottablePlantRegistry;
import org.betterx.wover.pottable.api.PottableSoil;
import org.betterx.wover.pottable.api.PottableSoilRegistry;
import org.betterx.wover.testmod.entrypoint.TestModWoverPottable;

import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    private static final ResourceLocation ABSENT_PLANT =
            ResourceLocation.parse("wover-pottable-testmod:absent-plant");

    @GameTest
    public void pottablePlantAndSoilResolveAtRuntime(GameTestHelper helper) {
        final var access = helper.getLevel().registryAccess();
        final Registry<PottablePlant> plants = access.lookupOrThrow(PottablePlantRegistry.POTTABLE_PLANT_REGISTRY);
        final Registry<PottableSoil> soils = access.lookupOrThrow(PottableSoilRegistry.POTTABLE_SOIL_REGISTRY);

        final List<String> failures = new ArrayList<>();

        // Positive: the injected plant resolves and still maps to the FERN block.
        final Optional<PottablePlant> plant = plants.getOptional(TestModWoverPottable.TEST_PLANT);
        if (plant.isEmpty()) {
            failures.add(TestModWoverPottable.TEST_PLANT.location()
                    + ": expected pottable plant registered via addBootstrap but it is missing");
        } else {
            if (!plant.get().block.equals(Blocks.FERN.builtInRegistryHolder().key())) {
                failures.add(TestModWoverPottable.TEST_PLANT.location()
                        + ": plant->block mapping drifted, expected minecraft:fern but was " + plant.get().block.location());
            }
            // Empty validSoils means "any registered soil" - the PODZOL soil must be accepted.
            if (!plant.get().isValidSoil(Blocks.PODZOL)) {
                failures.add(TestModWoverPottable.TEST_PLANT.location()
                        + ": expected plant with empty validSoils to accept the PODZOL soil, but it did not");
            }
        }

        // Positive: the injected soil resolves and still maps to the PODZOL block.
        final Optional<PottableSoil> soil = soils.getOptional(TestModWoverPottable.TEST_SOIL);
        if (soil.isEmpty()) {
            failures.add(TestModWoverPottable.TEST_SOIL.location()
                    + ": expected pottable soil registered via addBootstrap but it is missing");
        } else if (!soil.get().block.equals(Blocks.PODZOL.builtInRegistryHolder().key())) {
            failures.add(TestModWoverPottable.TEST_SOIL.location()
                    + ": soil->block mapping drifted, expected minecraft:podzol but was " + soil.get().block.location());
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
}
