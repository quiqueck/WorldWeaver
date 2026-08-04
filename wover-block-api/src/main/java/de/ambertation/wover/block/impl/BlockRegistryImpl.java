package de.ambertation.wover.block.impl;

import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.item.impl.ItemRegistryImpl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BlockRegistryImpl extends BlockRegistry {
    private static final Map<ModCore, BlockRegistryImpl> REGISTRIES = new HashMap<>();
    private final ItemRegistryImpl itemRegistry;

    private BlockRegistryImpl(ModCore modeCore) {
        super(modeCore);
        this.itemRegistry = (ItemRegistryImpl) ItemRegistry.forMod(modeCore);
    }

    public static Stream<BlockRegistry> streamAll() {
        return REGISTRIES.values().stream().map(r -> (BlockRegistry) r);
    }

    public static BlockRegistry forMod(ModCore modCore) {
        return REGISTRIES.computeIfAbsent(modCore, c -> new BlockRegistryImpl(modCore));
    }

    @Override
    public ItemRegistry itemRegistry() {
        return itemRegistry;
    }

    @SafeVarargs
    @Override
    protected final void registerBlockItem(
            @NotNull ResourceKey<Item> itemKey,
            @NotNull BlockItem blockItem,
            @Nullable TagKey<Item>... tags
    ) {
        this.itemRegistry.register(itemKey, blockItem, tags);
    }
}
