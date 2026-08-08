package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.loot.impl.LootTableAppendersImpl;
import de.ambertation.wover.testmod.entrypoint.TestModWoverLoot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.JsonOps;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for the loot-api: "an appender registered at mod-init time lands on the loot table
 * exactly once, and re-applying it does not stack".
 * <p>
 * The testmod ({@code TestModWoverLoot}) appends an always-rolled {@link TestModWoverLoot#POOL_MARKER}
 * pool to {@code minecraft:chests/simple_dungeon} and a {@link TestModWoverLoot#ENTRY_MARKER} entry to the
 * pools of {@code minecraft:chests/abandoned_mineshaft}. Neither item occurs in the vanilla version of
 * either table, so their presence is unambiguous.
 * <p>
 * <b>The interesting half is the idempotency check.</b> The additions are made while the reloadable loot
 * table registry is being rebuilt, and that registry is rebuilt on every {@code /reload} and every world
 * load. An implementation that accumulated state - or that appended to the current pools rather than to the
 * pools the datapacks provided - would double its additions on the second pass, and the only symptom would be
 * twice the loot. The test therefore drives the application code a second and a third time over the live
 * registry and asserts that nothing moves: same pool count, same entry counts, and still exactly one marker
 * per roll. Reverting the snapshot logic in {@code LootTableAppendersImpl}/{@code MutableLootTableImpl} makes
 * this fail with two, then three dragon eggs per roll.
 */
public class LootAppenderGameTest {
    private static final int ROLLS = 8;

    @GameTest
    public void appendersApplyExactlyOnceAndSurviveReapplication(GameTestHelper helper) {
        // The loot registry lives only in the reloadable holder - MinecraftServer#registryAccess() is the
        // composite of the layers below it and does not contain the loot table registry.
        final MinecraftServer server = helper.getLevel().getServer();
        final HolderLookup.Provider reloadable = server.reloadableRegistries().lookup();
        final HolderLookup.RegistryLookup<LootTable> registry = reloadable.lookupOrThrow(Registries.LOOT_TABLE);

        final LootTable dungeon = registry.get(BuiltInLootTables.SIMPLE_DUNGEON).map(Holder::value).orElse(null);
        final LootTable mineshaft = registry.get(BuiltInLootTables.ABANDONED_MINESHAFT).map(Holder::value).orElse(null);

        final List<String> failures = new ArrayList<>();
        if (dungeon == null) failures.add("minecraft:chests/simple_dungeon is missing from the loot registry");
        if (mineshaft == null) failures.add("minecraft:chests/abandoned_mineshaft is missing from the loot registry");
        if (!failures.isEmpty()) {
            helper.fail(Component.literal("Loot-appender regression:\n - " + String.join("\n - ", failures)));
            return;
        }

        // Baseline, i.e. the state the mixin produced during this server's data load.
        final int markersPerRoll = countMarkers(helper, dungeon);
        final List<Integer> mineshaftEntryCounts = entryCounts(mineshaft);

        if (markersPerRoll != 1) {
            failures.add("expected exactly one " + TestModWoverLoot.POOL_MARKER + " per roll of simple_dungeon"
                    + " but got " + markersPerRoll + " (0 = the appended pool never arrived, >1 = it was added"
                    + " more than once)");
        }
        if (mineshaftEntryCounts.isEmpty()) {
            failures.add("abandoned_mineshaft has no pools at all");
        }

        // Re-apply twice over the very same, already-extended registry. This is the same code path the mixin
        // runs, so a stacking implementation shows up immediately.
        LootTableAppendersImpl.applyAll(server.registryAccess(), reloadable);
        LootTableAppendersImpl.applyAll(server.registryAccess(), reloadable);


        final int markersAfter = countMarkers(helper, dungeon);
        final List<Integer> mineshaftEntryCountsAfter = entryCounts(mineshaft);

        if (markersAfter != markersPerRoll) {
            failures.add("re-applying the appenders changed simple_dungeon: " + markersPerRoll + " -> "
                    + markersAfter + " " + TestModWoverLoot.POOL_MARKER + " per roll (the additions stack)");
        }
        if (!mineshaftEntryCountsAfter.equals(mineshaftEntryCounts)) {
            failures.add("re-applying the appenders changed abandoned_mineshaft's pool entry counts: "
                    + mineshaftEntryCounts + " -> " + mineshaftEntryCountsAfter + " (the additions stack)");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal("Loot-appender regression:\n - " + String.join("\n - ", failures)));
            return;
        }

        helper.succeed();
    }

    /**
     * Rolls the table a few times and returns how many {@link TestModWoverLoot#POOL_MARKER} stacks a roll
     * yields. The appended pool rolls exactly once and holds exactly that one entry, so the answer is the
     * same for every roll; rolling repeatedly only guards against a lucky single sample.
     *
     * @return the marker count per roll, or {@code -1} if the rolls disagreed
     */
    private static int countMarkers(GameTestHelper helper, LootTable table) {
        final LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(helper.absolutePos(net.minecraft.core.BlockPos.ZERO)))
                .create(table.getParamSet());

        int expected = -1;
        for (int i = 0; i < ROLLS; i++) {
            int found = 0;
            for (ItemStack stack : table.getRandomItems(params)) {
                if (stack.getItem() == TestModWoverLoot.POOL_MARKER) found++;
            }
            if (expected == -1) {
                expected = found;
            } else if (expected != found) {
                return -1;
            }
        }
        return expected;
    }

    /** The number of entries in each pool of the table, in order. */
    private static List<Integer> entryCounts(LootTable table) {
        final List<Integer> counts = new ArrayList<>();
        for (LootPool pool : ((de.ambertation.wover.loot.mixin.LootTableAccessor) (Object) table).wover_getPools()) {
            counts.add(pool.entries.size());
        }
        return counts;
    }

    /**
     * {@code addConditionToEveryPool}/{@code addConditionToPool} half of {@link LootAppenderGameTest}: "a
     * table gated to never roll, plus a replacement pool, only ever yields the replacement - and re-applying
     * doesn't change what the gate evaluates to, even structurally".
     * <p>
     * {@link TestModWoverLoot#CONDITIONED_TABLE} has every one of its vanilla pools gated with an always-false
     * condition, then gets {@link TestModWoverLoot#CONDITION_MARKER} appended as its own, unconditional pool -
     * BetterEnd's exact "suppress vanilla, add a replacement" shape for its in-End fishing override. A behaviour-only
     * check ("still just the marker after two more passes") cannot tell a correctly-idempotent restriction
     * apart from one that re-derives the same condition twice as long every pass but still happens to evaluate
     * the same way; this additionally compares the pools' serialized conditions across passes so any such
     * growth fails loudly instead of only after a codec/JSON diff seems relevant.
     */
    @GameTest
    public void conditionsGateExistingPoolsAndSurviveReapplication(GameTestHelper helper) {
        final MinecraftServer server = helper.getLevel().getServer();
        final HolderLookup.Provider reloadable = server.reloadableRegistries().lookup();
        final HolderLookup.RegistryLookup<LootTable> registry = reloadable.lookupOrThrow(Registries.LOOT_TABLE);

        final LootTable conditioned = registry.get(TestModWoverLoot.CONDITIONED_TABLE).map(Holder::value).orElse(null);
        final List<String> failures = new ArrayList<>();
        if (conditioned == null) {
            helper.fail(Component.literal(
                    TestModWoverLoot.CONDITIONED_TABLE.location() + " is missing from the loot registry"));
            return;
        }

        final int[] onlyMarker = onlyMarkerRolls(helper, conditioned, failures, "initial application");
        final List<String> conditionsBefore = encodedConditions(conditioned);

        if (onlyMarker[0] != ROLLS) {
            failures.add("expected every one of " + ROLLS + " rolls of "
                    + TestModWoverLoot.CONDITIONED_TABLE.location() + " to yield only the marker (vanilla"
                    + " pools gated off), but " + (ROLLS - onlyMarker[0]) + " roll(s) also produced vanilla"
                    + " items - addConditionToEveryPool did not actually restrict the existing pools");
        }
        if (onlyMarker[1] != ROLLS) {
            failures.add("expected every roll to include exactly one " + TestModWoverLoot.CONDITION_MARKER
                    + ", but only " + onlyMarker[1] + "/" + ROLLS + " did - the replacement pool itself got"
                    + " restricted, or never landed");
        }

        // Same re-application the pool/entry half of this class does, over the same live, already-extended
        // registry - the scenario a growing condition would show up in.
        LootTableAppendersImpl.applyAll(server.registryAccess(), reloadable);
        LootTableAppendersImpl.applyAll(server.registryAccess(), reloadable);

        final int[] onlyMarkerAfter = onlyMarkerRolls(helper, conditioned, failures, "after re-application");
        final List<String> conditionsAfter = encodedConditions(conditioned);

        if (onlyMarkerAfter[0] != ROLLS || onlyMarkerAfter[1] != ROLLS) {
            failures.add("re-applying the appenders changed " + TestModWoverLoot.CONDITIONED_TABLE.location()
                    + "'s rolls: only-marker " + onlyMarker[0] + "/" + ROLLS + " -> " + onlyMarkerAfter[0]
                    + "/" + ROLLS + ", marker-present " + onlyMarker[1] + "/" + ROLLS + " -> "
                    + onlyMarkerAfter[1] + "/" + ROLLS);
        }
        if (!conditionsAfter.equals(conditionsBefore)) {
            failures.add("re-applying the appenders changed the serialized pool conditions of "
                    + TestModWoverLoot.CONDITIONED_TABLE.location() + " (the restriction is accumulating"
                    + " instead of being re-derived from the original snapshot):\n   before: " + conditionsBefore
                    + "\n   after:  " + conditionsAfter);
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal("Loot-condition regression:\n - " + String.join("\n - ", failures)));
            return;
        }

        helper.succeed();
    }

    /**
     * Rolls {@code table} {@link #ROLLS} times.
     *
     * @return {@code [rollsThatYieldedOnlyTheMarker, rollsThatIncludedExactlyOneMarker]}
     */
    private static int[] onlyMarkerRolls(GameTestHelper helper, LootTable table, List<String> failures, String what) {
        final LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(helper.absolutePos(net.minecraft.core.BlockPos.ZERO)))
                .create(table.getParamSet());

        int onlyMarker = 0;
        int exactlyOneMarker = 0;
        for (int i = 0; i < ROLLS; i++) {
            int markerCount = 0;
            int otherCount = 0;
            for (ItemStack stack : table.getRandomItems(params)) {
                if (stack.getItem() == TestModWoverLoot.CONDITION_MARKER) markerCount += stack.getCount();
                else otherCount += stack.getCount();
            }
            if (markerCount == 1) exactlyOneMarker++;
            if (markerCount == 1 && otherCount == 0) onlyMarker++;
        }
        return new int[]{onlyMarker, exactlyOneMarker};
    }

    /**
     * The serialized conditions of every pool of {@code table}, in order, for structural before/after
     * comparison. A pool's conditions are a plain {@code List<LootItemCondition>}, ANDed together - unlike on
     * later Minecraft versions there is no single optional holder to unwrap.
     */
    private static List<String> encodedConditions(LootTable table) {
        final List<LootPool> pools =
                ((de.ambertation.wover.loot.mixin.LootTableAccessor) (Object) table).wover_getPools();
        return pools.stream().map(pool -> LootItemCondition.DIRECT_CODEC
                .listOf()
                .encodeStart(JsonOps.INSTANCE, pool.conditions)
                .result()
                .map(Object::toString)
                .orElse("<encode failed>")
        ).collect(Collectors.toList());
    }
}
