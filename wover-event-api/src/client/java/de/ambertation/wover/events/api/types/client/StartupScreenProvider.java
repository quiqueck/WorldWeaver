package de.ambertation.wover.events.api.types.client;

import de.ambertation.wover.events.api.Subscriber;

import net.minecraft.client.gui.screens.Screen;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface StartupScreenProvider extends Subscriber, Consumer<List<Function<Runnable, Screen>>> {
}
