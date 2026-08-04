package de.ambertation.wover.tag.api;

import de.ambertation.wover.state.api.WorldState;
import de.ambertation.wover.tag.api.event.context.ItemTagBootstrapContext;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;
import de.ambertation.wover.tag.impl.TagManagerImpl;
import de.ambertation.wover.tag.impl.TagRegistryImpl;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
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
     * The registry for enchantment tags.
     */
    public static TagRegistryImpl<Enchantment, TagBootstrapContext<Enchantment>> ENCHANTMENTS = TagManagerImpl.ENCHANTMENTS;

    /**
     * The registry for entity types
     */
    public static TagRegistryImpl<EntityType<?>, TagBootstrapContext<EntityType<?>>> ENTITY_TYPES = TagManagerImpl.ENTITY_TYPES;

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
     * {@link net.minecraft.core.registries.Registries#tagsDirPath(ResourceKey)}, while {@code locationProvider}
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

    /**
     * Creates a streaming Codec for Tags from the given registry.
     *
     * @param registry The registry to create the Codec for.
     * @param <T>      The type of the Tag elements.
     * @return The created Codec.
     */
    public static <T> StreamCodec<RegistryFriendlyByteBuf, TagKey<T>> streamCodec(ResourceKey<Registry<T>> registry) {
        return TagManagerImpl.streamCodec(registry);
    }

    /**
     * Checks if the given {@link ItemStack} is a tool with the given mineable tag.
     *
     * @param stack The ItemStack to check.
     * @param tag   The tag to check for.
     * @return {@code true} if the ItemStack is a tool with the given mineable tag, {@code false} otherwise.
     */
    public static boolean isToolWithMineableTag(ItemStack stack, TagKey<Block> tag) {
        final var item = stack.getItem();
        var tool = item.components().get(DataComponents.TOOL);
        if (tool != null) {

            for (var rule : tool.rules()) {
                if (
                        rule.correctForDrops().orElse(false)
                                && rule.blocks().unwrapKey().map(key -> key == tag).orElse(false)
                ) {
                    return true;
                }
            }

        }
        return false;
    }
}
