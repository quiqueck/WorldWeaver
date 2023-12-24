package org.betterx.wover.events.api.client;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.client.AfterWelcomeScreen;
import org.betterx.wover.events.api.types.client.BeforeClientLoadScreen;
import org.betterx.wover.events.api.types.client.ShowExperimentalWarningScreen;
import org.betterx.wover.events.api.types.client.StartupScreenProvider;
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

    /**
     * This event is called, when Minecraft builds the list of Startup Screens.
     * <p>
     * Your subscriber will receive a {@link Runnable}-Object that needs to be called
     * once the returned Screen was closed. The <code>LayoutScreen</code> classes
     * are already set up accordingly. For your own Screen implementation you would
     * have to ensure the correct behaviour.
     * <p>
     * Assume you have the following class:
     * <pre class="java">public class HelloScreen extends WoverLayoutScreen {
     *      public WoverLayoutScreen(@NotNull Runnable onClose){
     *          super(onClose, translatable("screen.welcome.title"))
     *      }
     * }
     * </pre>
     * You can register the Screen using <code>ClientWorldLifecycle.ENUMERATE_STARTUP_SCREENS.subscribe(HelloScreen::new);</code>
     * <p>
     * All custom screens are displayed <b>after</b> the vanilla screens.
     */
    public static Event<StartupScreenProvider> ENUMERATE_STARTUP_SCREENS = ClientWorldLifecycleImpl.ENUMERATE_STARTUP_SCREENS;

    public static Event<AfterWelcomeScreen> AFTER_WELCOME_SCREEN = ClientWorldLifecycleImpl.AFTER_WELCOME_SCREEN;
}
