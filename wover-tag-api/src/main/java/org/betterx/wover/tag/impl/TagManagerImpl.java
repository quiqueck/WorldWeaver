package org.betterx.wover.tag.impl;

import org.betterx.wover.entrypoint.WoverTag;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.tag.api.TagRegistry;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

public class TagManagerImpl {
    private static final Map<String, TagRegistryImpl<?, ?>> TYPES = Maps.newHashMap();

    public static TagRegistryImpl<Block, TagBootstrapContext<Block>> BLOCKS = registerType(BuiltInRegistries.BLOCK);
    public static TagRegistryImpl<Item, ItemTagBootstrapContext> ITEMS = registerItem();
    public static BiomeTagRegistryImpl BIOMES = registerBiome();

    public static <T, P extends TagBootstrapContext<T>> TagRegistryImpl<T, P> registerType(DefaultedRegistry<T> registry) {
        TagRegistrySimple<T> type = new TagRegistrySimple<>(registry);
        return (TagRegistryImpl<T, P>) TYPES.computeIfAbsent(type.directory, (dir) -> type);
    }

    public static <T, P extends TagBootstrapContext<T>> TagRegistryImpl<T, P> registerType(
            Registry<T> registry,
            String directory
    ) {
        return registerType(registry.key(), directory, (o) -> registry.getKey(o));
    }

    public static <T, P extends TagBootstrapContext<T>> TagRegistryImpl<T, P> registerType(
            ResourceKey<? extends Registry<T>> registry,
            String directory,
            TagRegistry.LocationProvider<T> locationProvider
    ) {
        return (TagRegistryImpl<T, P>) TYPES.computeIfAbsent(
                directory,
                (dir) -> new TagRegistryImpl<>(
                        registry,
                        dir,
                        locationProvider
                ) {
                    @Override
                    public TagBootstrapContext<T> createBootstrapContext(boolean initAll) {
                        return new TagBootstrapContextImpl<>(this, initAll);
                    }
                }
        );
    }

    public static <T, P extends TagBootstrapContext<T>> TagRegistryImpl<T, P> registerType(
            ResourceKey<? extends Registry<T>> registryKey
    ) {
        return registerType(
                registryKey,
                TagManager.getTagDir(registryKey),
                (preset) -> WorldState.registryAccess() != null
                        ? WorldState.registryAccess()
                                    .registryOrThrow(registryKey)
                                    .getKey(preset)
                        : null
        );
    }

    public static TagRegistryImpl<Item, ItemTagBootstrapContext> registerItem() {
        ItemTagRegistryImpl type = new ItemTagRegistryImpl();
        return (TagRegistryImpl<Item, ItemTagBootstrapContext>) TYPES.computeIfAbsent(type.directory, (dir) -> type);
    }

    static BiomeTagRegistryImpl registerBiome() {
        return (BiomeTagRegistryImpl) TYPES.computeIfAbsent(
                "tags/worldgen/biome",
                (dir) -> new BiomeTagRegistryImpl(
                        dir,
                        b -> WorldState.getBiomeID(b)
                )
        );
    }

    @ApiStatus.Internal
    public static Map<ResourceLocation, List<TagLoader.EntryWithSource>> didLoadTagMap(
            String directory,
            Map<ResourceLocation, List<TagLoader.EntryWithSource>> tagsMap
    ) {
        TagRegistryImpl<?, ?> type = TYPES.get(directory);
        if (type != null) {
            TagBootstrapContext<?> provider = type.emitLoadEvent(false);

            provider.forEach((tag, entries) -> {
                List<TagLoader.EntryWithSource> builder = tagsMap.computeIfAbsent(
                        tag.location(),
                        key -> Lists.newArrayList()
                );

                entries.forEach(wrapper -> {
                    builder.add(new TagLoader.EntryWithSource(wrapper.createTagEntry(), WoverTag.C.namespace));
                });
            });
        }

        return tagsMap;
    }
}
