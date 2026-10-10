package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.generator.api.biomesource.WoverBiomePicker;
import de.ambertation.wover.util.RandomizedWeightedList;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Guards issue #36: the same seed produced the same biome <i>layout</i> but different biome <i>selection</i>
 * after a restart.
 * <p>
 * {@link WoverBiomePicker#rebuild()} turns its Biomes into a weighted search tree, and the order they are
 * added decides which weight sub-range (and thus which cells of the map) each Biome gets. That order used
 * to come from a {@code HashSet} keyed (ultimately) on {@link ResourceKey}, which hashes by JVM identity -
 * a different order on every boot, but a stable one within a single JVM. That is why a same-JVM comparison
 * of two pickers can not catch the bug: this test instead pins the tree to the one order that is a pure
 * function of the Biome set, ascending Identifier, by comparing against a reference list built in that
 * order. It also feeds the same Biomes in reversed order to make sure the order {@code addBiome()} was
 * called in does not matter (third-party callers do not go through the sorted populate walk).
 */
public class BiomePickerOrderGameTest {
    private static final List<ResourceKey<Biome>> BIOMES = List.of(
            Biomes.NETHER_WASTES,
            Biomes.SOUL_SAND_VALLEY,
            Biomes.CRIMSON_FOREST,
            Biomes.WARPED_FOREST,
            Biomes.BASALT_DELTAS,
            Biomes.THE_END,
            Biomes.END_HIGHLANDS,
            Biomes.END_MIDLANDS,
            Biomes.SMALL_END_ISLANDS,
            Biomes.END_BARRENS
    );
    private static final int SAMPLES = 2048;
    private static final long SEED = 976933332L;

    @GameTest
    public void pickerOrderIsIndependentOfInsertionAndHashOrder(GameTestHelper helper) {
        final HolderLookup<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);

        final List<ResourceKey<Biome>> sorted = new ArrayList<>(BIOMES);
        sorted.sort(Comparator.comparing(key -> key.identifier().toString()));
        final List<ResourceKey<Biome>> reversed = new ArrayList<>(sorted).reversed();

        final RandomizedWeightedList<ResourceKey<Biome>> reference = new RandomizedWeightedList<>();
        sorted.forEach(key -> reference.add(key, BiomeData.tempOf(key).genChance()));
        final RandomizedWeightedList<ResourceKey<Biome>>.SearchTree referenceTree = reference.buildSearchTree();

        final WoverBiomePicker forward = build(biomes, sorted);
        final WoverBiomePicker backward = build(biomes, reversed);

        final WorldgenRandom rRef = new WorldgenRandom(new LegacyRandomSource(SEED));
        final WorldgenRandom rFwd = new WorldgenRandom(new LegacyRandomSource(SEED));
        final WorldgenRandom rBwd = new WorldgenRandom(new LegacyRandomSource(SEED));

        for (int i = 0; i < SAMPLES; i++) {
            final ResourceKey<Biome> expected = referenceTree.getRandomValue(rRef);
            final ResourceKey<Biome> fwd = forward.getBiome(rFwd).biomeData.biomeKey;
            final ResourceKey<Biome> bwd = backward.getBiome(rBwd).biomeData.biomeKey;

            if (fwd != expected) {
                helper.fail("Sample " + i + ": picker chose " + fwd.identifier() + " but the Identifier-ordered "
                        + "reference chose " + expected.identifier() + " - the search tree is not built in a "
                        + "boot-independent order");
                return;
            }
            if (bwd != fwd) {
                helper.fail("Sample " + i + ": adding the same Biomes in reverse order changed the pick ("
                        + fwd.identifier() + " vs " + bwd.identifier() + ")");
                return;
            }
        }

        helper.succeed();
    }

    private static WoverBiomePicker build(HolderLookup<Biome> biomes, List<ResourceKey<Biome>> order) {
        final WoverBiomePicker picker = new WoverBiomePicker(biomes, Biomes.NETHER_WASTES);
        order.forEach(key -> picker.addBiome(BiomeData.tempOf(key)));
        picker.rebuild();
        return picker;
    }
}
