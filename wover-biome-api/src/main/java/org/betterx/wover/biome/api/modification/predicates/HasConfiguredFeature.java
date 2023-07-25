package org.betterx.wover.biome.api.modification.predicates;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

public record HasConfiguredFeature(ResourceKey<ConfiguredFeature<?, ?>> key) implements BiomePredicate {
    public static final KeyDispatchDataCodec<HasConfiguredFeature> CODEC = KeyDispatchDataCodec
            .of(ResourceKey.codec(Registries.CONFIGURED_FEATURE)
                           .xmap(HasConfiguredFeature::new, HasConfiguredFeature::key)
                           .fieldOf("feature_key")
            );


    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(BiomeSelectionContext ctx) {
        return ctx.hasFeature(key);
    }
}
