package org.betterx.wover.item.impl;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.ItemRegistry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemRegistryImpl extends ItemRegistry {
    /**
     * Global registry mapping mod cores to their respective item registries
     */
    private static final Map<ModCore, ItemRegistryImpl> REGISTRIES = new HashMap<>();

    /**
     * Creates a new item registry for the specified mod core.
     * Private constructor to ensure controlled creation through {@link #forMod(ModCore)}.
     *
     * @param modCore The mod core this registry will belong to
     */
    private ItemRegistryImpl(ModCore modCore) {
        super(modCore);
    }

    public static Stream<ItemRegistry> streamAll() {
        return REGISTRIES.values().stream().map(r -> (ItemRegistry) r);
    }

    public static ItemRegistry forMod(ModCore modCore) {
        return REGISTRIES.computeIfAbsent(modCore, c -> new ItemRegistryImpl(modCore));
    }

    @Override
    public <T extends Item> void register(@NotNull ResourceKey<Item> key, T item, @Nullable TagKey<Item>[] tags) {
        super.register(key, item, tags);
    }
}
