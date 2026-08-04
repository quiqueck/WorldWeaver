/**
 * Ready-to-use {@link de.ambertation.wover.datagen.api.WoverRegistryContentProvider} implementations for the
 * vanilla world-generation registries (Biomes, Configured/Placed Features, Structures, Structure Sets,
 * Structure Pools and Structure Processor Lists), automatic block tag generation for everything
 * registered via {@code BlockRegistry}, plus base classes for client-side model datagen.
 * <p>
 * Extend one of these classes instead of {@link de.ambertation.wover.datagen.api.WoverRegistryContentProvider}
 * directly when you only need to bootstrap and serialize elements for a single, well-known registry.
 *
 * @see de.ambertation.wover.datagen.api.WoverRegistryContentProvider
 */
package de.ambertation.wover.datagen.api.provider;
