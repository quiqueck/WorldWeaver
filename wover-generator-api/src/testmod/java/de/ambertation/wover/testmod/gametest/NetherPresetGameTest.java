package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeDataRegistry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;

import java.util.Set;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Guards the ordering between the {@code wover:wover/worldgen/biome_data} registry and
 * {@code minecraft:worldgen/multi_noise_biome_source_parameter_list}.
 * <p>
 * {@code BiomeSourceManagerImpl} hands every Nether {@link BiomeData} that carries climate parameter
 * points to Fabric's {@code NetherBiomes.addNetherBiome}, from the {@code biome_data} registry's
 * element callback. Fabric appends that table to {@code minecraft:nether} while the parameter list
 * registry decodes - and both registries load concurrently, so without
 * {@code DatapackRegistryLoadOrder.awaitElements} the preset captured whatever subset of our writes
 * happened to be in the table at that instant. It usually captured none of them.
 * <p>
 * The registry this reads is the one the running world actually uses, which is exactly the one that
 * was wrong: on a dedicated server the preset came out with only the 5 vanilla entries on most
 * boots, so this test would have failed most of the time and passed on the rest.
 * <p>
 * The expectation is derived from the {@code biome_data} registry rather than hard-coded, so it
 * keeps up with the testmod's data. The negative half guards against the whole thing passing
 * vacuously if the testmod ever stops contributing a Nether Biome with parameter points.
 */
public class NetherPresetGameTest {
    private static final String TESTMOD_NAMESPACE = "wover-generator-testmod";

    @GameTest
    public void netherPresetContainsEveryModdedNetherBiome(GameTestHelper helper) {
        final Registry<BiomeData> biomeData = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);

        final MultiNoiseBiomeSourceParameterList netherPreset = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)
                .getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER)
                .value();

        final Set<Identifier> bakedIntoPreset = netherPreset
                .parameters()
                .values()
                .stream()
                .map(entry -> ((Holder<Biome>) entry.getSecond()).unwrapKey())
                .filter(java.util.Optional::isPresent)
                .map(key -> key.get().identifier())
                .collect(Collectors.toSet());

        // Everything the testmod asks to be placed in the Nether *and* gives climate parameters to.
        // Without parameter points there is nothing to add to the preset, so those are not expected.
        final Set<Identifier> expected = biomeData
                .entrySet()
                .stream()
                .filter(e -> e.getKey().identifier().getNamespace().equals(TESTMOD_NAMESPACE))
                .filter(e -> e.getValue().isIntendedFor(BiomeTags.IS_NETHER))
                .filter(e -> !e.getValue().generationData.parameterPoints().isEmpty())
                .map(e -> e.getKey().identifier())
                .collect(Collectors.toSet());

        if (expected.isEmpty()) {
            helper.fail(
                    "The testmod no longer contributes a Nether BiomeData with parameter points, "
                            + "so this test can no longer detect the ordering bug it exists for."
            );
            return;
        }

        for (Identifier biome : expected) {
            if (!bakedIntoPreset.contains(biome)) {
                helper.fail(
                        "minecraft:nether is missing " + biome
                                + " - biome_data was not fully registered before the parameter list was baked. "
                                + "Baked: " + bakedIntoPreset.stream()
                                                             .map(Identifier::toString)
                                                             .sorted()
                                                             .collect(Collectors.joining(", "))
                );
                return;
            }
        }

        // The vanilla entries must survive the merge, otherwise "everything is present" would be
        // true of a preset that simply replaced the vanilla list with ours.
        final ResourceKey<Biome> netherWastes = ResourceKey.create(
                Registries.BIOME,
                Identifier.withDefaultNamespace("nether_wastes")
        );
        if (!bakedIntoPreset.contains(netherWastes.identifier())) {
            helper.fail("minecraft:nether lost its vanilla entries");
            return;
        }

        helper.succeed();
    }
}
