package org.betterx.test.wover;

import org.betterx.wover.events.api.SomeCommonAPI;

import net.fabricmc.api.ModInitializer;

public class WoverTestMod implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("Hello from the Test-Mod");
        SomeCommonAPI.print();
    }
}
