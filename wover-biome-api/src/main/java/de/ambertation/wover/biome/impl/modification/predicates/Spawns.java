package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.resources.Identifier;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record Spawns(EntityType<?> entityType) implements BiomePredicate {
    public static final KeyDispatchDataCodec<Spawns> CODEC = KeyDispatchDataCodec
            .of(Identifier.CODEC
                    .xmap(Spawns::fromLocation, Spawns::entityLocation)
                    .fieldOf("entity_type")
            );

    private static Spawns fromLocation(Identifier entityLocation) {
        return new Spawns(EntityType.byString(entityLocation.toString()).orElseThrow());
    }

    private Identifier entityLocation() {
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
            for (Weighted<MobSpawnSettings.SpawnerData> spawnEntry : spawns.getMobs(spawnGroup).unwrap()) {
                if (spawnEntry.value().type().equals(entityType)) {
                    return true;
                }
            }
        }

        return false;
    }
}
