package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeDataRegistry;
import de.ambertation.wover.biome.impl.data.BiomeDataRegistryImpl;
import de.ambertation.wover.generator.api.biomesource.WoverBiomePicker;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import java.util.List;
import java.util.Set;

/**
 * Vanilla Nether/End Biomes must compete with the same weight ({@code genChance}) as Biomes of mods that do
 * not ship WoVer data (e.g. Biomes O' Plenty). Their data used to set {@code 0.01}, which made the vanilla
 * Nether Biomes (and every sub-biome of them) practically disappear as soon as another mod added Nether
 * Biomes.
 */
public class VanillaBiomeWeightGameTest {
    private static final int SAMPLES = 20_000;
    private static final long SEED = 263L;
    private static final float EPSILON = 1e-5f;

    private static final List<ResourceKey<Biome>> VANILLA_NETHER = List.of(
            Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST,
            Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS
    );
    private static final List<ResourceKey<Biome>> VANILLA_END_TOP_LEVEL = List.of(
            Biomes.END_HIGHLANDS, Biomes.THE_END, Biomes.SMALL_END_ISLANDS
    );
    // stand-ins for Biomes of another mod: bound Biomes without any WoVer data
    private static final List<ResourceKey<Biome>> FOREIGN = List.of(
            Biomes.PLAINS, Biomes.DESERT, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP
    );

    @GameTest
    public void vanillaTopLevelBiomesUseForeignDefault(GameTestHelper helper) {
        final Registry<BiomeData> data = dataRegistry(helper);
        final float foreignDefault = BiomeData.tempOf(Biomes.PLAINS).genChance();

        for (ResourceKey<Biome> key : concat(VANILLA_NETHER, VANILLA_END_TOP_LEVEL)) {
            final BiomeData d = BiomeDataRegistryImpl.getFromRegistryOrTemp(data, key);
            if (d == null || d.isTemp()) {
                helper.fail("No WoVer data loaded for " + key.identifier());
                return;
            }
            if (Math.abs(d.genChance() - foreignDefault) > EPSILON) {
                helper.fail(key.identifier() + " has genChance " + d.genChance()
                        + ", but Biomes without WoVer data use " + foreignDefault);
                return;
            }
        }
        helper.succeed();
    }

    @GameTest
    public void endSubBiomesKeepTheirRatioToTheHighlands(GameTestHelper helper) {
        final Registry<BiomeData> data = dataRegistry(helper);
        final float highlands = BiomeDataRegistryImpl.getFromRegistryOrTemp(data, Biomes.END_HIGHLANDS).genChance();
        final float midlands = BiomeDataRegistryImpl.getFromRegistryOrTemp(data, Biomes.END_MIDLANDS).genChance();
        final float barrens = BiomeDataRegistryImpl.getFromRegistryOrTemp(data, Biomes.END_BARRENS).genChance();

        if (Math.abs(midlands / highlands - 0.25f) > EPSILON || Math.abs(barrens / highlands - 0.15f) > EPSILON) {
            helper.fail("Expected highlands : midlands : barrens = 1 : 0.25 : 0.15, got "
                    + highlands + " : " + midlands + " : " + barrens);
            return;
        }
        helper.succeed();
    }

    @GameTest
    public void vanillaNetherAndForeignBiomesArePickedEquallyOften(GameTestHelper helper) {
        final Registry<BiomeData> data = dataRegistry(helper);
        final HolderLookup<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        final WoverBiomePicker picker = new WoverBiomePicker(biomes, Biomes.NETHER_WASTES);

        VANILLA_NETHER.forEach(key -> picker.addBiome(BiomeDataRegistryImpl.getFromRegistryOrTemp(data, key)));
        FOREIGN.forEach(key -> picker.addBiome(BiomeData.tempOf(key)));
        picker.rebuild();

        final Set<ResourceKey<Biome>> vanilla = Set.copyOf(VANILLA_NETHER);
        final WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(SEED));
        int vanillaPicks = 0;
        for (int i = 0; i < SAMPLES; i++) {
            if (vanilla.contains(picker.getBiome(random).biomeData.biomeKey)) vanillaPicks++;
        }

        final double share = vanillaPicks / (double) SAMPLES;
        if (Math.abs(share - 0.5) > 0.03) {
            helper.fail("5 vanilla + 5 foreign Nether Biomes: vanilla share is " + share + ", expected ~0.5");
            return;
        }
        helper.succeed();
    }

    private static Registry<BiomeData> dataRegistry(GameTestHelper helper) {
        return helper.getLevel().registryAccess().lookupOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);
    }

    private static List<ResourceKey<Biome>> concat(List<ResourceKey<Biome>> a, List<ResourceKey<Biome>> b) {
        return java.util.stream.Stream.concat(a.stream(), b.stream()).toList();
    }
}
