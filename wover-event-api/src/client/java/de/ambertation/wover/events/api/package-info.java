/**
 * Some useful lifecycle events for the world creation/loading process.
 *
 * <p>
 * You can find common events in {@link de.ambertation.wover.events.api.WorldLifecycle}
 * All client-side events can be found in {@link de.ambertation.wover.events.api.client.ClientWorldLifecycle}
 *
 * <p>
 * <b>Example: </b>
 * Subscribe to the {@link de.ambertation.wover.events.api.WorldLifecycle#WORLD_FOLDER_READY} event
 * <code>
 * WorldLifecycle.WORLD_FOLDER_READY.subscribe(storage->System.out.println("World folder ready: " + storage));
 * </code>
 */
package de.ambertation.wover.events.api;