package org.betterx.wover.events.api.types;

import org.betterx.wover.events.api.Subscriber;

import net.minecraft.core.RegistryAccess;

/**
 * Used for subscribers of the
 * {@link org.betterx.wover.events.api.WorldLifecycle#WORLD_REGISTRY_READY}
 * event.
 */
@FunctionalInterface
public interface OnRegistryReady extends Subscriber {
    /**
     * The game uses multiple registries during game startup. The
     * {@link Stage} enum is used to indicate which stage the
     * registry was used in
     */
    enum Stage {
        /**
         * The preparation stage is encountered when the "Create World"
         * screen is opened, or whenever the game needs to temporarily
         * create a registry.
         *
         * <p>
         * For Example: {@link org.betterx.wover.state.api.WorldState} uses the
         * {@link org.betterx.wover.events.api.WorldLifecycle#WORLD_REGISTRY_READY}
         * to store the currently active registry. However, it will
         * ignore all registry access objects from this stage
         */
        PREPARATION,
        /**
         * The loading stage is encountered when the game loads a registry
         * to prepare the worlds datastructures.
         */
        LOADING,
        /**
         * Registries from the final stage are used by loaded world.
         */
        FINAL
    }

    /**
     * Called when the event is emitted.
     *
     * @param registryAccess the registry access that was used to load
     *                       the registry.
     * @param stage          the stage the registry was used in.
     */
    void ready(RegistryAccess registryAccess, Stage stage);
}
