package de.ambertation.wover.testmod.gametest;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Does a loaded {@code BiomeModification} actually change the target biome, not just exist in
 * {@link de.ambertation.wover.biome.api.modification.BiomeModificationRegistry#BIOME_MODIFICATION_REGISTRY}
 * - {@link BiomeGameTest} already covers the latter (registry presence for datapack JSON and the
 * runtime-injected entry) but can't catch a modification that's registered correctly yet never actually
 * mutates its target biome's {@link net.minecraft.world.level.biome.BiomeGenerationSettings}/tags (wrong
 * predicate, wrong decoration step, an ordering bug against another modification).
 * <p>
 * Unlike the worldgen/surface-rule boot checks elsewhere in this framework, this <b>can</b> be a plain
 * {@code @GameTest}: {@code BiomeModificationRegistryImpl} mutates each biome's generation settings and
 * tags in-memory at {@code MINECRAFT_SERVER_READY} - reading that back is a registry/object-state read,
 * not real chunk/terrain generation, so it isn't affected by {@code GameTestServer}'s hardcoded flat
 * overworld (see {@code CompatWorldgenBootCheck}'s class doc for why that constraint matters elsewhere).
 * <p>
 * Exercises the same three shipped testmod modifications {@link BiomeGameTest} already asserts are
 * registered:
 * <ul>
 *   <li>{@code test_features.json} - predicate {@code or(is_biome(beach), is_biome(meadow))}, adds
 *       {@code minecraft:small_basalt_columns} at {@link GenerationStep.Decoration#SURFACE_STRUCTURES}.
 *       Checked on both target biomes.</li>
 *   <li>{@code runtime_modification} (programmatic, {@code TestModWoverBiome}) - targets
 *       {@code minecraft:meadow} specifically, adds the same feature at
 *       {@link GenerationStep.Decoration#VEGETAL_DECORATION} - a different step than the datapack rule
 *       above, on the same biome, proving the two independent modification paths don't clobber each
 *       other.</li>
 *   <li>{@code test_tags.json} - predicate {@code in_dimension(minecraft:the_nether)}, adds the
 *       {@code minecraft:has_structure/swamp_hut} tag to every nether biome. Checked against every biome
 *       the live {@code the_nether} level's own {@code BiomeSource.possibleBiomes()} actually reports -
 *       not a hardcoded vanilla biome name, since the predicate itself evaluates the same way (see
 *       {@code InDimension.test()}) and a testmod's own nether generator may not include vanilla's usual
 *       nether biomes at all. A negative guard on {@code minecraft:plains} confirms an overworld-only
 *       biome does NOT pick up the nether-scoped tag.</li>
 * </ul>
 */
public class BiomeModificationAppliesGameTest {
    private static final ResourceLocation SWAMP_HUT_TAG_ID = ResourceLocation.parse("minecraft:has_structure/swamp_hut");

    @GameTest
    public void datapackModificationsActuallyApply(GameTestHelper helper) {
        final var registryAccess = helper.getLevel().registryAccess();
        final Registry<Biome> biomes = registryAccess.lookupOrThrow(Registries.BIOME);
        final Registry<PlacedFeature> placedFeatures = registryAccess.lookupOrThrow(Registries.PLACED_FEATURE);
        final Holder<PlacedFeature> smallBasaltColumns = placedFeatures.getOrThrow(NetherPlacements.SMALL_BASALT_COLUMNS);

        final List<String> failures = new ArrayList<>();

        // test_features.json applies independently to both predicate-matched biomes.
        for (ResourceKey<Biome> key : List.of(Biomes.BEACH, Biomes.MEADOW)) {
            if (!hasFeatureAtStep(biomes, key, GenerationStep.Decoration.SURFACE_STRUCTURES, smallBasaltColumns)) {
                failures.add(key.location() + ": expected small_basalt_columns at SURFACE_STRUCTURES "
                        + "(test_features.json) but it is missing");
            }
        }

        // runtime_modification targets meadow specifically, at a different step - both must hold at once.
        if (!hasFeatureAtStep(biomes, Biomes.MEADOW, GenerationStep.Decoration.VEGETAL_DECORATION, smallBasaltColumns)) {
            failures.add(Biomes.MEADOW.location() + ": expected small_basalt_columns at VEGETAL_DECORATION "
                    + "(runtime-injected modification) but it is missing");
        }

        // Not a hardcoded vanilla biome: the wover:in_dimension predicate itself evaluates
        // `dimension.generator().getBiomeSource().possibleBiomes()` (see InDimension.test()), i.e. "is
        // this biome actually reachable in that dimension's real generator" - not "is this vanilla's
        // usual biome for that dimension". A testmod's own nether generator may not even include
        // minecraft:nether_wastes. So this reads the live nether ServerLevel's own possibleBiomes() and
        // checks every one of them - the true predicate semantics - rather than guessing a name.
        final ServerLevel netherLevel = helper.getLevel().getServer().getLevel(Level.NETHER);
        final Set<ResourceKey<Biome>> reachableInNether = new LinkedHashSet<>();
        if (netherLevel != null) {
            for (Holder<Biome> holder : netherLevel.getChunkSource().getGenerator().getBiomeSource().possibleBiomes()) {
                holder.unwrapKey().ifPresent(reachableInNether::add);
            }
        }
        if (reachableInNether.isEmpty()) {
            failures.add("minecraft:the_nether: no biomes reachable via its own BiomeSource - can't verify the "
                    + "in_dimension(the_nether) predicate at all");
        }

        final TagKey<Biome> swampHutTag = TagKey.create(Registries.BIOME, SWAMP_HUT_TAG_ID);
        for (ResourceKey<Biome> key : reachableInNether) {
            if (!biomes.getOrThrow(key).is(swampHutTag)) {
                failures.add(key.location() + ": reachable in the_nether but missing " + SWAMP_HUT_TAG_ID
                        + " (test_tags.json, in_dimension the_nether)");
            }
        }

        // Negative guard: an overworld-only biome (never reachable in nether) must not have picked up
        // the nether-scoped tag.
        final Holder<Biome> plains = biomes.getOrThrow(Biomes.PLAINS);
        if (!reachableInNether.contains(Biomes.PLAINS) && plains.is(swampHutTag)) {
            failures.add(Biomes.PLAINS.location() + ": unexpectedly has " + SWAMP_HUT_TAG_ID
                    + " - the in_dimension(the_nether) predicate is not scoping correctly");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal("Biome-modification apply regression:\n - " + String.join("\n - ", failures)));
            return;
        }

        helper.succeed();
    }

    private static boolean hasFeatureAtStep(
            Registry<Biome> biomes, ResourceKey<Biome> biomeKey, GenerationStep.Decoration step,
            Holder<PlacedFeature> feature
    ) {
        final List<HolderSet<PlacedFeature>> features = biomes.getOrThrow(biomeKey).value().getGenerationSettings().features();
        final int index = step.ordinal();
        return index < features.size() && features.get(index).contains(feature);
    }
}
