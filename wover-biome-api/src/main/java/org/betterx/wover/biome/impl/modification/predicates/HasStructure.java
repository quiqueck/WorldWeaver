package org.betterx.wover.biome.impl.modification.predicates;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.structure.Structure;

public record HasStructure(ResourceKey<Structure> key) implements BiomePredicate {
    public static final KeyDispatchDataCodec<HasStructure> CODEC = KeyDispatchDataCodec
            .of(ResourceKey.codec(Registries.STRUCTURE)
                           .xmap(HasStructure::new, HasStructure::key)
                           .fieldOf("structure_key")
            );


    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        final Structure instance = ctx.structures.get(key);
        if (instance == null) return false;

        return instance.biomes().contains(ctx.biomeHolder);
    }
}