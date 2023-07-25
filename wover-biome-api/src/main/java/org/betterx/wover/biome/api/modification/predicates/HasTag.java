package org.betterx.wover.biome.api.modification.predicates;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

public record HasTag(TagKey<Biome> biomeTag) implements BiomePredicate {
    public static final KeyDispatchDataCodec<HasTag> CODEC = KeyDispatchDataCodec
            .of(TagKey.codec(Registries.BIOME)
                      .xmap(HasTag::new, HasTag::biomeTag)
                      .fieldOf("biome_tag")
            );


    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(BiomeSelectionContext ctx) {
        return ctx.hasTag(biomeTag);
    }
}
