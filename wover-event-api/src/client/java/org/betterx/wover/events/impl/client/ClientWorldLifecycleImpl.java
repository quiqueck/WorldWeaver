package org.betterx.wover.events.impl.client;

import org.betterx.wover.events.api.types.client.AfterWelcomeScreen;
import org.betterx.wover.events.api.types.client.ShowExperimentalWarningScreen;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.events.impl.types.ChainedEventImpl;
import org.betterx.wover.events.impl.types.client.AdditionalStartupScreenEventImpl;
import org.betterx.wover.events.impl.types.client.LoadScreenEventImpl;

public class ClientWorldLifecycleImpl {
    public static LoadScreenEventImpl BEFORE_CLIENT_LOAD_SCREEN = new LoadScreenEventImpl("BEFORE_CLIENT_LOAD_SCREEN");
    public static ChainedEventImpl<Boolean, ShowExperimentalWarningScreen> ALLOW_EXPERIMENTAL_WARNING_SCREEN = new ChainedEventImpl<>(
            "ALLOW_EXPERIMENTAL_WARNING_SCREEN");

    public static AdditionalStartupScreenEventImpl ENUMERATE_STARTUP_SCREENS = new AdditionalStartupScreenEventImpl(
            "ENUMERATE_STARTUP_SCREENS");
    public static EventImpl<AfterWelcomeScreen> AFTER_WELCOME_SCREEN = new EventImpl<>("AFTER_WELCOME_SCREEN");


}
