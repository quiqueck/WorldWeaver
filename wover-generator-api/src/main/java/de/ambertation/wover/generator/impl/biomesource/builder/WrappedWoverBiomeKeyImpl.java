package de.ambertation.wover.generator.impl.biomesource.builder;

import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeBuilder;

import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.NotNull;

public class WrappedWoverBiomeKeyImpl extends BiomeKey<WoverBiomeBuilder.Wrapped> {
    public WrappedWoverBiomeKeyImpl(@NotNull Identifier location) {
        super(location);
    }

    @Override
    public WoverBiomeBuilder.Wrapped bootstrap(BiomeBootstrapContext context) {
        return new WrappedWoverDataBuilderImpl(context, this);
    }
}
