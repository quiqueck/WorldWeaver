package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public record HasTag(TagKey<Biome> biomeTag) implements BiomePredicate {
    public static final MapCodec<HasTag> CODEC = TagKey.codec(Registries.BIOME)
            .xmap(HasTag::new, HasTag::biomeTag)
            .fieldOf("biome_tag");


    @Override
    public MapCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        return ctx.biomeHolder.is(biomeTag);
    }
}
