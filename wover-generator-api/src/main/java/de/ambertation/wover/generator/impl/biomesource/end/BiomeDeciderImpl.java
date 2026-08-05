package de.ambertation.wover.generator.impl.biomesource.end;

import de.ambertation.wover.generator.api.biomesource.end.BiomeDecider;

import net.minecraft.resources.Identifier;

import java.util.LinkedList;
import java.util.List;

public class BiomeDeciderImpl {
    static List<BiomeDecider> DECIDERS = new LinkedList<>();

    public static void registerHighPriorityDecider(Identifier location, BiomeDecider decider) {
        if (DECIDERS.size() == 0) DECIDERS.add(decider);
        else DECIDERS.add(0, decider);
    }

    public static void registerDecider(Identifier location, BiomeDecider decider) {
        DECIDERS.add(decider);
    }

}
