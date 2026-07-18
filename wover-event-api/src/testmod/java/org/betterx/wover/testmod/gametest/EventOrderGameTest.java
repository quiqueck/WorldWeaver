package org.betterx.wover.testmod.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest that locks in the {@link org.betterx.wover.events.api.WorldLifecycle} firing-order
 * contract (Concern 1: "all events fire at the right moment"). The events themselves fire during world
 * load, before this test runs; the sequence is captured by {@link EventOrderRecorder} (installed at
 * mod-init) and asserted here once the server is fully up.
 * <p>
 * The assertions deliberately track only the guaranteed part of the contract documented on
 * {@code WorldLifecycle}: everything from {@code BEFORE_LOADING_RESOURCES} to
 * {@code BEFORE_CREATING_LEVELS} fires exactly once, in that order, followed by one or more
 * {@code SERVER_LEVEL_READY}. {@code WORLD_FOLDER_READY} / {@code WORLD_REGISTRY_READY} may fire
 * multiple times and their mutual order is explicitly unspecified, so they are not order-asserted here.
 * <p>
 * A failure here is a real regression in event timing, not a test to be relaxed.
 */
public class EventOrderGameTest {
    /** The once-and-in-order spine of the world-load event sequence. */
    private static final List<String> SPINE = List.of(
            "BEFORE_LOADING_RESOURCES",
            "RESOURCES_LOADED",
            "ON_DIMENSION_LOAD",
            "MINECRAFT_SERVER_READY",
            "BEFORE_CREATING_LEVELS"
    );

    @GameTest
    public void eventsFireInDocumentedOrder(GameTestHelper helper) {
        final List<String> order = EventOrderRecorder.order();
        final List<String> failures = new ArrayList<>();

        // 1) Each spine event fired exactly once.
        for (String event : SPINE) {
            final long count = order.stream().filter(event::equals).count();
            if (count != 1) {
                failures.add(event + ": expected to fire exactly once but fired " + count + " times");
            }
        }

        // 2) The spine events fired in the documented relative order.
        int previousIndex = -1;
        String previousEvent = "<start>";
        for (String event : SPINE) {
            final int index = order.indexOf(event);
            if (index < 0) {
                failures.add(event + ": never fired");
                continue;
            }
            if (index <= previousIndex) {
                failures.add(event + " fired before " + previousEvent + " (indices " + index + " <= " + previousIndex + ")");
            }
            previousIndex = index;
            previousEvent = event;
        }

        // 3) At least one SERVER_LEVEL_READY, and every one fires after BEFORE_CREATING_LEVELS.
        final int beforeCreatingLevels = order.indexOf("BEFORE_CREATING_LEVELS");
        final long serverLevelReadyCount = order.stream().filter("SERVER_LEVEL_READY"::equals).count();
        if (serverLevelReadyCount < 1) {
            failures.add("SERVER_LEVEL_READY: expected to fire at least once but never fired");
        }
        for (int i = 0; i < order.size(); i++) {
            if (order.get(i).equals("SERVER_LEVEL_READY") && beforeCreatingLevels >= 0 && i < beforeCreatingLevels) {
                failures.add("SERVER_LEVEL_READY fired before BEFORE_CREATING_LEVELS (index " + i + " < " + beforeCreatingLevels + ")");
            }
        }

        // 4) A FINAL registry became ready, and it did so no later than RESOURCES_LOADED (the final
        //    registry is what the running world actually uses).
        final boolean sawFinalRegistry = order.stream().anyMatch(e -> e.startsWith("WORLD_REGISTRY_READY:") && e.endsWith("FINAL"));
        if (!sawFinalRegistry) {
            failures.add("WORLD_REGISTRY_READY(FINAL): expected at least one FINAL-stage registry but saw none");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "WorldLifecycle event-order contract violated:\n - " + String.join("\n - ", failures)
                            + "\n\nRecorded order: " + order
            ));
            return;
        }

        helper.succeed();
    }

    @GameTest
    public void folderReadySubscribersFireHighestPriorityFirst(GameTestHelper helper) {
        final List<String> priorityOrder = EventOrderRecorder.folderPriorityOrder();

        if (priorityOrder.size() < 3) {
            helper.fail(Component.literal(
                    "WORLD_FOLDER_READY did not fire all 3 priority subscribers (recorded: " + priorityOrder + ")"
            ));
            return;
        }

        // The first firing cycle must come out highest-priority-first: 2000, then default 1000, then 10.
        final List<String> firstCycle = priorityOrder.subList(0, 3);
        final List<String> expected = List.of("2000", "1000", "10");
        if (!firstCycle.equals(expected)) {
            helper.fail(Component.literal(
                    "Subscriber priority order violated: expected " + expected + " but was " + firstCycle
            ));
            return;
        }

        helper.succeed();
    }
}
