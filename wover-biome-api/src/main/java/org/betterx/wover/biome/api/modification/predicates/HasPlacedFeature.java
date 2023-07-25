package org.betterx.wover.biome.api.modification.predicates;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

public record HasPlacedFeature(ResourceKey<PlacedFeature> key) implements BiomePredicate {
    public static final KeyDispatchDataCodec<HasPlacedFeature> CODEC = KeyDispatchDataCodec
            .of(ResourceKey.codec(Registries.PLACED_FEATURE)
                           .xmap(HasPlacedFeature::new, HasPlacedFeature::key)
                           .fieldOf("feature_key")
            );


    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(BiomeSelectionContext ctx) {
        return ctx.hasPlacedFeature(key);
    }
}