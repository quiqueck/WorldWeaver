package org.betterx.wover.events.api.types;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.events.api.Subscriber;

import net.minecraft.nbt.CompoundTag;

/**
 * Used for subscribers of the
 * {@link org.betterx.wover.state.api.WorldConfig#event(ModCore)}
 * event.
 */
@FunctionalInterface
public interface OnWorldConfig extends Subscriber {
    enum State {
        /**
         * The WorldConfig is being created.
         */
        CREATED,
        /**
         * The worldConfig is being loaded.
         */
        LOADED,
        /**
         * Something unexpected happened while loading the worldConfig.
         */
        LOAD_FAILED

    }

    /**
     * Called when the event is emitted.
     *
     * @param modCore the mod core for which the config is being loaded.
     * @param root    the root tag of the config.
     * @param state   the state of the config.
     */
    void config(ModCore modCore, CompoundTag root, State state);
}
