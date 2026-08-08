package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public record HasStructure(ResourceKey<Structure> key) implements BiomePredicate {
    public static final MapCodec<HasStructure> CODEC = ResourceKey.codec(Registries.STRUCTURE)
            .xmap(HasStructure::new, HasStructure::key)
            .fieldOf("structure_key");


    @Override
    public MapCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        final Structure instance = ctx.structures.get(key).orElse(null).value();
        if (instance == null) return false;

        return instance.biomes().contains(ctx.biomeHolder);
    }
}