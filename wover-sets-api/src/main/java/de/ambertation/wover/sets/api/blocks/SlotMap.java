package de.ambertation.wover.sets.api.blocks;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * An ordered, keyed-by-{@link SlotType} collection of {@link SlotFactory} instances, returned by
 * {@link BlockSet#createDefaultDefinitions()} to describe every block a set should build. Iteration order
 * follows insertion order, and adding a factory for a {@link SlotType} that is already present replaces the
 * previous one (see {@link #replace}) - this is how a {@link BlockSet} subclass overrides individual slots from
 * its parent's default set (see {@code TestWoodSet} in this module's test mod).
 */
public class SlotMap implements Iterable<SlotFactory> {
    private final Map<SlotType, SlotFactory> map;

    protected SlotMap() {
        this.map = new LinkedHashMap<>();
    }

    /**
     * @param slots the factories to seed the map with, in order; {@code null} entries are skipped
     * @return a new map containing every non-null factory
     */
    public static SlotMap of(SlotFactory... slots) {
        final SlotMap map = new SlotMap();
        for (SlotFactory slot : slots) {
            if (slot != null) map.add(slot);
        }
        return map;
    }

    /**
     * Alias for {@link #add(SlotFactory)}, for readability when overriding an existing slot.
     *
     * @param definition the factory to add, replacing any existing factory for the same slot
     * @return this map
     */
    public SlotMap replace(SlotFactory definition) {
        return add(definition);
    }

    /**
     * @param definition the factory to add, replacing any existing factory for the same slot
     * @return this map
     */
    public SlotMap add(SlotFactory definition) {
        map.put(definition.slot(), definition);
        return this;
    }

    /**
     * @param definition the factory whose slot should be removed
     * @return this map
     */
    public SlotMap remove(SlotFactory definition) {
        return this.remove(definition.slot());
    }

    /**
     * @param slot the slot to remove
     * @return this map
     */
    public SlotMap remove(SlotType slot) {
        map.remove(slot);
        return this;
    }

    @NotNull
    @Override
    public Iterator<SlotFactory> iterator() {
        return map.values().iterator();
    }
}
