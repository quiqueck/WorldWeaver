package com.betterxlib.api.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating End dimension biomes.
 * <p>
 * Example usage:
 * <pre>{@code
 * BiomeEntry biome = EndBiomeBuilder.create("mymod", "crystal_plains")
 *     .temperature(0.5f)
 *     .downfall(0.0f)
 *     .fogColor(0x8080FF)
 *     .skyColor(0x000000)
 *     .waterColor(0x3F76E4)
 *     .waterFogColor(0x050533)
 *     .grassColor(0x8EB971)
 *     .foliageColor(0x71A74D)
 *     .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, crystalFeature)
 *     .build();
 * }</pre>
 */
public class EndBiomeBuilder {
    private final ResourceKey<Biome> key;
    private final String modId;
    private final String name;

    // Climate
    private float temperature = 0.5f;
    private float downfall = 0.0f;
    private boolean hasPrecipitation = false;

    // Colors
    private int fogColor = 0xA080A0; // Default End fog
    private int skyColor = 0x000000; // Black sky
    private int waterColor = 0x3F76E4;
    private int waterFogColor = 0x050533;
    @Nullable private Integer grassColor = null;
    @Nullable private Integer foliageColor = null;

    // Ambient
    @Nullable private AmbientParticleSettings particles = null;
    @Nullable private Holder<SoundEvent> ambientSound = null;
    @Nullable private AmbientMoodSettings moodSettings = null;
    @Nullable private AmbientAdditionsSettings additionsSettings = null;
    @Nullable private Music backgroundMusic = null;

    // Generation
    private final List<FeatureEntry> features = new ArrayList<>();
    private final List<CarverEntry> carvers = new ArrayList<>();
    private final MobSpawnSettings.Builder mobSpawnSettings = new MobSpawnSettings.Builder();

    private EndBiomeBuilder(String modId, String name) {
        this.modId = modId;
        this.name = name;
        this.key = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(modId, name));
    }

    /**
     * Create a new End biome builder.
     *
     * @param modId the mod ID
     * @param name the biome name
     * @return a new EndBiomeBuilder
     */
    public static EndBiomeBuilder create(String modId, String name) {
        return new EndBiomeBuilder(modId, name);
    }

    /**
     * Create a new End biome builder from a resource location.
     *
     * @param location the resource location
     * @return a new EndBiomeBuilder
     */
    public static EndBiomeBuilder create(ResourceLocation location) {
        return new EndBiomeBuilder(location.getNamespace(), location.getPath());
    }

    // Climate setters

    public EndBiomeBuilder temperature(float temperature) {
        this.temperature = temperature;
        return this;
    }

    public EndBiomeBuilder downfall(float downfall) {
        this.downfall = downfall;
        return this;
    }

    public EndBiomeBuilder hasPrecipitation(boolean hasPrecipitation) {
        this.hasPrecipitation = hasPrecipitation;
        return this;
    }

    // Color setters

    public EndBiomeBuilder fogColor(int fogColor) {
        this.fogColor = fogColor;
        return this;
    }

    public EndBiomeBuilder skyColor(int skyColor) {
        this.skyColor = skyColor;
        return this;
    }

    public EndBiomeBuilder waterColor(int waterColor) {
        this.waterColor = waterColor;
        return this;
    }

    public EndBiomeBuilder waterFogColor(int waterFogColor) {
        this.waterFogColor = waterFogColor;
        return this;
    }

    public EndBiomeBuilder grassColor(int grassColor) {
        this.grassColor = grassColor;
        return this;
    }

    public EndBiomeBuilder foliageColor(int foliageColor) {
        this.foliageColor = foliageColor;
        return this;
    }

    // Ambient setters

    public EndBiomeBuilder particles(AmbientParticleSettings particles) {
        this.particles = particles;
        return this;
    }

    public EndBiomeBuilder ambientSound(Holder<SoundEvent> sound) {
        this.ambientSound = sound;
        return this;
    }

    public EndBiomeBuilder moodSettings(AmbientMoodSettings settings) {
        this.moodSettings = settings;
        return this;
    }

    public EndBiomeBuilder additionsSettings(AmbientAdditionsSettings settings) {
        this.additionsSettings = settings;
        return this;
    }

    public EndBiomeBuilder backgroundMusic(Music music) {
        this.backgroundMusic = music;
        return this;
    }

    // Generation setters

    public EndBiomeBuilder addFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
        this.features.add(new FeatureEntry(step, feature));
        return this;
    }

    public EndBiomeBuilder addCarver(GenerationStep.Carving step, Holder<ConfiguredWorldCarver<?>> carver) {
        this.carvers.add(new CarverEntry(step, carver));
        return this;
    }

    public EndBiomeBuilder mobSpawnSettings(java.util.function.Consumer<MobSpawnSettings.Builder> consumer) {
        consumer.accept(this.mobSpawnSettings);
        return this;
    }

    /**
     * Build the biome entry.
     *
     * @return the biome entry ready for registration
     */
    public BiomeEntry build() {
        return new BiomeEntry(key, this::createBiome);
    }

    private Biome createBiome() {
        // Build special effects
        BiomeSpecialEffects.Builder effectsBuilder = new BiomeSpecialEffects.Builder()
            .fogColor(fogColor)
            .skyColor(skyColor)
            .waterColor(waterColor)
            .waterFogColor(waterFogColor);

        if (grassColor != null) {
            effectsBuilder.grassColorOverride(grassColor);
        }
        if (foliageColor != null) {
            effectsBuilder.foliageColorOverride(foliageColor);
        }
        if (particles != null) {
            effectsBuilder.ambientParticle(particles);
        }
        if (ambientSound != null) {
            effectsBuilder.ambientLoopSound(ambientSound);
        }
        if (moodSettings != null) {
            effectsBuilder.ambientMoodSound(moodSettings);
        }
        if (additionsSettings != null) {
            effectsBuilder.ambientAdditionsSound(additionsSettings);
        }
        if (backgroundMusic != null) {
            effectsBuilder.backgroundMusic(backgroundMusic);
        }

        // Build generation settings
        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder(null, null);

        for (FeatureEntry entry : features) {
            genBuilder.addFeature(entry.step, entry.feature);
        }
        for (CarverEntry entry : carvers) {
            genBuilder.addCarver(entry.step, entry.carver);
        }

        // Build the biome
        return new Biome.BiomeBuilder()
            .hasPrecipitation(hasPrecipitation)
            .temperature(temperature)
            .downfall(downfall)
            .specialEffects(effectsBuilder.build())
            .mobSpawnSettings(mobSpawnSettings.build())
            .generationSettings(genBuilder.build())
            .build();
    }

    /**
     * Get the resource key for this biome.
     *
     * @return the resource key
     */
    public ResourceKey<Biome> getKey() {
        return key;
    }

    private record FeatureEntry(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {}
    private record CarverEntry(GenerationStep.Carving step, Holder<ConfiguredWorldCarver<?>> carver) {}
}
