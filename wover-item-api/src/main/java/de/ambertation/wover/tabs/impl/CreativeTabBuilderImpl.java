package de.ambertation.wover.tabs.impl;

import de.ambertation.wover.tabs.api.interfaces.CreativeTabBuilder;
import de.ambertation.wover.tabs.api.interfaces.CreativeTabPredicate;
import de.ambertation.wover.tabs.api.interfaces.CreativeTabsBuilderWithTab;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.NotNull;

public class CreativeTabBuilderImpl implements CreativeTabBuilder {
    private final String name;
    private final ResourceLocation id;
    private ItemLike icon;
    private CreativeTabPredicate predicate = item -> true;
    private Component title;
    private final CreativeTabManagerImpl manager;

    CreativeTabBuilderImpl(@NotNull CreativeTabManagerImpl manager, @NotNull String name) {
        this.name = name;
        this.manager = manager;
        this.id = manager.C.mk(name + "_tab");
        this.title = Component.translatable("itemGroup." + manager.C.namespace + "." + name);
    }

    @Override
    public CreativeTabBuilderImpl setIcon(ItemLike icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public CreativeTabBuilderImpl setPredicate(CreativeTabPredicate predicate) {
        this.predicate = predicate;
        return this;
    }

    @Override
    public CreativeTabBuilderImpl setTitle(Component title) {
        this.title = title;
        return this;
    }

    @Override
    public CreativeTabsBuilderWithTab buildAndAdd() {
        if (icon == null)
            throw new IllegalStateException("Icon must be set");
        SimpleCreativeTabImpl res = new SimpleCreativeTabImpl(id, icon, title, predicate);
        manager.tabs.add(res);

        return manager;
    }
}
