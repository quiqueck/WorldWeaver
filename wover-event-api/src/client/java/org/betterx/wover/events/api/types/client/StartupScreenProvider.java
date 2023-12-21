package org.betterx.wover.events.api.types.client;

import org.betterx.wover.events.api.Subscriber;

import net.minecraft.client.gui.screens.Screen;

import java.util.function.Function;

@FunctionalInterface
public interface StartupScreenProvider extends Subscriber, Function<Runnable, Screen> {
}
