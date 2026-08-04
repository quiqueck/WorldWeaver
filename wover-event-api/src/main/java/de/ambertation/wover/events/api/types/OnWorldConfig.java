package de.ambertation.wover.events.api.types;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.events.api.Subscriber;

import net.minecraft.nbt.CompoundTag;

/**
 * Used for subscribers of the
 * {@link de.ambertation.wover.state.api.WorldConfig#event(ModCore)}
 * event.
 */
@FunctionalInterface
public interface OnWorldConfig extends Subscriber {
    /**
     * Indicates why the event for a given {@link de.ambertation.wover.core.api.ModCore}'s world config was fired.
     */
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
