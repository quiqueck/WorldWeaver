package org.betterx.wover.tag.impl;

import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.DefaultedRegistry;


public class TagRegistrySimple<T> extends TagRegistryImpl.WithRegistry<T, TagBootstrapContext<T>> {
    public TagRegistrySimple(DefaultedRegistry<T> registry) {
        super(registry);
    }

    @Override
    public TagBootstrapContext<T> createBootstrapContext(boolean initAll) {
        return TagBootstrapContextImpl.create(this, initAll);
    }
}

