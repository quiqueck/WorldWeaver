package org.betterx.wover.testmod.gametest;

import org.betterx.wover.testmod.block.TestBlockRegistry;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

/**
 * Fabric GameTest for Concern 2 ("all properties [...] stay unaltered"), complementing
 * {@link CallOrderGameTest} (which pins build() call-order precedence). Where CallOrderGameTest checks
 * how competing property sources interleave, this test checks the end-to-end result of applying a real
 * behaviour trait: the {@code FlammableBlockTrait} added to {@link TestBlockRegistry}'s blocks must
 * actually register them with Fabric's {@link FlammableBlockRegistry} at the expected burn/spread
 * chance, and plain property setters (e.g. {@code instabreak}) must survive onto the built block.
 * <p>
 * A failure here means a trait or property setter stopped taking effect - a real regression.
 */
public class TraitPropertyGameTest {
    // Default FlammableBlockTrait burn/spread chance (FlammableBlockBuilder.BUILDER.withDefault()).
    private static final int DEFAULT_FLAMMABLE_CHANCE = 5;

    private static void expectFlammable(List<String> failures, String name, Block block, int burn, int spread) {
        final FlammableBlockRegistry.Entry entry = FlammableBlockRegistry.getDefaultInstance().get(block);
        if (entry == null) {
            failures.add(name + ": expected a FlammableBlockRegistry entry (from FlammableBlockTrait) but found none");
            return;
        }
        if (entry.getBurnChance() != burn) {
            failures.add(name + ": expected burn chance " + burn + " but was " + entry.getBurnChance());
        }
        if (entry.getSpreadChance() != spread) {
            failures.add(name + ": expected spread chance " + spread + " but was " + entry.getSpreadChance());
        }
    }

    @GameTest
    public void flammableTraitRegistersBlocks(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        // All three testmod blocks carry FlammableBlockBuilder.BUILDER.withDefault() -> 5/5.
        expectFlammable(failures, "TEST_BLOCK", TestBlockRegistry.TEST_BLOCK, DEFAULT_FLAMMABLE_CHANCE, DEFAULT_FLAMMABLE_CHANCE);
        expectFlammable(failures, "TEST_DOOR", TestBlockRegistry.TEST_DOOR, DEFAULT_FLAMMABLE_CHANCE, DEFAULT_FLAMMABLE_CHANCE);
        expectFlammable(failures, "TEST_WALL", TestBlockRegistry.TEST_WALL, DEFAULT_FLAMMABLE_CHANCE, DEFAULT_FLAMMABLE_CHANCE);

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "FlammableBlockTrait registration regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }
        helper.succeed();
    }

    @GameTest
    public void plainPropertySettersSurvive(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        // TEST_BLOCK is built with .instabreak() -> destroy time must be 0.
        final float destroyTime = TestBlockRegistry.TEST_BLOCK.defaultDestroyTime();
        if (destroyTime != 0.0f) {
            failures.add("TEST_BLOCK: expected instabreak (destroyTime=0.0) but was " + destroyTime);
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Block property regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }
        helper.succeed();
    }
}
