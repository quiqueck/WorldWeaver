package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.loot.api.LootAddition;
import de.ambertation.wover.loot.api.LootAdditionFile;
import de.ambertation.wover.loot.api.LootAdditionOp;
import de.ambertation.wover.loot.impl.LootAdditionsImpl;
import de.ambertation.wover.loot.impl.LootTableAppendersImpl;
import de.ambertation.wover.loot.mixin.LootTableAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.JsonOps;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for the <b>datapack</b> half of the loot-api: "a {@code wover/loot_addition} JSON file
 * shipped by a pack lands on a vanilla loot table exactly once, reaches a real container, and survives
 * re-application".
 * <p>
 * The testmod ships two files under {@code data/wover-loot-testmod/wover/loot_addition/}. No Java appender is
 * involved anywhere in this class - every effect asserted here has to have come out of the JSON:
 * <ul>
 *     <li>{@code datapack_additions.json} adds a guaranteed {@link #POOL_MARKER} pool to
 *     {@code minecraft:chests/jungle_temple}; adds a {@link #ENTRY_MARKER} entry to every pool of
 *     {@code minecraft:chests/igloo_chest}; gates every pool of {@code minecraft:chests/underwater_ruin_big}
 *     with an always-false condition and then appends a {@link #CONDITION_MARKER} pool; and contains one
 *     addition with {@code "required": false} aimed at a loot table that does not exist.</li>
 *     <li>{@code gated_addition.json} would add a {@link #GATED_MARKER} pool to
 *     {@code minecraft:chests/shipwreck_supply}, but is gated behind
 *     {@code fabric:all_mods_loaded} on a mod that is not installed, so it must never apply.</li>
 * </ul>
 * All four markers are items that occur in no vanilla loot table at all, so finding one is unambiguous.
 *
 * <h2>What each arm rules out</h2>
 * <ul>
 *     <li><b>Positive, at container level</b> ({@link #datapackAdditionReachesARealContainer}): a chest
 *     pointed at the extended table and filled through {@code getItem → unpackLootTable → LootTable.fill} -
 *     the code path a player triggers - holds exactly one marker, next to the vanilla loot. Zero would mean
 *     the file never loaded; more than one that it was applied twice; no vanilla loot that the table was
 *     replaced rather than extended.</li>
 *     <li><b>Negative control</b> ({@link #untargetedAndGatedTablesStayClean}): a table no file mentions stays
 *     clean, so an implementation that injected into every table cannot pass. And the resource-condition-gated
 *     table stays clean too, while still being non-empty - the arm that fails if
 *     {@code fabric:load_conditions} were ignored.</li>
 *     <li><b>{@code required: false}</b>: the missing-target addition sits in the same file as the
 *     jungle_temple one. If a missing optional target aborted its file, the positive arm loses its marker.</li>
 *     <li><b>Idempotency</b> ({@link #datapackAdditionsSurviveReapplication}): the whole application is driven
 *     twice more over the live registry and nothing may move - pool count, the encoded entries of every pool,
 *     and the encoded <em>conditions</em> of every pool. The last one is the point: a behaviour-only check
 *     cannot tell a correctly re-derived restriction apart from one that keeps re-appending the same condition
 *     to a growing list on every pass but still evaluates the same way today.</li>
 *     <li><b>Merge semantics</b> ({@link #replaceAndAppendMergeLikeVanillaTags}): the pure merge step, over
 *     hand-built files, because "two packs ship the same file id" cannot be staged from a single mod jar.</li>
 * </ul>
 */
public class LootDatapackAdditionGameTest {
    private static final int ROLLS = 8;

    /** Extended by {@code datapack_additions.json} with a guaranteed {@link #POOL_MARKER} pool. */
    private static final ResourceKey<LootTable> POOL_TABLE = BuiltInLootTables.JUNGLE_TEMPLE;
    /** Gets a {@link #ENTRY_MARKER} entry added to every one of its pools. */
    private static final ResourceKey<LootTable> ENTRY_TABLE = BuiltInLootTables.IGLOO_CHEST;
    /** Every pool gated off, then a {@link #CONDITION_MARKER} pool appended. */
    private static final ResourceKey<LootTable> CONDITION_TABLE = BuiltInLootTables.UNDERWATER_RUIN_BIG;
    /** Targeted only by the resource-condition-gated file, which must never apply. */
    private static final ResourceKey<LootTable> GATED_TABLE = BuiltInLootTables.SHIPWRECK_SUPPLY;
    /** Named by no addition at all - the leak control. */
    private static final ResourceKey<LootTable> UNTOUCHED_TABLE = BuiltInLootTables.PILLAGER_OUTPOST;

    // Items that appear in no vanilla loot table, so a marker in a container can only have come from the
    // testmod's datapack files.
    private static final Item POOL_MARKER = Items.BARRIER;
    private static final Item ENTRY_MARKER = Items.JIGSAW;
    private static final Item CONDITION_MARKER = Items.STRUCTURE_BLOCK;
    private static final Item GATED_MARKER = Items.LIGHT;

    private static final BlockPos EXTENDED_CHEST = new BlockPos(1, 1, 1);
    private static final BlockPos GATED_CHEST = new BlockPos(5, 1, 1);
    private static final BlockPos UNTOUCHED_CHEST = new BlockPos(1, 1, 5);

    private static final long SEED_A = 0xDA7A_1L;
    private static final long SEED_B = 0xDA7A_2L;
    private static final long SEED_C = 0xDA7A_3L;

    @GameTest
    public void datapackAdditionReachesARealContainer(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final ChestBlockEntity chest = placeChest(helper, EXTENDED_CHEST, POOL_TABLE, SEED_A);
        final int markers = count(chest, POOL_MARKER);
        final int total = countAll(chest);

        if (markers != 1) {
            failures.add("a chest filled from " + POOL_TABLE.identifier() + " held " + markers + " "
                    + POOL_MARKER + "(s) instead of 1 (0 = the wover/loot_addition file never reached the"
                    + " container path - note the same file also carries a \"required\": false addition aimed"
                    + " at a table that does not exist, so a mishandled optional target lands here too;"
                    + " >1 = the addition was applied more than once)");
        }
        if (total <= markers) {
            failures.add("a chest filled from " + POOL_TABLE.identifier() + " held nothing but the marker ("
                    + total + " stack(s)) - the datapack addition replaced the vanilla table instead of"
                    + " extending it");
        }

        // The gated-off table: only the JSON-appended pool may still roll.
        final LootTable conditioned = table(helper, CONDITION_TABLE, failures);
        if (conditioned != null) {
            final int[] rolls = rollFor(helper, conditioned, CONDITION_MARKER);
            if (rolls[0] != ROLLS) {
                failures.add("expected all " + ROLLS + " rolls of " + CONDITION_TABLE.identifier()
                        + " to yield only " + CONDITION_MARKER + " (the JSON add_condition gates every vanilla"
                        + " pool off), but " + (ROLLS - rolls[0]) + " roll(s) also produced vanilla items");
            }
            if (rolls[1] != ROLLS) {
                failures.add("expected every roll of " + CONDITION_TABLE.identifier() + " to include exactly"
                        + " one " + CONDITION_MARKER + ", but only " + rolls[1] + "/" + ROLLS + " did - the"
                        + " JSON-appended replacement pool got restricted too, or never landed");
            }
        }

        // The add_entries op: exactly one marker entry in each existing pool, no more.
        final LootTable entryTable = table(helper, ENTRY_TABLE, failures);
        if (entryTable != null) {
            final List<Integer> perPool = markerEntriesPerPool(entryTable, ENTRY_MARKER);
            if (perPool.isEmpty()) {
                failures.add(ENTRY_TABLE.identifier() + " has no pools at all");
            } else if (perPool.stream().anyMatch(c -> c != 1)) {
                failures.add("expected exactly one " + ENTRY_MARKER + " entry in each pool of "
                        + ENTRY_TABLE.identifier() + " but found " + perPool + " (0 = add_entries with"
                        + " \"pool\": \"every\" did not reach that pool, >1 = it was applied repeatedly)");
            }
        }

        report(helper, failures);
    }

    @GameTest
    public void untargetedAndGatedTablesStayClean(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final ChestBlockEntity untouched = placeChest(helper, UNTOUCHED_CHEST, UNTOUCHED_TABLE, SEED_B);
        if (countAll(untouched) == 0) {
            failures.add("the control chest is empty, so \"no marker\" proves nothing: "
                    + UNTOUCHED_TABLE.identifier() + " never filled it");
        }
        for (Item marker : List.of(POOL_MARKER, ENTRY_MARKER, CONDITION_MARKER, GATED_MARKER)) {
            final int found = count(untouched, marker);
            if (found != 0) {
                failures.add("a chest filled from " + UNTOUCHED_TABLE.identifier() + " held " + found + " "
                        + marker + "(s) - no addition names that table, so additions are leaking into tables"
                        + " they were not written for");
            }
        }

        final ChestBlockEntity gated = placeChest(helper, GATED_CHEST, GATED_TABLE, SEED_C);
        if (countAll(gated) == 0) {
            failures.add("the resource-condition control chest is empty, so \"no marker\" proves nothing: "
                    + GATED_TABLE.identifier() + " never filled it");
        }
        final int gatedMarkers = count(gated, GATED_MARKER);
        if (gatedMarkers != 0) {
            failures.add("a chest filled from " + GATED_TABLE.identifier() + " held " + gatedMarkers + " "
                    + GATED_MARKER + "(s) - gated_addition.json is behind a fabric:all_mods_loaded condition"
                    + " on a mod that is not installed, so it must never have been applied");
        }

        report(helper, failures);
    }

    @GameTest
    public void datapackAdditionsSurviveReapplication(GameTestHelper helper) {
        final MinecraftServer server = helper.getLevel().getServer();
        final HolderLookup.Provider reloadable = server.reloadableRegistries().lookup();

        final List<String> failures = new ArrayList<>();
        final LootTable poolTable = table(helper, POOL_TABLE, failures);
        final LootTable entryTable = table(helper, ENTRY_TABLE, failures);
        final LootTable conditionTable = table(helper, CONDITION_TABLE, failures);
        if (!failures.isEmpty()) {
            report(helper, failures);
            return;
        }

        final List<String> before = snapshot(poolTable, entryTable, conditionTable);
        final int markersBefore = rollFor(helper, poolTable, POOL_MARKER)[1];

        // Exactly the code path the mixin runs, over the live, already-extended registry. A datapack path that
        // re-parsed and re-appended (rather than re-deriving from the pools the packs provided) doubles here.
        LootTableAppendersImpl.applyAll(server.registryAccess(), reloadable);
        LootTableAppendersImpl.applyAll(server.registryAccess(), reloadable);

        final List<String> after = snapshot(poolTable, entryTable, conditionTable);
        final int markersAfter = rollFor(helper, poolTable, POOL_MARKER)[1];

        if (markersBefore != ROLLS) {
            failures.add("expected every one of " + ROLLS + " rolls of " + POOL_TABLE.identifier()
                    + " to yield exactly one " + POOL_MARKER + " but only " + markersBefore + " did");
        }
        if (markersAfter != markersBefore) {
            failures.add("re-applying changed how many rolls of " + POOL_TABLE.identifier() + " yield exactly"
                    + " one " + POOL_MARKER + ": " + markersBefore + "/" + ROLLS + " -> " + markersAfter + "/"
                    + ROLLS + " (the datapack additions stack)");
        }
        if (!after.equals(before)) {
            failures.add("re-applying changed the serialized pools of the datapack-extended tables (the"
                    + " additions are accumulating instead of being re-derived from the original snapshot):"
                    + firstDifference(before, after));
        }

        report(helper, failures);
    }

    /**
     * The merge step in isolation. Two packs shipping the same file id cannot be staged from one mod jar, so
     * the rules {@link LootAdditionFile} documents are asserted against
     * {@link LootAdditionsImpl#merge(List)} directly: pack order is preserved, {@code replace: false} appends,
     * and {@code replace: true} discards everything the lower packs contributed for that id.
     */
    @GameTest
    public void replaceAndAppendMergeLikeVanillaTags(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final LootAddition low = addition(BuiltInLootTables.JUNGLE_TEMPLE);
        final LootAddition mid = addition(BuiltInLootTables.IGLOO_CHEST);
        final LootAddition high = addition(BuiltInLootTables.PILLAGER_OUTPOST);

        final LootAdditionFile appendLow = new LootAdditionFile(false, List.of(), List.of(low));
        final LootAdditionFile appendMid = new LootAdditionFile(false, List.of(), List.of(mid));
        final LootAdditionFile replaceHigh = new LootAdditionFile(true, List.of(), List.of(high));

        expect(failures, "a single file",
                LootAdditionsImpl.merge(List.of(appendLow)), List.of(low));
        expect(failures, "replace: false appends in pack order",
                LootAdditionsImpl.merge(List.of(appendLow, appendMid)), List.of(low, mid));
        expect(failures, "replace: true discards the packs below it",
                LootAdditionsImpl.merge(List.of(appendLow, appendMid, replaceHigh)), List.of(high));
        expect(failures, "a file above a replace still appends to it",
                LootAdditionsImpl.merge(List.of(appendLow, replaceHigh, appendMid)), List.of(high, mid));

        report(helper, failures);
    }

    private static LootAddition addition(ResourceKey<LootTable> target) {
        return new LootAddition(
                List.of(target),
                true,
                List.of(new LootAdditionOp.AddEntries(
                        LootAdditionOp.PoolSelector.EVERY,
                        List.of(net.minecraft.world.level.storage.loot.entries.LootItem
                                .lootTableItem(Items.BARRIER)
                                .build())
                ))
        );
    }

    private static void expect(List<String> failures, String what, List<LootAddition> actual, List<LootAddition> expected) {
        if (actual.size() != expected.size()) {
            failures.add(what + ": expected " + expected.size() + " addition(s) but got " + actual.size());
            return;
        }
        for (int i = 0; i < expected.size(); i++) {
            if (actual.get(i) != expected.get(i)) {
                failures.add(what + ": addition #" + i + " is " + actual.get(i).targets()
                        + " but should be " + expected.get(i).targets());
            }
        }
    }

    // --- helpers ---------------------------------------------------------------------------------------

    private static LootTable table(GameTestHelper helper, ResourceKey<LootTable> key, List<String> failures) {
        final HolderLookup.Provider reloadable = helper.getLevel().getServer().reloadableRegistries().lookup();
        final LootTable table = reloadable
                .lookupOrThrow(Registries.LOOT_TABLE)
                .get(key)
                .map(Holder::value)
                .orElse(null);
        if (table == null) failures.add(key.identifier() + " is missing from the loot registry");
        return table;
    }

    private static ChestBlockEntity placeChest(
            GameTestHelper helper,
            BlockPos pos,
            ResourceKey<LootTable> table,
            long seed
    ) {
        helper.setBlock(pos, Blocks.CHEST);
        final ChestBlockEntity chest = helper.getBlockEntity(pos, ChestBlockEntity.class);
        chest.setLootTable(table, seed);
        return chest;
    }

    /** Reads every slot - which is what triggers {@code unpackLootTable} - and counts {@code marker}. */
    private static int count(ChestBlockEntity chest, Item marker) {
        int found = 0;
        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
            final ItemStack stack = chest.getItem(slot);
            if (stack.getItem() == marker) found += stack.getCount();
        }
        return found;
    }

    private static int countAll(ChestBlockEntity chest) {
        int count = 0;
        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
            if (!chest.getItem(slot).isEmpty()) count++;
        }
        return count;
    }

    /**
     * Rolls {@code table} {@link #ROLLS} times.
     *
     * @return {@code [rollsThatYieldedOnlyTheMarker, rollsThatIncludedExactlyOneMarker]}
     */
    private static int[] rollFor(GameTestHelper helper, LootTable table, Item marker) {
        final LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(helper.absolutePos(BlockPos.ZERO)))
                .create(table.getParamSet());

        int onlyMarker = 0;
        int exactlyOne = 0;
        for (int i = 0; i < ROLLS; i++) {
            int markers = 0;
            int others = 0;
            for (ItemStack stack : table.getRandomItems(params)) {
                if (stack.getItem() == marker) markers += stack.getCount();
                else others += stack.getCount();
            }
            if (markers == 1) exactlyOne++;
            if (markers == 1 && others == 0) onlyMarker++;
        }
        return new int[]{onlyMarker, exactlyOne};
    }

    /** How many {@code marker} entries each pool of {@code table} holds, in order. */
    private static List<Integer> markerEntriesPerPool(LootTable table, Item marker) {
        final String needle = "\"" + BuiltInRegistries.ITEM.getKey(marker) + "\"";
        final List<Integer> counts = new ArrayList<>();
        for (LootPool pool : pools(table)) {
            int found = 0;
            for (LootPoolEntryContainer entry : pool.entries) {
                if (encode(entry).contains(needle)) found++;
            }
            counts.add(found);
        }
        return counts;
    }

    /**
     * A full structural snapshot of the datapack-extended tables: for every pool, its encoded conditions and
     * every one of its encoded entries. Encoding through the very codecs the datapack path decodes with is
     * what makes "the restriction grew another entry but still evaluates the same" a failure rather than an
     * invisible leak.
     */
    private static List<String> snapshot(LootTable... tables) {
        final List<String> lines = new ArrayList<>();
        for (LootTable table : tables) {
            final List<LootPool> pools = pools(table);
            lines.add("pools=" + pools.size());
            for (LootPool pool : pools) {
                lines.add("  conditions=" + LootItemCondition.DIRECT_CODEC
                        .listOf()
                        .encodeStart(JsonOps.INSTANCE, pool.conditions)
                        .result()
                        .map(Object::toString)
                        .orElse("<encode failed>"));
                for (LootPoolEntryContainer entry : pool.entries) {
                    lines.add("  entry=" + encode(entry));
                }
            }
        }
        return lines;
    }

    private static String encode(LootPoolEntryContainer entry) {
        return LootPoolEntries.CODEC
                .encodeStart(JsonOps.INSTANCE, entry)
                .result()
                .map(Object::toString)
                .orElse("<encode failed>");
    }

    private static List<LootPool> pools(LootTable table) {
        return ((LootTableAccessor) (Object) table).wover_getPools();
    }

    private static String firstDifference(List<String> before, List<String> after) {
        final int max = Math.max(before.size(), after.size());
        for (int i = 0; i < max; i++) {
            final String b = i < before.size() ? before.get(i) : "<missing>";
            final String a = i < after.size() ? after.get(i) : "<missing>";
            if (!b.equals(a)) {
                return "\n   line " + i + " before: " + b + "\n   line " + i + " after:  " + a
                        + "\n   (" + before.size() + " -> " + after.size() + " lines in total)";
            }
        }
        return "\n   (identical line by line, but the lists compare unequal)";
    }

    private static void report(GameTestHelper helper, List<String> failures) {
        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Datapack loot addition regression:\n - " + String.join("\n - ", failures)));
            return;
        }
        helper.succeed();
    }
}
