package org.betterx.wover.tag.api;

import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.impl.TagManagerImpl;
import org.betterx.wover.tag.impl.TagRegistryImpl;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * The main entry point for the tag API.
 * <p>
 * This class contains access to some frequently used tag registriesm as well as
 * methods to register new tag types.
 */
public class TagManager {
    /**
     * The registry for block tags.
     */
    public static TagRegistry<Block, TagBootstrapContext<Block>> BLOCKS = TagManagerImpl.BLOCKS;

    /**
     * The registry for item tags.
     */
    public static TagRegistry<Item, ItemTagBootstrapContext> ITEMS = TagManagerImpl.ITEMS;

    /**
     * The registry for biome tags.
     */
    public static BiomeTagRegistry BIOMES = TagManagerImpl.BIOMES;

    /**
     * Creates a new {@link TagRegistry} for the given registry.
     *
     * @param registry The registry to create the {@link TagRegistry} for.
     * @param <T>      The type of the Tag elements.
     * @param <P>      The type of the {@link TagBootstrapContext}.
     * @return The created {@link TagRegistry}.
     */
    public static <T, P extends TagBootstrapContext<T>> TagRegistry<T, P> registerType(DefaultedRegistry<T> registry) {
        return TagManagerImpl.registerType(registry);
    }


    /**
     * Creates a new {@link TagRegistry} for the given registry in the specified directory.
     *
     * @param registry  The registry to create the {@link TagRegistry} for.
     * @param directory The directory to load the tags from.
     * @param <T>       The type of the Tag elements.
     * @param <P>       The type of the {@link TagBootstrapContext}.
     * @return The created {@link TagRegistry}.
     */
    public static <T, P extends TagBootstrapContext<T>> TagRegistry<T, P> registerType(
            Registry<T> registry,
            String directory
    ) {
        return TagManagerImpl.registerType(registry, directory);
    }

    /**
     * Creates a new {@link TagRegistry} for the given registry in the specified directory.
     *
     * @param registry         The registry to create the {@link TagRegistry} for.
     * @param directory        The directory to load the tags from.
     * @param locationProvider The {@link TagRegistry.LocationProvider} to use.
     * @param <T>              The type of the Tag elements.
     * @param <P>              The type of the {@link TagBootstrapContext}.
     * @return The created {@link TagRegistry}.
     */
    public static <T, P extends TagBootstrapContext<T>> TagRegistry<T, P> registerType(
            ResourceKey<? extends Registry<T>> registry,
            String directory,
            TagRegistry.LocationProvider<T> locationProvider
    ) {
        return TagManagerImpl.registerType(registry, directory, locationProvider);
    }

    /**
     * Creates a new {@link TagRegistry} for the given registry.
     * <p>
     * This method is just a convenice method for
     * {@link #registerType(ResourceKey, String, TagRegistry.LocationProvider)}. The
     * {@code directory} is built using Minecrafts
     * {@link net.minecraft.tags.TagManager#getTagDir(ResourceKey)}, while {@code locationProvider}
     * will lookup the Registry using {@link WorldState#registryAccess()} and determin the
     * {@link net.minecraft.resources.ResourceLocation} using {@link Registry#getKey(Object)}.
     *
     * @param registry The registry to create the {@link TagRegistry} for.
     * @return The created {@link TagRegistry}.
     */
    public static <T, P extends TagBootstrapContext<T>> TagRegistryImpl<T, P> registerType(
            ResourceKey<? extends Registry<T>> registry
    ) {
        return TagManagerImpl.registerType(registry);
    }
}
