package de.ambertation.wover.testmod.entrypoint;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.concurrent.atomic.AtomicBoolean;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * "Does a WoVer surface rule actually change generated blocks", not just "is it registered" - the
 * existing {@link de.ambertation.wover.testmod.gametest.SurfaceRuleGameTest} only checks the latter
 * (registry presence), which can't catch a rule that's registered correctly but never actually fires
 * (wrong condition, wrong biome key, priority/ordering bug against another rule, etc).
 * <p>
 * Deliberately <b>not</b> a {@code @GameTest}: vanilla's {@code GameTestServer} hardcodes
 * {@code WorldPresets.FLAT} for every dimension it creates (see {@code CompatWorldgenBootCheck} in
 * wover-test-api for the full bytecode-level confirmation), so it never runs real noise-based chunk
 * generation - a GameTest run's terrain doesn't come from surface rules at all, real or otherwise.
 * <p>
 * Also deliberately <b>not</b> a search for a naturally-occurring biome (the approach
 * {@code CompatWorldgenBootCheck} uses for "is this namespace present anywhere", via
 * {@code BiomeSource.findClosestBiome3d}): that's search-based and seed-dependent, fine for "is this
 * namespace present somewhere" but unnecessarily slow and non-deterministic for "does this specific
 * rule fire". Since WoVer's whole point is that biomes and surface rules are driven by data, this
 * instead ships a purpose-built test dimension
 * ({@code data/wover-surface-testmod/dimension/surface_rule_check.json}) whose generator uses a
 * {@code minecraft:fixed} biome source pinned to {@code minecraft:plains} - every chunk in that
 * dimension, including chunk (0,0), is guaranteed to be plains, no search needed. WoVer's own surface
 * rule injection ({@code SurfaceRuleUtil.injectSurfaceRulesToAllDimensions}) is dimension-agnostic, so
 * the testmod's {@code test-plains} rule (committed datapack JSON, replaces the floor with
 * {@code acacia_planks}) applies here exactly as it would in the real overworld.
 * <p>
 * Boots a plain dedicated server (the same {@code ServerLifecycleEvents.SERVER_STARTED}-then-halt
 * pattern as {@code CompatWorldgenBootCheck}), forces chunk (0,0) of the test dimension to
 * {@link ChunkStatus#FULL}, and scans it for {@link Blocks#ACACIA_PLANKS}. On failure, halts via
 * {@code Runtime.halt(1)} instead of a clean {@code server.halt(false)} - a clean shutdown would let the
 * JVM exit 0, which would make this task report success to Gradle regardless of what the check found.
 * <p>
 * Gated behind {@value #ENABLED_PROPERTY} (only its own dedicated {@code runSurfaceRuleCheck} task
 * passes it): this class is on the testmod's normal {@code main} entrypoint list (loaded on every
 * environment, client or server), so without the guard it would also register on the ordinary
 * {@code testmodServer} dev run and kill that server the moment anyone started it.
 */
public class SurfaceRuleAppliesBootCheck implements ModInitializer {
    private static final String ENABLED_PROPERTY = "wover.surface.runSurfaceRuleCheck";
    private static final Identifier DIMENSION_ID = Identifier.fromNamespaceAndPath(
            "wover-surface-testmod", "surface_rule_check"
    );

    @Override
    public void onInitialize() {
        if (!Boolean.getBoolean(ENABLED_PROPERTY)) {
            return;
        }
        ServerLifecycleEvents.SERVER_STARTED.register(SurfaceRuleAppliesBootCheck::runCheck);
    }

    private static void runCheck(MinecraftServer server) {
        try {
            final ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, DIMENSION_ID);
            final ServerLevel level = server.getLevel(dimensionKey);
            if (level == null) {
                fail("dimension " + DIMENSION_ID + " did not load - check data/wover-surface-testmod/dimension/surface_rule_check.json");
                return;
            }

            final ChunkAccess chunk = level.getChunkSource().getChunk(0, 0, ChunkStatus.FULL, true);
            if (chunk == null) {
                fail("chunk (0,0) in " + DIMENSION_ID + " failed to generate to FULL status");
                return;
            }

            final AtomicBoolean found = new AtomicBoolean(false);
            chunk.findBlocks(state -> state.is(Blocks.ACACIA_PLANKS), (pos, state) -> found.set(true));

            if (!found.get()) {
                fail("chunk (0,0) in " + DIMENSION_ID + " (fixed-biome plains) contains no "
                        + Blocks.ACACIA_PLANKS + " - the test-plains surface rule did not fire");
                return;
            }

            System.out.println("[wover-surface] surface-rule-applies-boot-check: PASS");
            server.halt(false);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("uncaught exception during the check: " + t);
        }
    }

    private static void fail(String reason) {
        System.err.println("[wover-surface] surface-rule-applies-boot-check: FAIL - " + reason);
        // Runtime.halt(1), not server.halt(false): a clean server shutdown lets the JVM exit 0, which
        // would make this task report success to Gradle regardless of what the check actually found -
        // the whole point of this check is a deterministic exit code, same principle as everywhere else
        // in the compat-test framework.
        Runtime.getRuntime().halt(1);
    }
}
