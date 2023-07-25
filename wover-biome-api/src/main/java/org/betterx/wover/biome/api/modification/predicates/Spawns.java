package org.betterx.wover.biome.api.modification.predicates;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.entity.EntityType;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;

public record Spawns(EntityType<?> entityType) implements BiomePredicate {
    public static final KeyDispatchDataCodec<Spawns> CODEC = KeyDispatchDataCodec
            .of(ResourceLocation.CODEC
                    .xmap(Spawns::fromLocation, Spawns::entityLocation)
                    .fieldOf("entity_type")
            );

    private static Spawns fromLocation(ResourceLocation entityLocation) {
        return new Spawns(EntityType.byString(entityLocation.toString()).orElseThrow());
    }

    private ResourceLocation entityLocation() {
        return EntityType.getKey(entityType);
    }

    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(BiomeSelectionContext ctx) {
        return BiomeSelectors.spawnsOneOf(entityType).test(ctx);
    }
}
