package org.betterx.wover.biome.impl.modification.predicates;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;

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
    public boolean test(Context ctx) {
        return ctx.biomeHolder.is(biomeTag);
    }
}
