package org.betterx.wover.tag.impl;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.tag.api.TagRegistry;
import org.betterx.wover.tag.api.event.OnBoostrapTags;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jetbrains.annotations.Nullable;

public abstract class TagRegistryImpl<T, P extends TagBootstrapContext<T>> implements TagRegistry<T, P> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    final ConcurrentLinkedQueue<TagKey<T>> tags = new ConcurrentLinkedQueue<>();
    final @Nullable LocationProvider<T> locationProvider;
    public final String directory;

    private final EventImpl<OnBoostrapTags<T, P>> BOOTSTRAP_EVENT;

    protected TagRegistryImpl(
            ResourceKey<? extends Registry<T>> registryKey,
            String directory,
            LocationProvider<T> locationProvider
    ) {
        this.registryKey = registryKey;
        this.directory = directory;
        this.locationProvider = locationProvider;

        BOOTSTRAP_EVENT = new EventImpl<>("TAG_BOOTSTRAP_EVENT (" + directory + ")");
    }

    public TagKey<T> makeCommonTag(String name) {
        return makeTag(new ResourceLocation("c", name));
    }

    public TagKey<T> makeFabricTag(String name) {
        return makeTag(new ResourceLocation("fabric", name));
    }

    public TagKey<T> makeTag(ModCore mod, String name) {
        return makeTag(mod.id(name));
    }

    public TagKey<T> makeTag(ResourceLocation id) {
        final TagKey<T> tag = TagKey.create(registryKey, id);
        initializeTag(tag);
        return tag;
    }

    protected void initializeTag(TagKey<T> tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public abstract P createBootstrapContext(boolean initAll);

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (var entry : tags) {
            b.append("  - ").append(entry).append(" \n");
        }
        return "TagRegistry{" +
                "registryKey=" + registryKey +
                ", directory='" + directory + '\'' +
                ", tagElements=\n" + b.toString() +
                '}';
    }

    public ResourceKey<? extends Registry<T>> registryKey() {
        return registryKey;
    }

    public Event<OnBoostrapTags<T, P>> bootstrapEvent() {
        return BOOTSTRAP_EVENT;
    }

    P emitLoadEvent(boolean initAll) {
        P ctx = createBootstrapContext(initAll);
        if (!ModCore.isDatagen()) {
            BOOTSTRAP_EVENT.emit(c -> c.bootstrap(ctx));
        }
        return ctx;
    }

    public static abstract class WithRegistry<T, P extends TagBootstrapContext<T>> extends TagRegistryImpl<T, P> {
        private final DefaultedRegistry<T> registry;

        public WithRegistry(DefaultedRegistry<T> registry) {
            super(
                    registry.key(),
                    TagManager.getTagDir(registry.key()),
                    (T element) -> {
                        ResourceLocation id = registry.getKey(element);
                        if (id != registry.getDefaultKey()) {
                            return id;
                        }
                        return null;
                    }
            );
            this.registry = registry;
        }

        @Override
        public TagKey<T> makeTag(ResourceLocation id) {
            final TagKey<T> tag = registry
                    .getTagNames()
                    .filter(tagKey -> tagKey.location().equals(id))
                    .findAny()
                    .orElse(TagKey.create(registry.key(), id));
            initializeTag(tag);
            return tag;
        }
    }
}
