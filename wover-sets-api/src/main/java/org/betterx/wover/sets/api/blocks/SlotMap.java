package org.betterx.wover.sets.api.blocks;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class SlotMap implements Iterable<SlotDefinition> {
    private final Map<SlotType, SlotDefinition> map;

    protected SlotMap() {
        this.map = new LinkedHashMap<>();
    }

    public static SlotMap of(SlotDefinition... slots) {
        final SlotMap map = new SlotMap();
        for (SlotDefinition slot : slots) {
            map.add(slot);
        }
        return map;
    }

    public SlotMap replace(SlotDefinition definition) {
        return add(definition);
    }

    public SlotMap add(SlotDefinition definition) {
        map.put(definition.slot, definition);
        return this;
    }

    public SlotMap remove(SlotDefinition definition) {
        return this.remove(definition.slot);
    }

    public SlotMap remove(SlotType slot) {
        map.remove(slot);
        return this;
    }

    @NotNull
    @Override
    public Iterator<SlotDefinition> iterator() {
        return map.values().iterator();
    }
}
