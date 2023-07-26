package org.betterx.wover.biome.impl.modification.predicates;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;

public record IsBiome(ResourceKey<Biome> biomeKey) implements BiomePredicate {
    public static final KeyDispatchDataCodec<IsBiome> CODEC = KeyDispatchDataCodec
            .of(ResourceKey.codec(Registries.BIOME)
                           .xmap(IsBiome::new, IsBiome::biomeKey)
                           .fieldOf("biome_key")
            );


    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        return ctx.biomeKey.equals(biomeKey);
    }
}
