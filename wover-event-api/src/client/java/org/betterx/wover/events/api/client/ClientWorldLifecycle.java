package org.betterx.wover.events.api.client;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.client.AfterWelcomeScreen;
import org.betterx.wover.events.api.types.client.BeforeClientLoadScreen;
import org.betterx.wover.events.impl.client.ClientWorldLifecycleImpl;

import net.minecraft.world.level.storage.LevelStorageSource;

import java.util.function.Consumer;

/**
 * Provides some useful <b>client</b> lifecycle events for the world creation/loading process.
 */
public class ClientWorldLifecycle {
    /**
     * Called before the client load screen is shown.
     * <p>
     * This is a variat of
     * a {@link org.betterx.wover.events.api.ChainableSubscriber}.
     * All subscribers can request to cancle the continuation of the clients loading
     * process by returning {@code false}. If one subscriber cancels the loading process,
     * the loading screen will not be shown and no other subscribers will be called.
     * <p>
     * It is the responsibility of the subscriber to call the {@code callLoadScreen} function
     * that is passed as a parameter in {@link BeforeClientLoadScreen#process(LevelStorageSource, String, Consumer)}
     */
    public static Event<BeforeClientLoadScreen> BEFORE_CLIENT_LOAD_SCREEN = ClientWorldLifecycleImpl.BEFORE_CLIENT_LOAD_SCREEN;

    public static Event<AfterWelcomeScreen> AFTER_WELCOME_SCREEN = ClientWorldLifecycleImpl.AFTER_WELCOME_SCREEN;
}
