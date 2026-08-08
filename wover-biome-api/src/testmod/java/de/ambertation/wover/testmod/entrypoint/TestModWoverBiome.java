package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.BiomeManager;
import de.ambertation.wover.biome.api.builder.BiomeBuilder;
import de.ambertation.wover.biome.api.modification.BiomeModification;
import de.ambertation.wover.biome.api.modification.BiomeModificationRegistry;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.tag.api.TagManager;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;

import net.fabricmc.api.ModInitializer;

public class TestModWoverBiome implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-biome-testmod");

    public static final BiomeKey<BiomeBuilder.Vanilla> TEST_BIOME = BiomeManager.vanilla(C.id("test_biome"));

    // Shared with BiomeGameTest, which asserts these exact values arrive in the Biome.
    public static final int SPAWN_WEIGHT = 7;
    public static final int SPAWN_MIN_GROUP = 2;
    public static final int SPAWN_MAX_GROUP = 5;

    // A Biome tag that is only ever populated at runtime - there is deliberately no committed JSON for it,
    // and the datagen BiomeProvider (which builds its own private BiomeBootstrapContext) does not see it
    // either. It is attached to a vanilla Biome by the wrapped builder in onInitialize below, and
    // BiomeTagReloadGameTest asserts on it.
    public static final TagKey<Biome> RUNTIME_BIOME_TAG = TagManager.BIOMES.makeTag(C.id("runtime_biome_tag"));

    // The Biome the tag above is attached to, and one that it must never reach (negative control).
    public static final ResourceKey<Biome> TAGGED_BIOME = Biomes.PLAINS;
    public static final ResourceKey<Biome> UNTAGGED_BIOME = Biomes.DESERT;

    @Override
    public void onInitialize() {
        // A wrapped builder that only carries a tag: registerBiome is a no-op and registerBiomeData bails
        // out for the default fog density with no climate parameters, so this adds nothing to any registry.
        // It exists purely so the Biome-tag phase has something to contribute at runtime.
        BiomeManager.BOOTSTRAP_BIOMES_WITH_DATA.subscribe(context ->
                BiomeManager
                        .wrapped(TAGGED_BIOME)
                        .bootstrap(context)
                        .tag(RUNTIME_BIOME_TAG)
                        .register()
        );

        BiomeModificationRegistry.BOOTSTRAP_BIOME_MODIFICATION_REGISTRY.subscribe(context -> {
            var features = context.lookup(Registries.PLACED_FEATURE);

            BiomeModification
                    .build(context, C.id("runtime_modification"))
                    .isBiome(Biomes.MEADOW)
                    .addFeature(
                            GenerationStep.Decoration.VEGETAL_DECORATION,
                            features.getOrThrow(NetherPlacements.SMALL_BASALT_COLUMNS)
                    )
                    .register();

            // Exercises MobSettingsWorker, which 26.3 forced to be rewritten: a Biome's MobSpawnSettings
            // now live in its EnvironmentAttributeMap rather than in a mutable field. The two target
            // Biomes are chosen so BiomeGameTest can assert the rebuild is additive in both directions:
            // badlands brings vanilla spawns of its own, and soul sand valley is one of only two vanilla
            // Biomes that declare spawn costs (no overworld Biome does), so it is the only way to catch
            // MobSpawnSettings.Builder#addAllCosts being dropped. Neither is touched by another
            // modification's mob spawns here.
            BiomeModification
                    .build(context, C.id("runtime_spawn_modification"))
                    .inBiomes(Biomes.BADLANDS, Biomes.SOUL_SAND_VALLEY)
                    .addSpawn(EntityTypes.OCELOT, SPAWN_WEIGHT, SPAWN_MIN_GROUP, SPAWN_MAX_GROUP)
                    .register();
        });
    }
}