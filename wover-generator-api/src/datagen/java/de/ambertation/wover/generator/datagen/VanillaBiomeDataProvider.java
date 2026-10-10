package de.ambertation.wover.generator.datagen;

import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.datagen.api.provider.multi.WoverBiomeProvider;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeBuilder;

import net.minecraft.world.level.biome.Biomes;

public class VanillaBiomeDataProvider extends WoverBiomeProvider {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public VanillaBiomeDataProvider(
            ModCore modCore
    ) {
        super(modCore);
    }

    /**
     * The weight every Biome without WoVer data gets (see {@link de.ambertation.wover.biome.api.data.BiomeData#genChance()}),
     * i.e. the weight a Biome of any other mod competes with. Vanilla top-level Biomes use the same weight, so
     * neither vanilla nor other mods are preferred over each other.
     */
    private static final float DEFAULT_CHANCE = 1.0f;

    @Override
    protected void bootstrap(BiomeBootstrapContext context) {
        final var END_HIGHLANDS = WoverBiomeBuilder.wrappedKey(Biomes.END_HIGHLANDS);
        END_HIGHLANDS.bootstrap(context)
                     .genChance(DEFAULT_CHANCE)
                     .edgeSize(4)
                     .isEndHighlandBiome()
                     .register();

        // Midlands and barrens are sub-biomes of the highlands: their weight is relative to the highlands'
        // own weight, so they keep the established 0.20 : 0.05 : 0.03 ratio (80% : 20% : 12%).
        WoverBiomeBuilder.wrappedKey(Biomes.END_MIDLANDS)
                         .bootstrap(context)
                         .genChance(DEFAULT_CHANCE * 0.25f)
                         .isEndMidlandBiome(END_HIGHLANDS)
                         .register();

        WoverBiomeBuilder.wrappedKey(Biomes.END_BARRENS)
                         .bootstrap(context)
                         .genChance(DEFAULT_CHANCE * 0.15f)
                         .isEndBarrensBiome(END_HIGHLANDS)
                         .register();

        WoverBiomeBuilder.wrappedKey(Biomes.THE_END)
                         .bootstrap(context)
                         .genChance(DEFAULT_CHANCE)
                         .isEndCenterIslandBiome()
                         .register();

        WoverBiomeBuilder.wrappedKey(Biomes.SMALL_END_ISLANDS)
                         .bootstrap(context)
                         .genChance(DEFAULT_CHANCE)
                         .isEndSmallIslandBiome()
                         .register();


        WoverBiomeBuilder.wrappedKey(Biomes.NETHER_WASTES)
                         .bootstrap(context)
                         .genChance(DEFAULT_CHANCE)
                         .isNetherBiome()
                         .register();

        WoverBiomeBuilder.wrappedKey(Biomes.SOUL_SAND_VALLEY)
                         .bootstrap(context)
                         .genChance(DEFAULT_CHANCE)
                         .isNetherBiome()
                         .register();

        WoverBiomeBuilder.wrappedKey(Biomes.BASALT_DELTAS)
                         .bootstrap(context)
                         .genChance(DEFAULT_CHANCE)
                         .isNetherBiome()
                         .register();

        WoverBiomeBuilder.wrappedKey(Biomes.CRIMSON_FOREST)
                         .bootstrap(context)
                         .genChance(DEFAULT_CHANCE)
                         .isNetherBiome()
                         .register();

        WoverBiomeBuilder.wrappedKey(Biomes.WARPED_FOREST)
                         .bootstrap(context)
                         .genChance(DEFAULT_CHANCE)
                         .isNetherBiome()
                         .register();
    }


}
