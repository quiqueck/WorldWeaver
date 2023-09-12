package org.betterx.wover.generator.datagen;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.datagen.api.provider.multi.WoverBiomeProvider;
import org.betterx.wover.generator.api.biomesource.WoverBiomeBuilder;

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

    @Override
    protected void bootstrap(BiomeBootstrapContext context) {
        WoverBiomeBuilder.wrappedKey(Biomes.END_HIGHLANDS)
                         .bootstrap(context)
                         .genChance(2.0f)
                         .edgeSize(4)
                         .register();

        WoverBiomeBuilder.wrappedKey(Biomes.END_MIDLANDS)
                         .bootstrap(context)
                         .genChance(0.5f)
                         .register();

        WoverBiomeBuilder.wrappedKey(Biomes.END_BARRENS)
                         .bootstrap(context)
                         .genChance(0.3f)
                         .register();
    }


}
