package org.betterx.wover.datagen.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.tag.api.TagRegistry;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagElementWrapper;
import org.betterx.wover.tag.impl.TagManagerImpl;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
    protected final List<String> modIDs;
    /**
     * The {@link ModCore} instance to use
     */
    protected final ModCore modCore;

    /**
     * The {@link TagRegistry} that will provide the tags.
     */
    protected final TagRegistry<T, P> tagRegistry;

    /**
     * Tags that should be written even if they are empty.
     */
    private final Set<TagKey<T>> forceWrite;

    /**
     * Creates a new Instance that will not force the writing of any tag, and will
     * allow all namespaces.
     *
     * @param tagRegistry the {@link TagRegistry} that will provide the tags
     */
    public WoverTagProvider(
            ModCore modCore,
            TagRegistry<T, P> tagRegistry
    ) {
        this(modCore, tagRegistry, List.of(modCore.namespace, modCore.modId), Set.of());
    }

    /**
     * Creates a new Instance that will not force the writing of any tag.
     *
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
    protected abstract void prepareTags(P context);

    /**
     * When {@code true} all tags will be initialized with an empty element list
     * before {@link #prepareTags(TagBootstrapContext)} is called.
     * <p>
     * Only Tags that are initialized will be written to disk. Tags are automatically
     * initialized when elements are added to them. If this method returns {@code true}
     * from the {@link TagRegistry} will be initialized.
     * <p>
     * The default implementation returns {@code false}.
     *
     * @return {@code true} if all tags should be initialized
     */
    protected boolean initAll() {
        return false;
    }


    @Override
    public FabricTagProvider<T> getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        String baseName = this.getClass().getSimpleName();
        return new FabricTagProvider<T>(output, tagRegistry.registryKey(), registriesFuture) {
            @Override
            public String getName() {
                return baseName + " (" + super.getName() + ")";
            }

            @Override
            /**
             * Adds all tags to the {@link HolderLookup.Provider}.
             *
             * @param arg the {@link HolderLookup.Provider} to add the tags to
             */
            protected final void addTags(HolderLookup.Provider arg) {
                P provider = tagRegistry.createBootstrapContext(initAll());
                prepareTags(provider);

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

                    if (!force && elements.isEmpty()) return;
                    final FabricTagProvider<T>.FabricTagBuilder builder = getOrCreateTagBuilder(tag);

                    //write all elements that passed the above filtering...
                    for (var element : elements) {
                        if (element.tag()) {
                            if (element.required()) builder.forceAddTag(TagKey.create(registryKey, element.id()));
                            else builder.addOptionalTag(element.id());
                        } else {
                            if (element.required()) builder.add(element.id());
                            else builder.addOptional(element.id());
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
         */
        public ForBlocks(ModCore modCore) {
            super(modCore, TagManagerImpl.BLOCKS);
        }

        /**
         * Creates a new Instance that will not force the writing of any empty tag.
         *
         * @param modIDs List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *               mod will be written to the tag. If null all elements get written, an empty list will
         *               write nothing
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
         * @param modIDs List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *               mod will be written to the tag. If null all elements get written, an empty list will
         *               write nothing
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
         */
        public ForBiomes(ModCore modCore) {
            super(modCore, TagManagerImpl.BIOMES);
        }

        /**
         * Creates a new Instance that will not force the writing of any empty tag.
         *
         * @param modIDs List of ModIDs that are allowed to include data. All Resources in the namespace of the
         *               mod will be written to the tag. If null all elements get written, an empty list will
         *               write nothing
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
}
