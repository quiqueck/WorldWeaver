package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.testmod.entrypoint.TestModWoverLoot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for the loot-api, at the level a player would notice: <b>a real chest, filling itself from a
 * real vanilla loot table, actually hands out the appended item.</b>
 * <p>
 * {@link LootAppenderGameTest} proves the appenders land on the registry exactly once. That is a statement
 * about {@code LootTable} objects; it says nothing about whether the container path uses the extended table.
 * This test closes that gap by going through vanilla's own machinery end to end: a chest is placed, its
 * {@code lootTable} field is pointed at {@link TestModWoverLoot#EXTENDED_TABLE}, and the fill is triggered by
 * simply reading a slot - {@code RandomizableContainerBlockEntity#getItem} calls
 * {@code unpackLootTable(null)}, which resolves the key through
 * {@code MinecraftServer#reloadableRegistries()} and calls {@code LootTable#fill}. That is the exact code path
 * a player triggers by opening a dungeon chest.
 * <p>
 * Three things are asserted, and the second is what makes the first mean anything:
 * <ol>
 *     <li><b>Positive:</b> a chest on the extended table contains exactly one marker - the right item
 *     <em>and</em> the custom name the appended pool stamps on it - alongside the vanilla loot, which proves
 *     the table was extended rather than replaced.</li>
 *     <li><b>Control:</b> a chest on {@link TestModWoverLoot#CONTROL_TABLE}, a vanilla table this testmod
 *     never registers an appender for, contains no marker at all - while still being non-empty, so
 *     "no marker" cannot be an unfilled chest. Without this, an implementation that injected into
 *     <em>every</em> table would sail through the positive case.</li>
 *     <li><b>World-level idempotency:</b> refilling repeatedly, and a second, independent chest, still yield
 *     exactly one marker each. {@link LootAppenderGameTest} covers the registry side of this; here it is the
 *     loot a player would actually receive.</li>
 * </ol>
 */
public class LootContainerGameTest {
    /** How often {@link #repeatedFillsNeverStackTheMarker} refills the chest. */
    private static final int REFILLS = 6;

    // Fixed loot seeds, so a failure is reproducible rather than "that one roll". The appended pool rolls
    // unconditionally, so the seed cannot change how many markers a fill produces either way.
    private static final long EXTENDED_SEED = 0x100D_1L;
    private static final long CONTROL_SEED = 0x100D_2L;
    private static final long SECOND_SEED = 0x100D_3L;

    // Far enough apart that two chests never merge into a double chest, and inside the 8x8x8 empty template.
    private static final BlockPos EXTENDED_CHEST = new BlockPos(1, 1, 1);
    private static final BlockPos CONTROL_CHEST = new BlockPos(5, 1, 1);
    private static final BlockPos SECOND_CHEST = new BlockPos(1, 1, 5);

    @GameTest
    public void chestOnExtendedVanillaTableContainsTheAppendedItem(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final ChestBlockEntity chest = placeChest(helper, EXTENDED_CHEST, TestModWoverLoot.EXTENDED_TABLE, EXTENDED_SEED);
        final int markers = countMarkers(chest, failures, "extended chest");
        final int total = countAll(chest);

        if (markers != 1) {
            failures.add("a chest filled from " + name(TestModWoverLoot.EXTENDED_TABLE) + " held " + markers
                    + " marker(s) instead of 1 (0 = the appended pool never reached the container path,"
                    + " >1 = it was appended more than once)");
        }
        if (total <= markers) {
            failures.add("a chest filled from " + name(TestModWoverLoot.EXTENDED_TABLE) + " held nothing but the"
                    + " marker (" + total + " stack(s) in total) - the appender replaced the vanilla table"
                    + " instead of extending it");
        }

        report(helper, failures);
    }

    @GameTest
    public void chestOnUnextendedVanillaTableStaysClean(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final ChestBlockEntity chest = placeChest(helper, CONTROL_CHEST, TestModWoverLoot.CONTROL_TABLE, CONTROL_SEED);
        final int markers = countMarkers(chest, failures, "control chest");
        final int total = countAll(chest);

        if (markers != 0) {
            failures.add("a chest filled from " + name(TestModWoverLoot.CONTROL_TABLE) + " held " + markers
                    + " marker(s) - that table has no appender registered, so the addition is leaking into"
                    + " tables it was not registered for");
        }
        if (total == 0) {
            failures.add("the control chest is empty, so \"no marker\" proves nothing: "
                    + name(TestModWoverLoot.CONTROL_TABLE) + " never filled it");
        }

        report(helper, failures);
    }

    @GameTest
    public void repeatedFillsNeverStackTheMarker(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        // Same block entity, filled over and over. unpackLootTable() clears the key after filling, so each
        // pass has to set it again - which is exactly what a freshly generated chest does.
        final ChestBlockEntity chest = placeChest(helper, EXTENDED_CHEST, TestModWoverLoot.EXTENDED_TABLE, EXTENDED_SEED);
        for (int i = 0; i < REFILLS; i++) {
            final int markers = countMarkers(chest, failures, "refill #" + (i + 1));
            if (markers != 1) {
                failures.add("refill #" + (i + 1) + " of the same chest yielded " + markers
                        + " marker(s) instead of 1");
            }
            chest.clearContent();
            chest.setLootTable(TestModWoverLoot.EXTENDED_TABLE, i + 1);
        }

        // ...and an independent chest, in case the first one had simply cached something.
        final ChestBlockEntity other = placeChest(helper, SECOND_CHEST, TestModWoverLoot.EXTENDED_TABLE, SECOND_SEED);
        final int markers = countMarkers(other, failures, "second chest");
        if (markers != 1) {
            failures.add("a second, independently placed chest yielded " + markers + " marker(s) instead of 1");
        }

        report(helper, failures);
    }

    /** Places a chest and points it at {@code table}; the fill only happens once a slot is read. */
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

    /**
     * Reads every slot - which is what triggers {@code unpackLootTable} - and counts the appended markers.
     * <p>
     * A marker only counts when both the item and the custom name the appended pool stamps on it match. A
     * stack that is the right item but carries no name is reported separately: that would mean something other
     * than this testmod's pool produced it.
     */
    private static int countMarkers(ChestBlockEntity chest, List<String> failures, String what) {
        int markers = 0;
        int unnamed = 0;
        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
            final ItemStack stack = chest.getItem(slot);
            if (stack.getItem() != TestModWoverLoot.POOL_MARKER) continue;

            final Component name = stack.get(DataComponents.CUSTOM_NAME);
            if (name != null && TestModWoverLoot.POOL_MARKER_NAME.equals(name.getString())) {
                markers += stack.getCount();
            } else {
                unnamed += stack.getCount();
            }
        }
        if (unnamed > 0) {
            failures.add(what + ": found " + unnamed + " " + TestModWoverLoot.POOL_MARKER
                    + " without the \"" + TestModWoverLoot.POOL_MARKER_NAME + "\" name - the marker did not"
                    + " come from the appended pool");
        }
        return markers;
    }

    /** The number of non-empty stacks in the chest. Assumes the chest has already been unpacked. */
    private static int countAll(ChestBlockEntity chest) {
        int count = 0;
        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
            if (!chest.getItem(slot).isEmpty()) count++;
        }
        return count;
    }

    private static String name(ResourceKey<LootTable> table) {
        return table.location().toString();
    }

    private static void report(GameTestHelper helper, List<String> failures) {
        if (!failures.isEmpty()) {
            helper.fail(Component.literal("Loot-appender container regression:\n - " + String.join("\n - ", failures)));
            return;
        }
        helper.succeed();
    }
}
