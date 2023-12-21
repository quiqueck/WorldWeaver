package org.betterx.wover.events.api.types.client;

import org.betterx.wover.events.api.Subscriber;

import net.minecraft.world.level.storage.LevelStorageSource;

/**
 * Used for subscribers of the
 * {@link org.betterx.wover.events.api.client.ClientWorldLifecycle#BEFORE_CLIENT_LOAD_SCREEN}
 * event.
 */
@FunctionalInterface
public interface BeforeClientLoadScreen extends Subscriber {
    /**
     * Used to continue the loading process with the next registered step.
     */
    interface ContinueWith {
        /**
         * Will immediately continue the loading process with the next registered step.
         */
        void loadingScreen();
    }

    /**
     * Called when the event is emitted.
     * <p>
     * It is the responsibility of the subscriber to <b>call the {@code continueWith}
     * function</b> to continue the loading process. For example, if your processing method shows a UI, you need to
     * call {@code continueWith} when the user closes the UI. If you just need to do some synchronous processing, you
     * can call {@code continueWith} immediately when you are finished.
     *
     * @param levelAccess  the access object for the world folder.
     * @param continueWith Your implementation needs to call this function at some point. Otherwise, loading will not
     *                     continue.
     */
    void process(LevelStorageSource.LevelStorageAccess levelAccess, ContinueWith continueWith);
}
