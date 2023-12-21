package org.betterx.wover.events.api.client;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.client.AfterWelcomeScreen;
import org.betterx.wover.events.api.types.client.BeforeClientLoadScreen;
import org.betterx.wover.events.api.types.client.ShowExperimentalWarningScreen;
import org.betterx.wover.events.impl.client.ClientWorldLifecycleImpl;

import net.minecraft.world.level.storage.LevelStorageSource;

/**
 * Provides some useful <b>client</b> lifecycle events for the world creation/loading process.
 */
public class ClientWorldLifecycle {
    /**
     * Called before the client load screen is shown.
     * <p>
     * This is a variat of
     * a {@link org.betterx.wover.events.api.ChainableSubscriber}.
     * All subscribers need to call the {@code continueWith} function that is passed as a parameter in
     * {@link BeforeClientLoadScreen#process(LevelStorageSource.LevelStorageAccess, BeforeClientLoadScreen.ContinueWith)}
     * to continue the loading process.
     * <p>
     * <b>Example:</b> When your implementation needs to show a UI, you need to call {@code continueWith} when the
     * user closes the UI. If you just need to do some synchronous processing, you can call {@code continueWith}
     * immediately when you are finished.
     */
    public static Event<BeforeClientLoadScreen> BEFORE_CLIENT_LOAD_SCREEN = ClientWorldLifecycleImpl.BEFORE_CLIENT_LOAD_SCREEN;

    /**
     * Called when we need to determine weather or not to show the experimental warning screen.
     * <p>
     * This is a {@link org.betterx.wover.events.api.ChainableSubscriber}, where the result of a subscriber is
     * passed to the next subscriber.
     */
    public static Event<ShowExperimentalWarningScreen> ALLOW_EXPERIMENTAL_WARNING_SCREEN = ClientWorldLifecycleImpl.ALLOW_EXPERIMENTAL_WARNING_SCREEN;

    public static Event<AfterWelcomeScreen> AFTER_WELCOME_SCREEN = ClientWorldLifecycleImpl.AFTER_WELCOME_SCREEN;
}
