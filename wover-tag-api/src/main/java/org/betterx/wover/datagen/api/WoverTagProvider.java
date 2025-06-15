package org.betterx.wover.datagen.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverTag;
import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.tag.api.TagRegistry;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagElementWrapper;
import org.betterx.wover.tag.impl.TagManagerImpl;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link FabricTagProvider} that writes tags to the data directory.
 * <p>
 * This class does interface with the Tag API. It allows you to
 * bootstrap Tags and serialize them to disk. Only tags that are
 * populated in {@link #prepareTags(TagBootstrapContext)} can be
 * serialized.
 *
 * @param <T> the taggable type
 * @param <P> the bootstrap context type
 */
public abstract class WoverTagProvider<T, P extends TagBootstrapContext<T>> implements WoverDataProvider<FabricTagProvider<T>> {
    /**
     * All allowed namespaces for this tag provider.
     * <p>
     * If null all namespaces are allowed, if empty no namespaces are.Only Tags that
     * are in one of the allowed namespaces or that contain at least one element from the
     * allowed namespaces will be written.
     */
    @Nullable
    protected List<String> modIDs;
    /**
     * The {@link ModCore} instance to use
     */
    public final ModCore modCore;

    /**
     * The {@link TagRegistry} that will provide the tags.
     */
    public final TagRegistry<T, P> tagRegistry;

    /**
     * Tags that should be written even if they are empty.
     */
    private Set<TagKey<T>> forceWrite;

    private static @NotNull List<String> defaultModIDList(ModCore modCore) {
        return List.of(modCore.namespace, modCore.modId);
    }

    /**
     * Creates a new Instance that will not force the writing of any tag, and will
     * allow all namespaces.
     *
     * @param modCore     the {@link ModCore} instance to use
     * @param tagRegistry the {@link TagRegistry} that will provide the tags
     */
    public WoverTagProvider(
            ModCore modCore,
            TagRegistry<T, P> tagRegistry
    ) {
        this(modCore, tagRegistry, defaultModIDList(modCore), Set.of());
    }

    /**
     * Creates a new Instance that will not force the writing of any tag, and will
     * allow all namespaces.
     *
     * @param modCore        the {@link ModCore} instance to use
     * @param tagRegistry    the {@link TagRegistry} that will provide the tags
     * @param forceWriteKeys the keys that should always get written
     */
    public WoverTagProvider(
            ModCore modCore,
            TagRegistry<T, P> tagRegistry,
            Set<TagKey<T>> forceWriteKeys
    ) {
        this(modCore, tagRegistry, defaultModIDList(modCore), forceWriteKeys);
    }

    /**
     * Creates a new Instance that will not force the writing of any tag.
     *
     * @param modCore     the {@link ModCore} instance to use
     * @param tagRegistry the {@link TagRegistry} that will provide the tags
     * @param modIDs      List of ModIDs that are allowed to include data. All Resources in the namespace of the
     *                    mod will be written to the tag. If null all elements get written, an empty list will
     *                    write nothing
     */
    public WoverTagProvider(
            ModCore modCore,
            TagRegistry<T, P> tagRegistry,
            @Nullable List<String> modIDs
    ) {
        this(modCore, tagRegistry, modIDs, Set.of());
    }

    /**
     * Creates a new Instance .
     *
     * @param modCore        the {@link ModCore} instance to use
     * @param tagRegistry    the {@link TagRegistry} that will provide the tags
     * @param modIDs         List of ModIDs that are allowed to inlcude data. All Resources in the namespace of the
     *                       mod will be written to the tag. If null all elements get written, an empty list will
     *                       write nothing
     * @param forceWriteKeys the keys that should always get written
     */
    public WoverTagProvider(
            ModCore modCore,
            TagRegistry<T, P> tagRegistry,
            @Nullable List<String> modIDs,
            Set<TagKey<T>> forceWriteKeys
    ) {
        this.modCore = modCore;
        this.tagRegistry = tagRegistry;
        this.modIDs = modIDs;
        this.forceWrite = forceWriteKeys;
    }

    /**
     * Adds the set of keys from the given {@link WoverTagProvider} to the list of keys that should always be written.
     *
     * @param provider the provider to add the keys from
     */
    public void mergeAllowedAndForced(@Nullable WoverTagProvider<T, ?> provider) {
        if (provider != null) {
            final Set<TagKey<T>> c = new HashSet<>(this.forceWrite);
            c.addAll(provider.forceWrite);

            // Make the set immutable
            this.forceWrite = Collections.unmodifiableSet(c);

            if (provider.modIDs != null) {
                if (this.modIDs == null) {
                    this.modIDs = Collections.unmodifiableList(new LinkedList<>(provider.modIDs));
                } else {
                    final var l = new LinkedList<>(provider.modIDs);
                    l.addAll(this.modIDs);
                    this.modIDs = Collections.unmodifiableList(l);
                }
            }
        }
    }

    /**
     * Tests whether the given {@link ResourceLocation} should be added to the tag.
     * <p>
     * The default implementation will return true if the {@link ResourceLocation}
     * is included in the {@link #modIDs} list, or if the {@link #modIDs} list is
     * {@code null}.
     *
     * @param loc the {@link ResourceLocation} to test
     * @return {@code true} if the {@link ResourceLocation} is allowed
     */
    protected boolean shouldAdd(ResourceLocation loc) {
        return modIDs == null || modIDs.contains(loc.getNamespace());
    }

    /**
     * Called before the tags are written to disk.
     * <p>
     * This method is used to add elements to the tags. The {@link TagBootstrapContext}
     * provides the necessary methods to add elements.
     *
     * @param context the {@link TagBootstrapContext} you can use to add elements to the tags
     */
    public abstract void prepareTags(P context);

    /**
     * When {@code true} all tags will be initialized with an empty element list
     * before {@link #prepareTags(TagBootstrapContext)} is called.
     * <p>
     * Only Tags that are initialized will be written to disk. Tags are automatically
     * initialized when elements are added to them. If this method returns {@code true}
     * all elements from the {@link TagRegistry} will be initialized.
     * <p>
     * The default implementation returns {@code false}.
     *
     * @return {@code true} if all tags should be initialized
     */
    protected boolean initAll() {
        return false;
    }

    protected String getTitle() {
        return this.modCore.namespace + "/" + this.getClass().getSimpleName();
    }

    /**
     * When {@code true} the original tags will be replaced by the new tags.
     *
     * @return {@code true} if the original tags should be replaced
     */
    protected boolean replaceOriginalTags() {
        return false;
    }

    @Override
    public FabricTagProvider<T> getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new FabricTagProvider<T>(output, tagRegistry.registryKey(), registriesFuture) {
            @Override
            public String getName() {
                return getTitle() + " (" + super.getName() + ")";
            }

            @Override
            /**
             * Adds all tags to the {@link HolderLookup.Provider}.
             *
             * @param arg the {@link HolderLookup.Provider} to add the tags to
             */
            protected final void addTags(HolderLookup.Provider arg) {
                P provider = tagRegistry.createBootstrapContext(initAll());

                //make sure that force written Tags are added to the provider
                forceWrite.forEach(provider::asPlaceholder);
                prepareTags(provider);

                LibWoverTag.C.LOG.debug("    ****> Writing tags for {}", modIDs);
                provider.forEach((tag, allElements) -> {
                    boolean force = forceWrite.contains(tag);

                    //if the Tag is from an accepted mod, we will write all elements
                    //otherwise (if another mod manages this tag), then we will only
                    // write the elements that are from an accepted mod
                    final List<TagElementWrapper<T>> elements = shouldAdd(tag.location())
                            ? allElements
                            : allElements.stream()
                                         .filter(element -> shouldAdd(element.id()))
                                         .toList();

                    if (!force && elements.isEmpty()) {
                        return;
                    }

                    TagAppender<ResourceKey<T>, T> builder = this.builder(tag);
                    builder.setReplace(replaceOriginalTags());

                    ResourceKey<T> key;
                    TagKey<T> tagKey;
                    //write all elements that passed the above filtering...
                    for (var element : elements) {
                        if (element.tag()) {
                            tagKey = TagKey.create(tagRegistry.registryKey(), element.id());
                            if (element.required()) builder.forceAddTag(tagKey);
                            else builder.addOptionalTag(tagKey);
                        } else {
                            key = ResourceKey.create(registryKey, element.id());
                            if (element.required()) builder.add(key);
                            else builder.addOptional(key);
                        }
                    }
                });
            }
        };
    }

    /**
     * Convenience class for creating {@link WoverTagProvider} instances for
     * {@link Block} tags.
     */
    public abstract static class ForBlocks extends WoverTagProvider<Block, TagBootstrapContext<Block>> {
        /**
         * Creates a new Instance that includes all namespaces but will not
         * force the writing of any empty tag.
         *
         * @param modCore the {@link ModCore} instance to use
         */
        public ForBlocks(ModCore modCore) {
            super(modCore, TagManagerImpl.BLOCKS);
        }

        /**
         * Creates a new Instance that will not force the writing of any empty tag.
         *
         * @param modCore the {@link ModCore} instance to use
         * @param modIDs  List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *                mod will be written to the tag. If null all elements get written, an empty list will
         *                write nothing
         */
        public ForBlocks(
                ModCore modCore,
                @Nullable List<String> modIDs
        ) {
            super(modCore, TagManagerImpl.BLOCKS, modIDs);
        }

        /**
         * Creates a new Instance.
         *
         * @param modCore        the {@link ModCore} instance to use
         * @param forceWriteKeys the keys that should always get written
         */
        public ForBlocks(
                ModCore modCore,
                Set<TagKey<Block>> forceWriteKeys
        ) {
            super(modCore, TagManagerImpl.BLOCKS, forceWriteKeys);
        }

        /**
         * Creates a new Instance.
         *
         * @param modCore        the {@link ModCore} instance to use
         * @param modIDs         List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *                       mod will be written to the tag. If null all elements get written, an empty list will
         *                       write nothing
         * @param forceWriteKeys the keys that should always get written
         */
        public ForBlocks(
                ModCore modCore,
                @Nullable List<String> modIDs,
                Set<TagKey<Block>> forceWriteKeys
        ) {
            super(modCore, TagManagerImpl.BLOCKS, modIDs, forceWriteKeys);
        }
    }

    /**
     * Convenience class for creating {@link WoverTagProvider} instances for
     * {@link Item} tags.
     */
    public abstract static class ForItems extends WoverTagProvider<Item, ItemTagBootstrapContext> {
        /**
         * Creates a new Instance that includes all namespaces but will not
         * force the writing of any empty tag.
         */
        public ForItems(ModCore modCore) {
            super(modCore, TagManagerImpl.ITEMS);
        }

        /**
         * Creates a new Instance that will not force the writing of any empty tag.
         *
         * @param modCore the {@link ModCore} instance to use
         * @param modIDs  List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *                mod will be written to the tag. If null all elements get written, an empty list will
         *                write nothing
         */
        public ForItems(
                ModCore modCore,
                @Nullable List<String> modIDs
        ) {
            super(modCore, TagManagerImpl.ITEMS, modIDs);
        }

        /**
         * Creates a new Instance.
         *
         * @param modCore        the {@link ModCore} instance to use
         * @param forceWriteKeys the keys that should always get written
         */
        public ForItems(
                ModCore modCore,
                Set<TagKey<Item>> forceWriteKeys
        ) {
            super(modCore, TagManagerImpl.ITEMS, forceWriteKeys);
        }

        /**
         * Creates a new Instance.
         *
         * @param modCore        the {@link ModCore} instance to use
         * @param modIDs         List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *                       mod will be written to the tag. If null all elements get written, an empty list will
         *                       write nothing
         * @param forceWriteKeys the keys that should always get written
         */
        public ForItems(
                ModCore modCore,
                @Nullable List<String> modIDs,
                Set<TagKey<Item>> forceWriteKeys
        ) {
            super(modCore, TagManagerImpl.ITEMS, modIDs, forceWriteKeys);
        }
    }

    /**
     * Convenience class for creating {@link WoverTagProvider} instances for
     * {@link Biome} tags.
     */
    public abstract static class ForBiomes extends WoverTagProvider<Biome, TagBootstrapContext<Biome>> {
        /**
         * Creates a new Instance that includes all namespaces but will not
         * force the writing of any empty tag.
         *
         * @param modCore the {@link ModCore} instance to use
         */
        public ForBiomes(ModCore modCore) {
            super(modCore, TagManagerImpl.BIOMES);
        }

        /**
         * Creates a new Instance that will not force the writing of any empty tag.
         *
         * @param modCore the {@link ModCore} instance to use
         * @param modIDs  List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *                mod will be written to the tag. If null all elements get written, an empty list will
         *                write nothing
         */
        public ForBiomes(
                ModCore modCore,
                @Nullable List<String> modIDs
        ) {
            super(modCore, TagManagerImpl.BIOMES, modIDs);
        }

        /**
         * Creates a new Instance.
         *
         * @param modCore        the {@link ModCore} instance to use
         * @param forceWriteKeys the keys that should always get written
         */
        public ForBiomes(
                ModCore modCore,
                Set<TagKey<Biome>> forceWriteKeys
        ) {
            super(modCore, TagManagerImpl.BIOMES, forceWriteKeys);
        }

        /**
         * Creates a new Instance.
         *
         * @param modCore        the {@link ModCore} instance to use
         * @param modIDs         List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *                       mod will be written to the tag. If null all elements get written, an empty list will
         *                       write nothing
         * @param forceWriteKeys the keys that should always get written
         */
        public ForBiomes(
                ModCore modCore,
                @Nullable List<String> modIDs,
                Set<TagKey<Biome>> forceWriteKeys
        ) {
            super(modCore, TagManagerImpl.BIOMES, modIDs, forceWriteKeys);
        }
    }

    /**
     * Convenience class for creating {@link WoverTagProvider} instances for
     * Enchantement tags.
     */
    public abstract static class ForEnchantments extends WoverTagProvider<Enchantment, TagBootstrapContext<Enchantment>> {
        /**
         * Creates a new Instance that includes all namespaces but will not
         * force the writing of any empty tag.
         *
         * @param modCore the {@link ModCore} instance to use
         */
        public ForEnchantments(ModCore modCore) {
            super(modCore, TagManager.ENCHANTMENTS);
        }

        /**
         * Creates a new Instance that will not force the writing of any empty tag.
         *
         * @param modCore the {@link ModCore} instance to use
         * @param modIDs  List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *                mod will be written to the tag. If null all elements get written, an empty list will
         *                write nothing
         */
        public ForEnchantments(
                ModCore modCore,
                @Nullable List<String> modIDs
        ) {
            super(modCore, TagManager.ENCHANTMENTS, modIDs);
        }

        /**
         * Creates a new Instance.
         *
         * @param modCore        the {@link ModCore} instance to use
         * @param forceWriteKeys the keys that should always get written
         */
        public ForEnchantments(
                ModCore modCore,
                Set<TagKey<Enchantment>> forceWriteKeys
        ) {
            super(modCore, TagManager.ENCHANTMENTS, forceWriteKeys);
        }

        /**
         * Creates a new Instance.
         *
         * @param modCore        the {@link ModCore} instance to use
         * @param modIDs         List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *                       mod will be written to the tag. If null all elements get written, an empty list will
         *                       write nothing
         * @param forceWriteKeys the keys that should always get written
         */
        public ForEnchantments(
                ModCore modCore,
                @Nullable List<String> modIDs,
                Set<TagKey<Enchantment>> forceWriteKeys
        ) {
            super(modCore, TagManager.ENCHANTMENTS, modIDs, forceWriteKeys);
        }
    }

    /**
     * Convenience class for creating {@link WoverTagProvider} instances for
     * EntityType tags.
     */
    public abstract static class ForEntityTypes extends WoverTagProvider<EntityType<?>, TagBootstrapContext<EntityType<?>>> {
        /**
         * Creates a new Instance that includes all namespaces but will not
         * force the writing of any empty tag.
         *
         * @param modCore the {@link ModCore} instance to use
         */
        public ForEntityTypes(ModCore modCore) {
            super(modCore, TagManager.ENTITY_TYPES);
        }

        /**
         * Creates a new Instance that will not force the writing of any empty tag.
         *
         * @param modCore the {@link ModCore} instance to use
         * @param modIDs  List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *                mod will be written to the tag. If null all elements get written, an empty list will
         *                write nothing
         */
        public ForEntityTypes(
                ModCore modCore,
                @Nullable List<String> modIDs
        ) {
            super(modCore, TagManager.ENTITY_TYPES, modIDs);
        }

        /**
         * Creates a new Instance.
         *
         * @param modCore        the {@link ModCore} instance to use
         * @param forceWriteKeys the keys that should always get written
         */
        public ForEntityTypes(
                ModCore modCore,
                Set<TagKey<EntityType<?>>> forceWriteKeys
        ) {
            super(modCore, TagManager.ENTITY_TYPES, forceWriteKeys);
        }

        /**
         * Creates a new Instance.
         *
         * @param modCore        the {@link ModCore} instance to use
         * @param modIDs         List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *                       mod will be written to the tag. If null all elements get written, an empty list will
         *                       write nothing
         * @param forceWriteKeys the keys that should always get written
         */
        public ForEntityTypes(
                ModCore modCore,
                @Nullable List<String> modIDs,
                Set<TagKey<EntityType<?>>> forceWriteKeys
        ) {
            super(modCore, TagManager.ENTITY_TYPES, modIDs, forceWriteKeys);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " for " + modCore + " (" + modIDs + ")";
    }
}
