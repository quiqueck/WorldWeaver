package org.betterx.wover.biome.impl.modification.predicates;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

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
    public boolean test(Context ctx) {
        final MobSpawnSettings spawns = ctx.biome.getMobSettings();

        for (MobCategory spawnGroup : MobCategory.values()) {
            for (MobSpawnSettings.SpawnerData spawnEntry : spawns.getMobs(spawnGroup).unwrap()) {
                if (spawnEntry.type.equals(entityType)) {
                    return true;
                }
            }
        }

        return false;
    }
}
