package org.betterx.wover.events.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SomeCommonAPI {
    public static void print() {
        System.out.println("Printed from SomeCommonAPI");
    }

    @Environment(EnvType.CLIENT)
    public static void printClient() {
        System.out.println("Printed from SomeCommonAPI [client]");
    }
}
