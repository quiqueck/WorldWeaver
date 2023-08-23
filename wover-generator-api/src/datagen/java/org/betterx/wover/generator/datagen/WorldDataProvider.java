package org.betterx.wover.generator.datagen;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.datagen.api.provider.multi.WoverBiomeProvider;
import org.betterx.wover.generator.api.biomesource.WoverBiomeBuilder;

import net.minecraft.world.level.biome.Biomes;

public class WorldDataProvider extends WoverBiomeProvider {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WorldDataProvider(
            ModCore modCore
    ) {
        super(modCore);
    }

    @Override
    protected void bootstrap(BiomeBootstrapContext context) {
        WoverBiomeBuilder.wrappedKey(Biomes.END_HIGHLANDS)
                         .bootstrap(context)
                         .edge(Biomes.END_MIDLANDS)
                         .edgeSize(4)
                         .register();

        WoverBiomeBuilder.wrappedKey(Biomes.END_MIDLANDS)
                         .bootstrap(context)
                         .parent(Biomes.END_HIGHLANDS)
                         .genChance(0.5f)
                         .register();

        WoverBiomeBuilder.wrappedKey(Biomes.END_BARRENS)
                         .bootstrap(context)
                         .parent(Biomes.END_HIGHLANDS)
                         .genChance(0.3f)
                         .register();
    }


}
