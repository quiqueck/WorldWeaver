package de.ambertation.wover.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure-logic JUnit tests for {@link PriorityLinkedList} and, through it, its backing
 * {@link SortedLinkedList} insertion-sort. These need no Minecraft bootstrap and run via
 * {@code ./gradlew :wover-core-api:test}.
 * <p>
 * What is guarded: the priority ordering contract that the library relies on wherever ordered
 * subscribers/handlers are stored (higher priority runs first, lower priority ends up last, and
 * ties keep their insertion order). A Minecraft-version port that reshuffles the {@code util}
 * collections — e.g. flipping the comparator sign, breaking the {@code >= 0} tie handling in the
 * insertion sort, or mis-linking the {@code previous} pointer during insertion — would silently
 * change the order in which handlers fire. Each assertion below fails on exactly such a regression.
 */
class PriorityLinkedListTest {
    @Test
    void higherPriorityComesFirstLowerPriorityLast() {
        final PriorityLinkedList<String> list = new PriorityLinkedList<>();
        // Deliberately add out of order so a broken insertion sort cannot pass by accident.
        list.add("mid", 100);
        list.add("high", 200);
        list.add("low", 10);

        // Front-to-back must be strictly descending by priority regardless of insertion order.
        assertEquals(3, list.size());
        assertEquals("high", list.get(0));
        assertEquals("mid", list.get(1));
        assertEquals("low", list.get(2));

        // The iterator must yield the same order as index access.
        assertEquals(List.of("high", "mid", "low"), drain(list));
    }

    @Test
    void equalPrioritiesKeepInsertionOrder() {
        final PriorityLinkedList<String> list = new PriorityLinkedList<>();
        // All identical priority: the insertion sort must be stable (append after equal elements).
        list.add("first", 100);
        list.add("second", 100);
        list.add("third", 100);

        assertEquals(List.of("first", "second", "third"), drain(list));

        // A higher-priority element inserted afterwards must still jump to the front, and the
        // existing tie-group must retain its relative order behind it.
        list.add("winner", 500);
        assertEquals(List.of("winner", "first", "second", "third"), drain(list));
    }

    @Test
    void defaultPriorityPlacesElementByItsRank() {
        final PriorityLinkedList<String> list = new PriorityLinkedList<>();
        // add(T) uses DEFAULT_PRIORITY; sanity-check the constant is the documented 1000.
        assertEquals(1000, PriorityLinkedList.DEFAULT_PRIORITY);

        list.add("aboveDefault", 2000);
        list.add("default");                 // -> DEFAULT_PRIORITY (1000)
        list.add("belowDefault", 500);

        assertEquals(List.of("aboveDefault", "default", "belowDefault"), drain(list));
    }

    @Test
    void removeAndContainsOperateOnValues() {
        final PriorityLinkedList<String> list = new PriorityLinkedList<>();
        list.add("a", 300);
        list.add("b", 200);
        list.add("c", 100);

        assertTrue(list.contains("b"));
        assertTrue(list.remove("b"));        // remove a middle node
        assertFalse(list.contains("b"));
        assertEquals(List.of("a", "c"), drain(list));

        assertFalse(list.remove("missing")); // removing an absent value changes nothing
        assertEquals(2, list.size());
    }

    private static <T> List<T> drain(PriorityLinkedList<T> list) {
        return java.util.stream.StreamSupport
                .stream(list.spliterator(), false)
                .toList();
    }
}
