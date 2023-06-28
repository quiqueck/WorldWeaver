package org.betterx.test.wover.client;

import de.ambertation.wunderlib.math.Float3;
import org.betterx.wover.client.events.foo.SomeClientAPI;
import org.betterx.wover.events.api.SomeCommonAPI;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WoverClientTestMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("Hello from the Test-Mod [client]");
        SomeCommonAPI.print();
        SomeCommonAPI.printClient();
        SomeClientAPI.print();

        Float3 f = Float3.of(1, 2, 3);
        System.out.println("Float3: " + f);
    }
}
