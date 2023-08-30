package org.betterx.wover.events.api.types.client;

import org.betterx.wover.events.api.Subscriber;

@FunctionalInterface
public interface AfterWelcomeScreen extends Subscriber {
    void didPresent();
}
