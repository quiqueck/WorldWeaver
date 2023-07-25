package org.betterx.wover.biome.api.modification.predicates;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.structure.Structure;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

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
    public boolean test(BiomeSelectionContext ctx) {
        return ctx.validForStructure(key);
    }
}