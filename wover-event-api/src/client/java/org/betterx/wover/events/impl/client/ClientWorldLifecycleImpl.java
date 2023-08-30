package org.betterx.wover.events.impl.client;

import org.betterx.wover.events.api.types.client.AfterWelcomeScreen;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.events.impl.types.client.LoadScreenEventImpl;

public class ClientWorldLifecycleImpl {
    public static LoadScreenEventImpl BEFORE_CLIENT_LOAD_SCREEN = new LoadScreenEventImpl("BEFORE_CLIENT_LOAD_SCREEN");
    public static EventImpl<AfterWelcomeScreen> AFTER_WELCOME_SCREEN = new EventImpl<>("AFTER_WELCOME_SCREEN");
}
