package org.betterx.wover.events.api.types.client;

import org.betterx.wover.events.api.Subscriber;
import org.betterx.wover.util.function.VoidFunction;

import net.minecraft.world.level.storage.LevelStorageSource;

/**
 * Used for subscribers of the
 * {@link org.betterx.wover.events.api.client.ClientWorldLifecycle#BEFORE_CLIENT_LOAD_SCREEN}
 * event.
 */
@FunctionalInterface
public interface BeforeClientLoadScreen extends Subscriber {
    /**
     * Called when the event is emitted.
     * <p>
     * Subscribers can request to cancle the continuation of the clients loading
     * process by returning {@code true}. If one subscriber cancels the loading
     * process, the loading screen will not be shown and no other subscribers will
     * be called.
     * It is the responsibility of the subscriber to call the {@code callLoadScreen}
     * function to continue the loading process.
     *
     * @param levelSource    the storage source that can be used to open the world folder.
     * @param levelID        the id of the level that is being loaded.
     * @param callLoadScreen a function that can be called to continue the loading process. This method should only
     *                       get called, when the subscriber cancels the loading process.
     * @return {@code true} if the subscriber cancels the loading process.
     */
    boolean process(LevelStorageSource levelSource, String levelID, VoidFunction callLoadScreen);
}
