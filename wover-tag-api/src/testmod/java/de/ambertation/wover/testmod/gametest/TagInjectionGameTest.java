package de.ambertation.wover.testmod.gametest;

import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for the tag-api: custom tags declared through {@code TagManager.BLOCKS.makeTag(...)}
 * and populated in a {@code bootstrapEvent()} subscriber must reach the live tag map at runtime,
 * <em>without any committed tag JSON</em>.
 * <p>
 * There is deliberately no {@code data/wover-tag-testmod/tags/block/aa.json} on disk. The members are
 * instead injected by {@code TagLoaderMixin} (which forwards {@code TagLoader.load} into
 * {@code TagManagerImpl.didLoadTagMap}), so the whole runtime injection path is what this test guards.
 * The testmod ({@code TestModWoverTag}) registers tag {@code aa} and adds
 * {@link Blocks#ACACIA_BUTTON} and {@link Blocks#ACACIA_DOOR} to it (plus {@link Blocks#ACACIA_FENCE}
 * via the {@code aaa}/{@code aa} same-id dedup); it also puts {@link Blocks#ACACIA_BUTTON} into tag
 * {@code bb}.
 * <p>
 * This reads the tag membership back off the block's built-in registry holder once the server is up.
 * The negative case ({@link Blocks#STONE} not in {@code aa}) guarantees the assertion can actually
 * fail on regression rather than passing vacuously. A missing member is a real regression in the tag
 * injection mixin or the bootstrap-event plumbing.
 */
public class TagInjectionGameTest {
    private static final TagKey<Block> AA = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.parse("wover-tag-testmod:aa")
    );
    private static final TagKey<Block> BB = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.parse("wover-tag-testmod:bb")
    );

    @GameTest
    public void customBlockTagsAreInjectedAtRuntime(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        // Positive: members the testmod added must resolve through the injected tag map.
        if (!Blocks.ACACIA_BUTTON.builtInRegistryHolder().is(AA)) {
            failures.add("ACACIA_BUTTON expected in tag 'aa' but it is missing");
        }
        if (!Blocks.ACACIA_DOOR.builtInRegistryHolder().is(AA)) {
            failures.add("ACACIA_DOOR expected in tag 'aa' but it is missing");
        }
        if (!Blocks.ACACIA_BUTTON.builtInRegistryHolder().is(BB)) {
            failures.add("ACACIA_BUTTON expected in tag 'bb' but it is missing");
        }

        // Negative: a block that was never added must NOT be in the tag, otherwise the test
        // could never fail.
        if (Blocks.STONE.builtInRegistryHolder().is(AA)) {
            failures.add("STONE must NOT be in tag 'aa' (tag injection is over-broad)");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Tag-injection regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }
}
