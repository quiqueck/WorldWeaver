package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.util.RandomizedWeightedList;

import net.fabricmc.api.ModInitializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WoverWorldCoreTestMod implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-core-testmod");

    @Override
    public void onInitialize() {
        Random rnd = new Random();
        RandomizedWeightedList<String> strings = new RandomizedWeightedList<>();
        strings.add("A-2", 2);
        strings.add("B-1", 1);
        strings.add("D-4", 4);
        strings.add("E-2", 2);
        strings.add("F-8", 8);
        strings.add("G-3", 3);
        strings.add("H-5", 5);
        strings.add("I-1", 1);


        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < strings.getTotalWeight() * 100; i++) {
            counts.compute(strings.getRandomValue(rnd), (k, v) -> v == null ? 1 : v + 1);
        }

        System.out.println("List ------------------:\n" + counts);

        final RandomizedWeightedList<String>.SearchTree tree = strings.buildSearchTree();
        System.out.println(tree);
        counts.clear();
        for (int i = 0; i < strings.getTotalWeight() * 100; i++) {
            counts.compute(tree.getRandomValue(rnd), (k, v) -> v == null ? 1 : v + 1);
        }

        System.out.println("Tree ------------------:\n" + counts);
    }
}