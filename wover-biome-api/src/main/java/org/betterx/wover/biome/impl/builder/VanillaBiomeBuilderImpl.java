package org.betterx.wover.biome.impl.builder;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.data.BiomeData;

import net.minecraft.data.worldgen.BootstapContext;

public class VanillaBiomeBuilderImpl extends BiomeBuilder.Vanilla {
    public VanillaBiomeBuilderImpl(BiomeBootstrapContext context, BiomeKey<Vanilla> key) {
        super(context, key);
    }

    @Override
    public void registerBiomeData(BootstapContext<BiomeData> dataContext) {
        if (fogDensity == 1.0f && parameters.isEmpty()) return;

        dataContext.register(key.dataKey, new BiomeData(fogDensity, key.key, parameters));
    }
}
