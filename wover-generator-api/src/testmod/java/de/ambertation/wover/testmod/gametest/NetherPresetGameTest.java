package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeDataRegistry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Guards the position of our Datapack registries inside {@code WORLDGEN_REGISTRIES}.
 * <p>
 * {@code BiomeSourceManagerImpl} hands every Nether {@link BiomeData} that carries climate parameter
 * points to Fabric's {@code NetherBiomes.addNetherBiome}, from the {@code biome_data} registry's
 * element callback. Fabric appends that table to {@code minecraft:nether} while the parameter list
 * registry decodes - and {@code RegistryDataLoader} walks the registry list strictly in order here,
 * so if {@code biome_data} is positioned after the parameter list (as it was when our registries
 * were simply appended), the preset is baked before we have written anything into that table and
 * comes out with the five vanilla Nether Biomes and nothing else, on every boot.
 * <p>
 * {@code RegistryDataLoaderMixin} therefore inserts our registries in front of
 * {@link MultiNoiseBiomeSourceParameterLists#NETHER}. This test reads the registry the running world
 * actually uses, which is exactly the one that was wrong.
 * <p>
 * The expectation is derived from the {@code biome_data} registry rather than hard-coded, so it
 * keeps up with the testmod's data. The negative half guards against the whole thing passing
 * vacuously if the testmod ever stops contributing a Nether Biome with parameter points.
 * <p>
 * 26.1+ loads these registries concurrently, where no list order can express the dependency; those
 * branches solve it with {@code DatapackRegistryLoadOrder.awaitElements} and carry the same test.
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

        final Set<ResourceLocation> bakedIntoPreset = netherPreset
                .parameters()
                .values()
                .stream()
                .map(entry -> ((Holder<Biome>) entry.getSecond()).unwrapKey())
                .filter(Optional::isPresent)
                .map(key -> key.get().location())
                .collect(Collectors.toSet());

        // Everything the testmod asks to be placed in the Nether *and* gives climate parameters to.
        // Without parameter points there is nothing to add to the preset, so those are not expected.
        final Set<ResourceLocation> expected = biomeData
                .entrySet()
                .stream()
                .filter(e -> e.getKey().location().getNamespace().equals(TESTMOD_NAMESPACE))
                .filter(e -> e.getValue().isIntendedFor(BiomeTags.IS_NETHER))
                .filter(e -> !e.getValue().generationData.parameterPoints().isEmpty())
                .map(e -> e.getKey().location())
                .collect(Collectors.toSet());

        if (expected.isEmpty()) {
            helper.fail(Component.literal(
                    "The testmod no longer contributes a Nether BiomeData with parameter points, "
                            + "so this test can no longer detect the ordering bug it exists for."
            ));
            return;
        }

        for (ResourceLocation biome : expected) {
            if (!bakedIntoPreset.contains(biome)) {
                helper.fail(Component.literal(
                        "minecraft:nether is missing " + biome
                                + " - our registries were baked into the preset too late. "
                                + "Baked: " + bakedIntoPreset.stream()
                                                             .map(ResourceLocation::toString)
                                                             .sorted()
                                                             .collect(Collectors.joining(", "))
                ));
                return;
            }
        }

        // The vanilla entries must survive the merge, otherwise "everything is present" would be
        // true of a preset that simply replaced the vanilla list with ours.
        final ResourceKey<Biome> netherWastes = ResourceKey.create(
                Registries.BIOME,
                ResourceLocation.withDefaultNamespace("nether_wastes")
        );
        if (!bakedIntoPreset.contains(netherWastes.location())) {
            helper.fail(Component.literal("minecraft:nether lost its vanilla entries"));
            return;
        }

        helper.succeed();
    }
}
