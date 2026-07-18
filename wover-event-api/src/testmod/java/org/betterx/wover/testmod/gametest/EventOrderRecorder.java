package org.betterx.wover.testmod.gametest;

import org.betterx.wover.events.api.WorldLifecycle;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Records the actual firing order of every {@link WorldLifecycle} event during server startup so that
 * {@link EventOrderGameTest} can assert it once the server is fully up.
 * <p>
 * {@link WorldLifecycle} events fire while the world is being loaded/created - which happens strictly
 * <em>before</em> any {@code @GameTest} runs. A GameTest therefore cannot observe them as they happen;
 * instead {@link #install()} is called from the testmod's {@code onInitialize} (mod-init, before world
 * load), subscribes a lightweight recorder to each event, and stores the sequence. The GameTest then
 * reads it back and validates the documented contract.
 */
public final class EventOrderRecorder {
    /** Every recorded event name, in the exact order the events fired. */
    private static final List<String> ORDER = new CopyOnWriteArrayList<>();

    /**
     * Firing order of the three {@link WorldLifecycle#WORLD_FOLDER_READY} recorder subscribers, labelled
     * by their priority. Used to prove subscriber-priority ordering (highest priority first). Because
     * {@code WORLD_FOLDER_READY} may fire more than once, this list can contain repeats of the 3-element
     * cycle; the test only inspects the first cycle.
     */
    private static final List<String> FOLDER_PRIORITY_ORDER = new CopyOnWriteArrayList<>();

    private static boolean installed = false;

    private EventOrderRecorder() {
    }

    /**
     * Subscribes the recorders. Idempotent - safe to call more than once, only the first call wires up.
     */
    public static synchronized void install() {
        if (installed) return;
        installed = true;

        // Three subscribers on the same event at distinct priorities. The event fires them
        // highest-priority-first, so the recorded labels must come out 2000, 1000 (default), 10.
        // Only the highest-priority one also records the event into ORDER (avoids duplicating it).
        WorldLifecycle.WORLD_FOLDER_READY.subscribe(access -> {
            ORDER.add("WORLD_FOLDER_READY");
            FOLDER_PRIORITY_ORDER.add("2000");
        }, 2000);
        WorldLifecycle.WORLD_FOLDER_READY.subscribe(access -> FOLDER_PRIORITY_ORDER.add("1000"));
        WorldLifecycle.WORLD_FOLDER_READY.subscribe(access -> FOLDER_PRIORITY_ORDER.add("10"), 10);

        WorldLifecycle.WORLD_REGISTRY_READY.subscribe((registry, stage) ->
                ORDER.add("WORLD_REGISTRY_READY:" + stage));
        WorldLifecycle.CREATED_NEW_WORLD_FOLDER.subscribe((storage, registries, preset, dimensions, recreated) ->
                ORDER.add("CREATED_NEW_WORLD_FOLDER"));
        WorldLifecycle.BEFORE_LOADING_RESOURCES.subscribe((resourceManager, featureFlags) ->
                ORDER.add("BEFORE_LOADING_RESOURCES"));
        WorldLifecycle.RESOURCES_LOADED.subscribe(resourceManager ->
                ORDER.add("RESOURCES_LOADED"));
        WorldLifecycle.ON_DIMENSION_LOAD.subscribe(input -> {
            ORDER.add("ON_DIMENSION_LOAD");
            return input;
        });
        WorldLifecycle.MINECRAFT_SERVER_READY.subscribe((storageSource, packRepository, worldStem) ->
                ORDER.add("MINECRAFT_SERVER_READY"));
        WorldLifecycle.BEFORE_CREATING_LEVELS.subscribe((storage, packRepository, registries, worldData) ->
                ORDER.add("BEFORE_CREATING_LEVELS"));
        WorldLifecycle.SERVER_LEVEL_READY.subscribe((serverLevel, levelKey, levelStem, seed) ->
                ORDER.add("SERVER_LEVEL_READY"));
    }

    /** The recorded firing order (read-only snapshot). */
    public static List<String> order() {
        return Collections.unmodifiableList(ORDER);
    }

    /** The recorded {@code WORLD_FOLDER_READY} priority firing order (read-only snapshot). */
    public static List<String> folderPriorityOrder() {
        return Collections.unmodifiableList(FOLDER_PRIORITY_ORDER);
    }
}
