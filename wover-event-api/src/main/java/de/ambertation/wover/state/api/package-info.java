/**
 * Classes from this package are used to capture the state of a loaded world.
 * <p>
 * {@link de.ambertation.wover.state.api.WorldState} exposes read-only access to the registries and storage of the
 * currently loaded world (fed by the events in {@link de.ambertation.wover.events.api.WorldLifecycle}).
 * {@link de.ambertation.wover.state.api.WorldConfig} lets mods persist their own NBT data inside a world's save folder,
 * and {@link de.ambertation.wover.state.api.WorldDatapackConfig} lets mods register JSON config resources that are
 * populated from datapacks.
 */
package de.ambertation.wover.state.api;