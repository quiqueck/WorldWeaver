package org.betterx.wover.tag.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.tag.api.event.OnBoostrapTags;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import org.jetbrains.annotations.ApiStatus;

/**
 * Tag Registry.
 * <p>
 * Manages all the tags for a given {@link net.minecraft.core.Registry}. A registry
 * is mostly used in the Data Generators to create the tags.
 * <p>
 * However, if you have no choice, you can also use the Registry to create tags at runtime.
 *
 * @param <T> The type of the tag.
 * @param <P> The type of the bootstrap context.
 */
public interface TagRegistry<T, P extends TagBootstrapContext<T>> {
    /**
     * Used to lookup the {@link ResourceLocation} of an element.
     *
     * @param <T> The type of the element.
     */
    @FunctionalInterface
    interface LocationProvider<T> {
        /**
         * Get the {@link ResourceLocation} of an element.
         *
         * @param item The element.
         * @return the {@link ResourceLocation} of the element.
         */
        ResourceLocation get(T item);
    }

    /**
     * The {@link ResourceKey} of the registry that manages the Tags.
     *
     * @return the {@link ResourceKey} of the registry that manages the Tags.
     */
    ResourceKey<? extends Registry<T>> registryKey();

    /**
     * Get or create a common {@link TagKey} (namespace is 'c').
     *
     * @param name - The name of the Tag;
     * @return the corresponding TagKey {@link TagKey<T>}.
     * @see <a href="https://fabricmc.net/wiki/tutorial:tags">Fabric Wiki (Tags)</a>
     */
    TagKey<T> makeCommonTag(String name);

    /**
     * Get or create a fabric {@link TagKey} (namespace is 'fabric').
     *
     * @param name - The name of the Tag;
     * @return the corresponding TagKey {@link TagKey<T>}.
     * @see <a href="https://fabricmc.net/wiki/tutorial:tags">Fabric Wiki (Tags)</a>
     */
    TagKey<T> makeFabricTag(String name);

    /**
     * Get or create a {@link TagKey}.
     *
     * @param mod  - {@link ModCore} mod;
     * @param name - {@link String} tag name.
     * @return the corresponding TagKey {@link TagKey<T>}.
     */
    TagKey<T> makeTag(ModCore mod, String name);

    /**
     * Get or create a {@link TagKey}.
     *
     * @param id - {@link ResourceLocation} of the tag;
     * @return the corresponding TagKey {@link TagKey<T>}.
     */
    TagKey<T> makeTag(ResourceLocation id);

    /**
     * An event that is fired when the tags for this registry are beeing loaded.
     * <p>
     * This event is <b>not</b> fired during DataGen. In general Tags should
     * be created during DataGen whenever possible using {@code WoverTagProvider}.
     * <p>
     * When the event is emitted, the callback Function will recive a
     * {@link TagBootstrapContext} object you can use to add runtime tags.
     *
     * @return the event you can subscribe to
     */
    Event<OnBoostrapTags<T, P>> bootstrapEvent();


    /**
     * Internally used to create the bootstrap context.
     * <p>
     * You should not call this method directly.
     *
     * @param initAll Whether to initialize all the tags with an empty set of elements.
     * @return the bootstrap context.
     */
    @ApiStatus.Internal
    P createBootstrapContext(boolean initAll);
}
