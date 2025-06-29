package org.betterx.wover.biome.impl.modification;

import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
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

    private Map<MobCategory, WeightedList<MobSpawnSettings.SpawnerData>> customSpawners;

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

    public <M extends Mob> void addSpawns(WeightedList<MobSpawnSettings.SpawnerData> spawns) {
        Map<MobCategory, List<Weighted<MobSpawnSettings.SpawnerData>>> input =
                spawns.unwrap().stream().collect(Collectors.groupingBy(s -> s.value().type().getCategory()));
        if (input.isEmpty()) return;

        unfreezeSpawners();
        for (MobCategory category : input.keySet()) {
            final WeightedList<MobSpawnSettings.SpawnerData> currentSpawns = customSpawners.get(category);
            final List<Weighted<MobSpawnSettings.SpawnerData>> mutableSpawns;
            if (currentSpawns == null) {
                mutableSpawns = input.get(category);
            } else {
                List<Weighted<MobSpawnSettings.SpawnerData>> tmpA = currentSpawns.unwrap();
                List<Weighted<MobSpawnSettings.SpawnerData>> tmpB = input.get(category);
                mutableSpawns = new ArrayList<>(tmpA.size() + tmpB.size());
                mutableSpawns.addAll(tmpA);
                mutableSpawns.addAll(tmpB);
            }
            customSpawners.put(category, WeightedList.of(mutableSpawns));
        }
    }
}
