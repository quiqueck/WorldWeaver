package de.ambertation.wover.events.impl.types.client;

import de.ambertation.wover.events.api.types.client.StartupScreenProvider;
import de.ambertation.wover.events.impl.AbstractEvent;

import net.minecraft.client.gui.screens.Screen;

import java.util.List;
import java.util.function.Function;

public class AdditionalStartupScreenEventImpl extends AbstractEvent<StartupScreenProvider> {
    public AdditionalStartupScreenEventImpl(String eventName) {
        super(eventName);
    }

    public final void process(List<Function<Runnable, Screen>> screens) {
        for (Subscriber<StartupScreenProvider> h : handlers) {
            h.task.accept(screens);
        }
    }
}
