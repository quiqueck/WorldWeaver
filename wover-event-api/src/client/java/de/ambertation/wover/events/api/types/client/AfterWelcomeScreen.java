package de.ambertation.wover.events.api.types.client;

import de.ambertation.wover.events.api.Subscriber;

@FunctionalInterface
public interface AfterWelcomeScreen extends Subscriber {
    void didPresent();
}
