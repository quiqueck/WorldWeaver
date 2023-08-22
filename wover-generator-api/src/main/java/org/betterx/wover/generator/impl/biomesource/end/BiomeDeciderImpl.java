package org.betterx.wover.generator.impl.biomesource.end;

import org.betterx.wover.generator.api.biomesource.end.BiomeDecider;

import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;

public class BiomeDeciderImpl {
    static List<BiomeDecider> DECIDERS = new LinkedList<>();

    public static void registerHighPriorityDecider(ResourceLocation location, BiomeDecider decider) {
        if (DECIDERS.size() == 0) DECIDERS.add(decider);
        else DECIDERS.add(0, decider);
    }

    public static void registerDecider(ResourceLocation location, BiomeDecider decider) {
        DECIDERS.add(decider);
    }

}
