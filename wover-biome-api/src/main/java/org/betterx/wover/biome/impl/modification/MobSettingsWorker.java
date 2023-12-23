package org.betterx.wover.biome.impl.modification;

import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MobSettingsWorker {
    private final MobSpawnSettings mobSettings;
    private final Biome biome;

    private Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> customSpawners;

    public MobSettingsWorker(Biome biome) {
        this.biome = biome;
        this.mobSettings = biome.getMobSettings();
    }

    private void unfreezeSpawners() {
        if (customSpawners == null) {
            customSpawners = new HashMap<>(mobSettings.spawners);
            mobSettings.spawners = customSpawners;
        }
    }

    private void freezeSpawners() {
        if (customSpawners != null) {
            mobSettings.spawners = ImmutableMap.copyOf(customSpawners);
            customSpawners = null;
        }
    }

    public boolean finished() {
        boolean res = customSpawners != null;
        freezeSpawners();
        return res;
    }

    public <M extends Mob> void addSpawns(List<MobSpawnSettings.SpawnerData> spawns) {
        Map<MobCategory, List<MobSpawnSettings.SpawnerData>> input =
                spawns.stream().collect(Collectors.groupingBy(s -> s.type.getCategory()));
        if (input.isEmpty()) return;

        unfreezeSpawners();
        for (MobCategory category : input.keySet()) {
            final WeightedRandomList<MobSpawnSettings.SpawnerData> currentSpawns = customSpawners.get(category);
            final List<MobSpawnSettings.SpawnerData> mutableSpawns;
            if (currentSpawns == null) {
                mutableSpawns = input.get(category);
            } else {
                var tmpA = currentSpawns.unwrap();
                var tmpB = input.get(category);
                mutableSpawns = new ArrayList<>(tmpA.size() + tmpB.size());
                mutableSpawns.addAll(tmpA);
                mutableSpawns.addAll(tmpB);
            }
            customSpawners.put(category, WeightedRandomList.create(mutableSpawns));
        }
    }
}
