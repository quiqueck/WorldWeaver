package org.betterx.wover.testmod.structure.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.testmod.entrypoint.WoverStructureTestMod;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class BiomeTagProvider extends WoverTagProvider.ForBiomes {
    public BiomeTagProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void prepareTags(TagBootstrapContext<Biome> provider) {
        provider.add(WoverStructureTestMod.TEST_STRUCTURE.getBiomeTag(),
                Biomes.SAVANNA, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.NETHER_WASTES
        );
    }
}
