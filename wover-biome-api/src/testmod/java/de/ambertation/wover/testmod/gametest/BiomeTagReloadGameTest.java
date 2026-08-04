package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.tag.impl.TagManagerImpl;
import de.ambertation.wover.testmod.entrypoint.TestModWoverBiome;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for wover-biome-api: the Biome-tag phase must stay re-entrant, because it runs once per
 * datapack (re-)load and not once per world.
 * <p>
 * {@code BiomeManagerImpl.onBootstrapTags} reads the cached {@code BiomeBootstrapContextImpl} - the only
 * place the registered {@code BiomeBuilder}s live - and that context is built exactly once per datapack
 * load, while the biome registry is bootstrapped. {@code TagLoader.load} however runs again on every
 * {@code /reload}, without any preceding biome bootstrap to rebuild the context. When the tag phase used to
 * invalidate the context on its way out, the second pass found nothing and threw, which aborted the whole
 * reload ("Reload failed; keeping old data") and silently discarded every datapack change.
 * <p>
 * This drives the same entry point {@code TagLoaderMixin} calls, so a second (and third) tag load is
 * exercised without needing an actual {@code /reload}. The testmod attaches
 * {@link TestModWoverBiome#RUNTIME_BIOME_TAG} to {@link TestModWoverBiome#TAGGED_BIOME} through a wrapped
 * {@code BiomeBuilder}; there is no committed JSON for that tag, so it can only come from the builder path
 * under test. The negative assertions ({@link TestModWoverBiome#UNTAGGED_BIOME} must stay out of the tag,
 * and an id nobody declares must not appear) keep the test able to fail.
 */
public class BiomeTagReloadGameTest {
    // Must match the directory TagManagerImpl.registerBiome registers the biome tag registry under -
    // didLoadTagMap is a no-op for an unknown directory, which would make this test vacuous.
    private static final String BIOME_TAG_DIRECTORY = "tags/worldgen/biome";

    private static final ResourceLocation ABSENT_TAG = ResourceLocation.parse("wover-biome-testmod:does_not_exist_tag");

    @GameTest
    public void biomeTagsSurviveARepeatedTagLoad(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        // 1. The startup tag load must have put the runtime-only tag onto the biome.
        final Holder<Biome> tagged = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.BIOME)
                .getOrThrow(TestModWoverBiome.TAGGED_BIOME);
        final Holder<Biome> untagged = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.BIOME)
                .getOrThrow(TestModWoverBiome.UNTAGGED_BIOME);

        if (!tagged.is(TestModWoverBiome.RUNTIME_BIOME_TAG)) {
            failures.add(TestModWoverBiome.TAGGED_BIOME.location()
                    + ": expected in the runtime-only biome tag after the initial tag load, but it is missing");
        }
        if (untagged.is(TestModWoverBiome.RUNTIME_BIOME_TAG)) {
            failures.add(TestModWoverBiome.UNTAGGED_BIOME.location()
                    + ": must NOT be in the runtime-only biome tag (the tag assertions are not meaningful)");
        }

        // 2. Re-run the tag phase twice, the way /reload does. Before the fix the first of these threw a
        //    NullPointerException because the context had already been invalidated at startup.
        final ResourceLocation runtimeTag = TestModWoverBiome.RUNTIME_BIOME_TAG.location();
        final List<Integer> entryCounts = new ArrayList<>();
        for (int pass = 1; pass <= 2; pass++) {
            final Map<ResourceLocation, List<TagLoader.EntryWithSource>> tagMap
                    = TagManagerImpl.didLoadTagMap(BIOME_TAG_DIRECTORY, new HashMap<>());

            final List<TagLoader.EntryWithSource> entries = tagMap.get(runtimeTag);
            if (entries == null || entries.isEmpty()) {
                failures.add("reload pass " + pass + ": " + runtimeTag
                        + " contributed no entries - biome tags would be lost on /reload");
                entryCounts.add(-1);
            } else {
                entryCounts.add(entries.size());
            }

            if (tagMap.containsKey(ABSENT_TAG)) {
                failures.add("reload pass " + pass + ": " + ABSENT_TAG
                        + " unexpectedly present - the tag-map assertions are not meaningful");
            }
        }

        // 3. Every pass has to contribute the same members; a growing or shrinking count means the
        //    builders are re-collected (or dropped) per pass instead of being reused.
        if (entryCounts.size() == 2 && !entryCounts.get(0).equals(entryCounts.get(1))) {
            failures.add("reload passes disagree on the number of entries for " + runtimeTag
                    + ": " + entryCounts.get(0) + " vs " + entryCounts.get(1));
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Biome tag reload regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }
}
