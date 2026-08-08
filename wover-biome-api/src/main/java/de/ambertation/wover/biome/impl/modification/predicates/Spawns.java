package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;
import de.ambertation.wover.biome.impl.modification.MobSettingsWorker;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record Spawns(EntityType<?> entityType) implements BiomePredicate {
    // 26.3 removed EntityType#byString(String). EntityType.CODEC is BuiltInRegistries.ENTITY_TYPE
    // .byNameCodec(), so it reads and writes the same plain id string the Identifier round-trip used to
    // produce - the serialized shape of "entity_type" is unchanged.
    public static final MapCodec<Spawns> CODEC = EntityType.CODEC
            .xmap(Spawns::new, Spawns::entityType)
            .fieldOf("entity_type");

    @Override
    public MapCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        final MobSpawnSettings spawns = MobSettingsWorker.mobSettingsOf(ctx.biome);

        for (MobCategory spawnGroup : MobCategory.values()) {
            // 26.3: MobSpawnSettings#getMobs -> #getMobsToSpawn. The sibling #getMobsInCategory returns
            // null for a category the Biome never declared; getMobsToSpawn falls back to the empty list,
            // which is what this loop over every MobCategory needs.
            for (Weighted<MobSpawnSettings.SpawnerData> spawnEntry : spawns.getMobsToSpawn(spawnGroup)
                                                                          .unwrap()) {
                if (spawnEntry.value().type().equals(entityType)) {
                    return true;
                }
            }
        }

        return false;
    }
}
