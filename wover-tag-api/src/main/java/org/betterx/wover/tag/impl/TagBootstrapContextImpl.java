package org.betterx.wover.tag.impl;

import org.betterx.wover.entrypoint.LibWoverTag;
import org.betterx.wover.tag.api.TagRegistry;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagElementWrapper;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagBootstrapContextImpl<T, P extends TagBootstrapContext<T>> implements TagBootstrapContext<T> {
    private final Map<TagKey<T>, TagSet<T>> tags = new ConcurrentHashMap<>();
    private final @Nullable TagRegistryImpl<T, P> tagRegistry;

    protected TagBootstrapContextImpl(@Nullable TagRegistryImpl<T, P> tagRegistry) {
        this.tagRegistry = tagRegistry;
    }

    private void clearAll() {
        if (tagRegistry == null) return;
        for (var tag : tagRegistry.tags) {
            initializeTag(tag);
        }
    }

    protected static final ConcurrentHashMap<TagRegistry, TagBootstrapContextImpl> CACHE = new ConcurrentHashMap<>();

    public static void invalidateCaches() {
        LibWoverTag.C.log.debug("Invalidating TagBootstrapContext Caches");
        CACHE.clear();
    }

    protected static <T, P extends TagBootstrapContext<T>, R extends TagRegistryImpl<T, P>> TagBootstrapContextImpl<T, P> create(
            @NotNull R tagRegistry,
            boolean initAll,
            Function<R, TagBootstrapContextImpl<T, P>> factory
    ) {
        final TagBootstrapContextImpl<?, ?> registry
                = CACHE.computeIfAbsent(tagRegistry, (r) -> factory.apply(tagRegistry));
        if (initAll) registry.clearAll();
        return (TagBootstrapContextImpl<T, P>) registry;
    }

    static <T, P extends TagBootstrapContext<T>> TagBootstrapContextImpl<T, P> create(
            @NotNull TagRegistryImpl<T, P> tagRegistry,
            boolean initAll
    ) {
        return create(tagRegistry, initAll, TagBootstrapContextImpl::new);
    }

    protected void initializeTag(TagKey<T> tag) {
        //just for the side effect of creating an entry for the element
        //that way empty tagElements will not get lost
        getSetForTag(tag);
    }

    public TagSet<T> getSetForTag(TagKey<T> tag) {
        if (tag == null) {
            LibWoverTag.C.log.verboseWarning("Tag should not be null!");
            return new TagSet<>();
        }
        return tags.computeIfAbsent(tag, k -> new TagSet<>());
    }

    public void add(TagKey<T> tagID, T... elements) {
        add(tagID, false, elements);
    }

    public void addOptional(TagKey<T> tagID, T... elements) {
        add(tagID, true, elements);
    }

    protected void add(TagKey<T> tagID, boolean optional, T... elements) {
        TagSet<T> set = getSetForTag(tagID);
        for (T element : elements) {
            final ResourceLocation id = tagRegistry.locationProvider.get(element);
            if (id != null) {
                final TagElementWrapperImpl<T> wrapper = new TagElementWrapperImpl<>(id, false, !optional);
                set.add(wrapper);
            }
        }
    }

    public void add(T element, TagKey<T>... tags) {
        for (TagKey<T> tagID : tags) {
            add(tagID, false, element);
        }
    }

    public void addOptional(T element, TagKey<T>... tags) {
        for (TagKey<T> tagID : tags) {
            add(tagID, true, element);
        }
    }

    public void add(TagKey<T> tagID, TagKey<T>... tags) {
        add(tagID, false, tags);
    }

    public void addOptional(TagKey<T> tagID, TagKey<T>... tags) {
        add(tagID, true, tags);
    }

    protected void add(TagKey<T> tagID, boolean optional, TagKey<T>... tagElements) {
        final TagSet<T> set = getSetForTag(tagID);
        for (TagKey<T> element : tagElements) {
            final ResourceLocation id = element.location();
            if (id != null) {
                final TagElementWrapperImpl<T> wrapper = new TagElementWrapperImpl<>(id, true, !optional);
                set.add(wrapper);
            }
        }
    }

    /**
     * Adds one Tag to multiple Elements.
     *
     * @param tagID    {@link TagKey} tag ID.
     * @param elements array of Elements to add into tag.
     */
    public void add(TagKey<T> tagID, ResourceKey<T>... elements) {
        add(tagID, false, elements);
    }

    public void addOptional(TagKey<T> tagID, ResourceKey<T>... elements) {
        add(tagID, true, elements);
    }

    void add(TagKey<T> tagID, boolean optional, ResourceKey<T>... elements) {
        synchronized (this) {
            final TagSet<T> set = getSetForTag(tagID);
            for (ResourceKey<T> element : elements) {
                final ResourceLocation id = element.location();

                if (id != null) {
                    final TagElementWrapperImpl<T> wrapper = new TagElementWrapperImpl<>(id, false, !optional);
                    set.add(wrapper);
                }
            }
        }
    }


    public boolean contains(TagKey<T> tagID, T element) {
        final TagSet<T> set = getSetForTag(tagID);
        final ResourceLocation id = tagRegistry.locationProvider.get(element);
        if (id != null) {
            for (var entry : set)
                if (!entry.tag()) {
                    if (id.equals(entry.id()))
                        return true;
                }
        }
        return false;
    }

    public void forEach(BiConsumer<TagKey<T>, List<TagElementWrapper<T>>> consumer) {
        for (var entry : tags.entrySet()) {
            consumer.accept(
                    entry.getKey(),
                    entry.getValue()
                         .stream()
                         .sorted(Comparator.comparing(TagElementWrapper::id))
                         .toList()
            );
        }
    }

    @Override
    public TagRegistry<T, P> registry() {
        return tagRegistry;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (var entry : tags.entrySet()) {
            b.append("  - ").append(entry.getKey()).append(": \n");
            for (var element : entry.getValue()) {
                b.append("    - ").append(element).append("\n");
            }
        }
        return "TagElementProviderImpl{" +
                "tagRegistry=" + tagRegistry +
                ", tags=\n" + b.toString() +
                '}';
    }
}
