package org.betterx.wover.biome.api.modification.predicates;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.dimension.LevelStem;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

public record InDimension(ResourceKey<LevelStem> dimensionKey) implements BiomePredicate {
    public static final KeyDispatchDataCodec<InDimension> CODEC = KeyDispatchDataCodec
            .of(ResourceKey.codec(Registries.LEVEL_STEM)
                           .xmap(InDimension::new, InDimension::dimensionKey)
                           .fieldOf("dimension_key")
            );


    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(BiomeSelectionContext ctx) {
        return ctx.canGenerateIn(dimensionKey);
    }
}
