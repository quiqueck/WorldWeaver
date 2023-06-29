package org.betterx.wover.client.events.api;

import org.betterx.wover.client.events.api.types.BeforeClientLoadScreen;
import org.betterx.wover.client.events.impl.ClientWorldLifecycleImpl;
import org.betterx.wover.events.api.Event;

public class ClientWorldLifecycle {
    public static Event<BeforeClientLoadScreen> BEFORE_CLIENT_LOAD_SCREEN = ClientWorldLifecycleImpl.BEFORE_CLIENT_LOAD_SCREEN;
}
