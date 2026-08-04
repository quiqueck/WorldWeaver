package de.ambertation.wover.testmod.gametest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for wover-sets-api: a single {@code WoodenBlockSet} definition ({@code TestWoodSet},
 * base name {@code wooden}) must expand into, and register, its whole family of member blocks (planks,
 * stairs, slab, fence, door, …) at runtime.
 * <p>
 * The set is declared once in the testmod as {@code new TestWoodSet().buildAndRegister()}; this test
 * reads {@link BuiltInRegistries#BLOCK}/{@link BuiltInRegistries#ITEM} back and asserts the expected
 * members exist. Because the block IDs are derived from the set's base name plus its slot definitions,
 * a regression in set expansion (a dropped slot, a renamed suffix) shows up here as a missing member.
 * The negative case guards against the test passing vacuously.
 */
public class SetGameTest {
    // Representative members a WoodenBlockSet must produce from base name "wooden".
    private static final List<String> EXPECTED_BLOCKS = List.of(
            "wover-sets-testmod:wooden_log",
            "wover-sets-testmod:wooden_planks",
            "wover-sets-testmod:wooden_stairs",
            "wover-sets-testmod:wooden_slab",
            "wover-sets-testmod:wooden_fence",
            "wover-sets-testmod:wooden_door",
            "wover-sets-testmod:wooden_trapdoor",
            "wover-sets-testmod:wooden_button",
            "wover-sets-testmod:wooden_wall"
    );

    // Set members are also given a BlockItem, so the same ids must resolve in the ITEM registry.
    private static final List<String> EXPECTED_ITEMS = List.of(
            "wover-sets-testmod:wooden_planks",
            "wover-sets-testmod:wooden_stairs"
    );

    // A plausible-but-nonexistent variant: proves the assertions are real (the test can fail).
    private static final String NOT_A_MEMBER = "wover-sets-testmod:wooden_totally_not_a_slot";

    @GameTest
    public void woodSetExpandsIntoItsMembers(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        for (String id : EXPECTED_BLOCKS) {
            if (!BuiltInRegistries.BLOCK.containsKey(ResourceLocation.parse(id))) {
                failures.add(id + ": expected set member block missing from BuiltInRegistries.BLOCK");
            }
        }
        for (String id : EXPECTED_ITEMS) {
            if (!BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(id))) {
                failures.add(id + ": expected set member item missing from BuiltInRegistries.ITEM");
            }
        }
        if (BuiltInRegistries.BLOCK.containsKey(ResourceLocation.parse(NOT_A_MEMBER))) {
            failures.add(NOT_A_MEMBER + ": a non-member block unexpectedly exists (test would pass vacuously)");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "WoodenBlockSet expansion regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }
}
