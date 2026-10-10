package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.biome.api.data.BiomeGenerationDataContainer;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeData;
import de.ambertation.wover.generator.api.biomesource.WoverBiomePicker;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Guards quiqueck/BCLib#216: a Biome whose data sets {@code genChance} to 0 is <i>disabled</i>. It used to
 * reach {@code RandomizedWeightedList#add}, which rejects weights {@code <= 0}, and crash the whole biome
 * source on world creation.
 */
public class DisabledBiomeGameTest {
    private static final int SAMPLES = 2048;
    private static final long SEED = 216L;

    @GameTest
    public void genChanceZeroIsNeverPicked(GameTestHelper helper) {
        final HolderLookup<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        final WoverBiomePicker picker = new WoverBiomePicker(biomes, Biomes.NETHER_WASTES);

        picker.addBiome(data(Biomes.CRIMSON_FOREST, 0.0f));
        picker.addBiome(data(Biomes.BASALT_DELTAS, -1.0f));
        picker.addBiome(data(Biomes.WARPED_FOREST, 1.0f));
        picker.rebuild();

        final WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(SEED));
        for (int i = 0; i < SAMPLES; i++) {
            final ResourceKey<Biome> picked = picker.getBiome(random).biomeData.biomeKey;
            if (picked != Biomes.WARPED_FOREST) {
                helper.fail(Component.literal("Sample " + i + ": picked " + picked.location() + ", but only warped_forest is enabled"));
                return;
            }
        }

        helper.succeed();
    }

    @GameTest
    public void onlyDisabledBiomesFallBack(GameTestHelper helper) {
        final HolderLookup<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        final WoverBiomePicker picker = new WoverBiomePicker(biomes, Biomes.NETHER_WASTES);

        picker.addBiome(data(Biomes.CRIMSON_FOREST, 0.0f));
        picker.rebuild();

        if (!picker.isEmpty()) {
            helper.fail(Component.literal("A picker holding only disabled Biomes must count as empty"));
            return;
        }
        final ResourceKey<Biome> picked = picker.getBiome(new WorldgenRandom(new LegacyRandomSource(SEED)))
                                                .biomeData.biomeKey;
        if (picked != Biomes.NETHER_WASTES) {
            helper.fail(Component.literal("Expected the fallback nether_wastes, but picked " + picked.location()));
            return;
        }

        helper.succeed();
    }

    private static WoverBiomeData data(ResourceKey<Biome> key, float genChance) {
        return new WoverBiomeData(1.0f, key, BiomeGenerationDataContainer.EMPTY, 0.1f, genChance, 4, false, null, null);
    }
}
