package de.ambertation.wover.biome.impl.builder;

import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.biome.api.builder.BiomeBuilder;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeGenerationDataContainer;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;

public class WrappedBiomeBuilderImpl extends BiomeBuilder.Wrapped {
    public WrappedBiomeBuilderImpl(
            BiomeBootstrapContext context,
            BiomeKey<Wrapped> key
    ) {
        super(context, key);
    }

    @Override
    public void registerBiome(BootstrapContext<Biome> biomeContext) {

    }

    @Override
    public void registerBiomeTags(TagBootstrapContext<Biome> context) {
        super.registerBiomeTags(context);
    }

    @Override
    public void registerBiomeData(BootstrapContext<BiomeData> dataContext) {
        if (fogDensity == 1.0f && parameters.isEmpty()) return;

        dataContext.register(key.dataKey, new BiomeData(fogDensity, key.key, new BiomeGenerationDataContainer(parameters, intendedPlacement)));
    }
}
