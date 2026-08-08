package de.ambertation.wover.biome.impl.modification;

import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adds mob spawns to an already-built {@link Biome}, on behalf of a
 * {@link de.ambertation.wover.biome.api.modification.BiomeModification}.
 * <p>
 * <b>26.3 rewrite.</b> {@code Biome#getMobSettings()} is gone: 26.3 moved a Biome's
 * {@link MobSpawnSettings} into its {@link EnvironmentAttributeMap}, under
 * {@link EnvironmentAttributes#NATURAL_MOB_SPAWNS}. {@code Biome.BiomeBuilder#mobSpawnSettings} is now
 * just a shorthand for {@code setAttribute(NATURAL_MOB_SPAWNS, ...)}, and at runtime
 * {@code EnvironmentAttributeSystem} reads the value back through
 * {@code biome.getAttributes().applyModifier(NATURAL_MOB_SPAWNS, base)} for the Biome at the sampled
 * position.
 * <p>
 * The 26.1 implementation unfroze the Biome's {@code MobSpawnSettings.spawners} map and mutated it in
 * place. That cannot be carried over, and not merely because the field was renamed to
 * {@code spawnsByCategory}: when a Biome does not declare the attribute at all, {@code applyModifier}
 * hands back the attribute's default value, which is the <b>shared singleton</b>
 * {@link MobSpawnSettings#EMPTY}. Mutating that would leak this modification's spawns into every Biome in
 * the game that declares no spawns of its own. So rather than mutating a {@link MobSpawnSettings}, we
 * build a new one and install it as the Biome's own attribute value — which is also simply how 26.3
 * expects a Biome's spawns to be expressed.
 * <p>
 * Two properties of the vanilla attribute system make that work:
 * <ul>
 *     <li>Values are resolved lazily on every query, so replacing the map after the Biome was built is
 *     still picked up.</li>
 *     <li>The per-level positional layers are created only for those attributes that some Biome
 *     declares, and that key set is snapshotted while the level is built. We run at
 *     {@code WorldLifecycle.MINECRAFT_SERVER_READY}, which precedes
 *     {@code WorldLifecycle.BEFORE_CREATING_LEVELS}, so a Biome that gains {@code NATURAL_MOB_SPAWNS}
 *     for the first time here still gets a layer.</li>
 * </ul>
 */
public class MobSettingsWorker {
    private final Biome biome;
    private final MobSpawnSettings mobSettings;

    private Map<MobCategory, WeightedList<MobSpawnSettings.SpawnerData>> customSpawners;

    public MobSettingsWorker(Biome biome) {
        this.biome = biome;
        this.mobSettings = mobSettingsOf(biome);
    }

    /**
     * Reads the {@link MobSpawnSettings} that are in effect for the passed {@link Biome}.
     * <p>
     * This is the replacement for {@code Biome#getMobSettings()}, which 26.3 removed. A Biome that
     * declares no spawns yields {@link MobSpawnSettings#EMPTY}, so treat the result as read-only — it may
     * be that shared singleton.
     *
     * @param biome The Biome to read.
     * @return The Biome's mob spawn settings, never {@code null}.
     */
    public static MobSpawnSettings mobSettingsOf(Biome biome) {
        return biome.getAttributes().applyModifier(
                EnvironmentAttributes.NATURAL_MOB_SPAWNS,
                EnvironmentAttributes.NATURAL_MOB_SPAWNS.defaultValue()
        );
    }

    private void unfreezeSpawners() {
        if (customSpawners == null) {
            // definedCategories() is the key set of spawnsByCategory and getMobsInCategory() its plain
            // Map#get, so together they reproduce the map exactly - including the distinction vanilla's
            // builder draws between a category mapped to an empty list (noSpawns) and an absent category
            // (dontOverride). Seeding from them and rebuilding via MobSpawnSettings.Builder is lossless.
            customSpawners = new HashMap<>();
            for (MobCategory category : mobSettings.definedCategories()) {
                customSpawners.put(category, mobSettings.getMobsInCategory(category));
            }
        }
    }

    private void freezeSpawners() {
        if (customSpawners == null) return;

        final MobSpawnSettings.Builder builder = new MobSpawnSettings.Builder();
        customSpawners.forEach(builder::addAllSpawns);
        builder.addAllCosts(mobSettings.allSpawnCosts());

        // EnvironmentAttributeMap is immutable and its builder is the only way to derive one, so we swap
        // the Biome's whole map for a copy in which NATURAL_MOB_SPAWNS is set to our merged settings.
        // Every other attribute is carried over untouched by putAll.
        biome.attributes = EnvironmentAttributeMap
                .builder()
                .putAll(biome.getAttributes())
                .set(EnvironmentAttributes.NATURAL_MOB_SPAWNS, builder.build())
                .build();

        customSpawners = null;
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
