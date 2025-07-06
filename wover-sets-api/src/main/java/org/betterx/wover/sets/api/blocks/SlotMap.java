package org.betterx.wover.sets.api.blocks;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class SlotMap implements Iterable<SlotFactory> {
    private final Map<SlotType, SlotFactory> map;

    protected SlotMap() {
        this.map = new LinkedHashMap<>();
    }

    public static SlotMap of(SlotFactory... slots) {
        final SlotMap map = new SlotMap();
        for (SlotFactory slot : slots) {
            map.add(slot);
        }
        return map;
    }

    public SlotMap replace(SlotFactory definition) {
        return add(definition);
    }

    public SlotMap add(SlotFactory definition) {
        map.put(definition.slot(), definition);
        return this;
    }

    public SlotMap remove(SlotFactory definition) {
        return this.remove(definition.slot());
    }

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
