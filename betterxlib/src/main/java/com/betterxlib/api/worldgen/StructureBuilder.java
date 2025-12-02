package com.betterxlib.api.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.Map;
import java.util.Optional;

/**
 * Builder for creating structure registrations.
 * <p>
 * Example usage:
 * <pre>{@code
 * // Create resource keys
 * ResourceKey<Structure> MY_TOWER = StructureBuilder.structureKey("mymod", "tower");
 * ResourceKey<StructureSet> MY_TOWER_SET = StructureBuilder.structureSetKey("mymod", "towers");
 *
 * // Create structure settings
 * Structure.StructureSettings settings = StructureBuilder.settings()
 *     .biomes(BiomeTags.IS_END_HIGHLANDS)
 *     .step(GenerationStep.Decoration.SURFACE_STRUCTURES)
 *     .terrainAdaptation(TerrainAdjustment.BEARD_BOX)
 *     .build();
 *
 * // Create placement
 * StructurePlacement placement = StructureBuilder.randomSpread()
 *     .spacing(32)
 *     .separation(8)
 *     .salt(12345678)
 *     .build();
 * }</pre>
 */
public class StructureBuilder {

    private StructureBuilder() {}

    /**
     * Create a resource key for a structure.
     *
     * @param modId the mod ID
     * @param name the structure name
     * @return the resource key
     */
    public static ResourceKey<Structure> structureKey(String modId, String name) {
        return ResourceKey.create(Registries.STRUCTURE,
            ResourceLocation.fromNamespaceAndPath(modId, name));
    }

    /**
     * Create a resource key for a structure set.
     *
     * @param modId the mod ID
     * @param name the structure set name
     * @return the resource key
     */
    public static ResourceKey<StructureSet> structureSetKey(String modId, String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET,
            ResourceLocation.fromNamespaceAndPath(modId, name));
    }

    /**
     * Start building structure settings.
     *
     * @return a new SettingsBuilder
     */
    public static SettingsBuilder settings() {
        return new SettingsBuilder();
    }

    /**
     * Start building a random spread placement.
     *
     * @return a new RandomSpreadBuilder
     */
    public static RandomSpreadBuilder randomSpread() {
        return new RandomSpreadBuilder();
    }

    /**
     * Builder for Structure.StructureSettings.
     */
    public static class SettingsBuilder {
        private HolderSet<Biome> biomes;
        private Map<net.minecraft.world.entity.MobCategory, net.minecraft.world.level.levelgen.structure.StructureSpawnOverride> spawnOverrides = Map.of();
        private GenerationStep.Decoration step = GenerationStep.Decoration.SURFACE_STRUCTURES;
        private TerrainAdjustment terrainAdaptation = TerrainAdjustment.NONE;

        SettingsBuilder() {}

        public SettingsBuilder biomes(HolderSet<Biome> biomes) {
            this.biomes = biomes;
            return this;
        }

        public SettingsBuilder biomes(TagKey<Biome> biomeTag) {
            // This will need to be resolved at registration time
            // For now, store as null and handle in data generation
            return this;
        }

        public SettingsBuilder spawnOverrides(Map<net.minecraft.world.entity.MobCategory, net.minecraft.world.level.levelgen.structure.StructureSpawnOverride> overrides) {
            this.spawnOverrides = overrides;
            return this;
        }

        public SettingsBuilder step(GenerationStep.Decoration step) {
            this.step = step;
            return this;
        }

        public SettingsBuilder terrainAdaptation(TerrainAdjustment terrainAdaptation) {
            this.terrainAdaptation = terrainAdaptation;
            return this;
        }

        public Structure.StructureSettings build() {
            if (biomes == null) {
                throw new IllegalStateException("Biomes not set");
            }
            return new Structure.StructureSettings(biomes, spawnOverrides, step, terrainAdaptation);
        }
    }

    /**
     * Builder for RandomSpreadStructurePlacement.
     */
    public static class RandomSpreadBuilder {
        private int spacing = 32;
        private int separation = 8;
        private RandomSpreadType spreadType = RandomSpreadType.LINEAR;
        private int salt = 0;
        private Optional<StructurePlacement.ExclusionZone> exclusionZone = Optional.empty();

        RandomSpreadBuilder() {}

        public RandomSpreadBuilder spacing(int spacing) {
            this.spacing = spacing;
            return this;
        }

        public RandomSpreadBuilder separation(int separation) {
            this.separation = separation;
            return this;
        }

        public RandomSpreadBuilder spreadType(RandomSpreadType type) {
            this.spreadType = type;
            return this;
        }

        public RandomSpreadBuilder triangular() {
            return spreadType(RandomSpreadType.TRIANGULAR);
        }

        public RandomSpreadBuilder linear() {
            return spreadType(RandomSpreadType.LINEAR);
        }

        public RandomSpreadBuilder salt(int salt) {
            this.salt = salt;
            return this;
        }

        public RandomSpreadBuilder exclusionZone(StructurePlacement.ExclusionZone zone) {
            this.exclusionZone = Optional.of(zone);
            return this;
        }

        public RandomSpreadStructurePlacement build() {
            return new RandomSpreadStructurePlacement(
                spacing,
                separation,
                spreadType,
                salt
            );
        }
    }
}
