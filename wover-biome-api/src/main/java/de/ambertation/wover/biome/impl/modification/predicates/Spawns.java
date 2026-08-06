package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record Spawns(EntityType<?> entityType) implements BiomePredicate {
    // 26.2 removed EntityType#byString(String). EntityType.CODEC is BuiltInRegistries.ENTITY_TYPE
    // .byNameCodec(), so it reads and writes the same plain id string the Identifier round-trip used to
    // produce - the serialized shape of "entity_type" is unchanged.
    public static final KeyDispatchDataCodec<Spawns> CODEC = KeyDispatchDataCodec
            .of(EntityType.CODEC
                    .xmap(Spawns::new, Spawns::entityType)
                    .fieldOf("entity_type")
            );

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
