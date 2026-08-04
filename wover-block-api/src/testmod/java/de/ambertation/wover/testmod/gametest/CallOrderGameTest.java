package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.testmod.block.CallOrderTestBlocks;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest that locks in the call-order build() precedence contract from WorldWeaver commit
 * {@code 5dcd992} (see {@code MIGRATION_1.21.7_STATUS.md}, trap 0). The blocks under test are registered by
 * {@link CallOrderTestBlocks} at mod init; the definition's {@code build()} is a pure builder, so the
 * assertions do not need the world - the GameTest is only the harness that reports pass/fail at runtime.
 * <p>
 * If any case fails this documents a real regression (build() no longer honours call order), NOT a test to
 * be relaxed.
 */
public class CallOrderGameTest {
    // Property accessors, mirroring BetterNether/BetterEnd's BlockPropertiesProvider golden-file dump.
    private static float destroyTime(Block block) {
        return block.defaultDestroyTime();
    }

    private static float resistance(Block block) {
        return block.getExplosionResistance();
    }

    private static MapColor mapColor(Block block) {
        return block.defaultMapColor();
    }

    private static SoundType sound(Block block) {
        return block.defaultBlockState().getSoundType();
    }

    private static void expectDestroyTime(List<String> failures, String rule, float expected, Block block) {
        final float actual = destroyTime(block);
        if (actual != expected) {
            failures.add(rule + ": expected destroyTime=" + expected + " but was " + actual);
        }
    }

    private static void expectResistance(List<String> failures, String rule, float expected, Block block) {
        final float actual = resistance(block);
        if (actual != expected) {
            failures.add(rule + ": expected resistance=" + expected + " but was " + actual);
        }
    }

    /**
     * Verifies the four call-order rules (plus a sound/map-color interleave) with unmistakable numeric
     * markers: chain strength {@code 1}, trait strength {@code 7}, ctor strength {@code 13}, and the
     * {@code replacePropertiesWithCopy} base (Obsidian) {@code 50}.
     */
    @GameTest
    public void callOrderBuildPrecedence(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        // Case 1: .strength(1) THEN .addTrait(strength 7) -> trait is later in call order -> trait wins.
        expectDestroyTime(
                failures,
                "case 1 (trait after setter -> trait wins)",
                CallOrderTestBlocks.TRAIT_STRENGTH,
                CallOrderTestBlocks.CASE1_TRAIT_AFTER_SETTER
        );

        // Case 2: .addTrait(strength 7) THEN .strength(1) -> chain setter is later -> chain wins.
        // Trap-0 regression guard: proves interleaving-by-call-order, not "trait always wins".
        expectDestroyTime(
                failures,
                "case 2 (setter after trait -> setter wins) [TRAP-0 GUARD]",
                CallOrderTestBlocks.CHAIN_STRENGTH,
                CallOrderTestBlocks.CASE2_SETTER_AFTER_TRAIT
        );

        // Case 3: block factory sets strength(13) and runs LAST -> ctor wins over chain (1) AND trait (7).
        expectDestroyTime(
                failures,
                "case 3 (ctor/factory Properties wins over chain and trait)",
                CallOrderTestBlocks.CTOR_STRENGTH,
                CallOrderTestBlocks.CASE3_CTOR_WINS
        );

        // Case 4: replacePropertiesWithCopy(Obsidian) is the eager base; a later .strength(1) wins over it.
        expectDestroyTime(
                failures,
                "case 4 (copy is eager base, later setter wins)",
                CallOrderTestBlocks.CHAIN_STRENGTH,
                CallOrderTestBlocks.CASE4_COPY_IS_BASE
        );
        expectResistance(
                failures,
                "case 4 (copy is eager base, later setter wins)",
                CallOrderTestBlocks.CHAIN_STRENGTH,
                CallOrderTestBlocks.CASE4_COPY_IS_BASE
        );

        // Case 4b: replacePropertiesWithCopy(Obsidian) as base, then a property-bearing trait wins over it.
        expectDestroyTime(
                failures,
                "case 4b (copy is eager base, later trait wins)",
                CallOrderTestBlocks.TRAIT_STRENGTH,
                CallOrderTestBlocks.CASE4B_TRAIT_AFTER_COPY
        );

        // Case 5: sound(WOOL) -> trait{sound(METAL), map(BLUE)} -> sound(GRAVEL).
        // Sound: last chain op wins (GRAVEL). Map color: only the trait set it (BLUE).
        final Block case5 = CallOrderTestBlocks.CASE5_SOUND_MAP_INTERLEAVE;
        if (sound(case5) != CallOrderTestBlocks.LATE_SOUND) {
            failures.add(
                    "case 5 (chain sound after trait wins): expected sound=" + CallOrderTestBlocks.LATE_SOUND
                            + " but was " + sound(case5)
            );
        }
        if (mapColor(case5) == null || mapColor(case5).id != CallOrderTestBlocks.TRAIT_MAP_COLOR.id) {
            failures.add(
                    "case 5 (trait-only map color survives): expected mapColor id="
                            + CallOrderTestBlocks.TRAIT_MAP_COLOR.id
                            + " but was " + (mapColor(case5) == null ? "null" : mapColor(case5).id)
            );
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Call-order build() precedence contract violated (commit 5dcd992):\n - "
                            + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }
}
