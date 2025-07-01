package org.betterx.wover.block.api;

import org.betterx.wover.block.impl.BlockRegistryImpl;
import org.betterx.wover.block.impl.WoverBlockItemImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;
import org.betterx.wover.loot.api.LootTableManager;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockRegistry {
    public final ModCore C;
    private final Map<ResourceKey<Block>, Block> blocks = new HashMap<>();
    private Map<Block, TagKey<Block>[]> datagenTags;


    protected BlockRegistry(ModCore modeCore) {
        this.C = modeCore;

        if (ModCore.isDatagen()) {
            datagenTags = new HashMap<>();
        }
    }

    public static Stream<BlockRegistry> streamAll() {
        return BlockRegistryImpl.streamAll();
    }

    public static BlockRegistry forMod(ModCore modCore) {
        return BlockRegistryImpl.forMod(modCore);
    }

    public abstract ItemRegistry itemRegistry();

    public Stream<Block> allBlocks() {
        return blocks.values().stream();
    }

    public Stream<Map.Entry<ResourceKey<Block>, Block>> allEntries() {
        return blocks.entrySet().stream();
    }

    public Stream<BlockItem> allBlockItems() {
        return blocks
                .values()
                .stream()
                .filter(block -> block.asItem() instanceof BlockItem)
                .map(block -> (BlockItem) block.asItem());
    }

    /**
     * Creates a resource key for a block with the given name.
     * The resource key uses this registry's mod namespace.
     *
     * @param blockName The name identifier for the block
     * @return A ResourceKey for the block in this mod's namespace
     */
    public @NotNull ResourceKey<Block> key(@NotNull String blockName) {
        return ResourceKey.create(BuiltInRegistries.BLOCK.key(), C.mk(blockName));
    }

    public @NotNull ResourceKey<Item> blockItemKey(@NotNull ResourceKey<Block> blockKey) {
        return ResourceKey.create(BuiltInRegistries.ITEM.key(), blockKey.location());
    }

    public VanillaBlockDefinition defineDefaultBlock(String blockName) {
        return new VanillaBlockDefinition(this, blockName);
    }

    public <B extends Block> DefaultBlockDefinition<B> defineDefaultBlock(
            String blockName,
            DefaultBlockDefinition.BlockFactory<B> blockFactory
    ) {
        return new DefaultBlockDefinition<>(this, blockName, blockFactory);
    }

    public <B extends Block> DefaultBlockDefinition<B> defineDefaultBlockWithProps(
            String blockName,
            Function<BlockBehaviour.Properties, B> blockFactory
    ) {
        return new DefaultBlockDefinition<>(this, blockName, (def) -> blockFactory.apply(def.properties));
    }

    <T extends Block> boolean register(
            @NotNull ResourceKey<Block> key,
            T block,
            @Nullable TagKey<Block>[] tags
    ) {
        if (block != null && block != Blocks.AIR) {
            _registerBlockOnly(key, block, tags);
            return true;
        }
        return false;
    }

    /**
     * This is here for legacy support. It allows registering a block with a
     * {@link CustomBlockItemProvider}. Going forward we use the BlockDefinition system
     * to handle block registration and item creation.
     *
     * @param key
     * @param block
     * @param tags
     * @param itemKey
     * @param itemTags
     * @param <T>
     * @deprecated Will be removed in the 21.6.x series once BetterNether and BetterEnd were updated to use the new system.
     */
    <T extends Block> void registerLegacy(
            @NotNull ResourceKey<Block> key,
            T block,
            @Nullable TagKey<Block>[] tags,
            @NotNull ResourceKey<Item> itemKey,
            @Nullable TagKey<Item>[] itemTags
    ) {
        if (register(key, block, tags)) {
            final BlockItem item;

            if (block instanceof CustomBlockItemProvider provider) {
                item = provider.getCustomBlockItem(
                        itemKey.location(),
                        defaultBlockItemSettings().setId(ResourceKey.create(
                                BuiltInRegistries.ITEM.key(),
                                itemKey.location()
                        ))
                );
            } else {
                item = WoverBlockItemImpl.create(block, defaultBlockItemSettings().setId(itemKey));
            }

            if (itemTags == null)
                registerBlockItem(itemKey, item);
            else
                registerBlockItem(itemKey, item, itemTags);
        }
    }

    @SafeVarargs
    public final <T extends Block> T register(String path, T block, TagKey<Block>... tags) {
        return register(path, block, tags, null);
    }


    public <T extends Block> T register(String path, T block, TagKey<Block>[] tags, TagKey<Item>[] itemTags) {
        var blockKey = key(path);
        var itemKey = blockItemKey(blockKey);
        registerLegacy(blockKey, block, tags, itemKey, itemTags);

        registerAsFlammable(block);
        return block;
    }

    @SafeVarargs
    private void _registerBlockOnly(
            @NotNull ResourceKey<Block> key,
            @NotNull Block block,
            @Nullable TagKey<Block>... tags
    ) {
        Registry.register(BuiltInRegistries.BLOCK, key, block);
        blocks.put(key, block);

        if (datagenTags != null && tags != null && tags.length > 0) datagenTags.put(block, tags);
    }

    @SafeVarargs
    public final <T extends Block> T registerBlockOnly(String path, T block, TagKey<Block>... tags) {
        if (block != null && block != Blocks.AIR) {
            _registerBlockOnly(key(path), block, tags);
        }

        return block;
    }

    protected abstract void registerBlockItem(
            @NotNull ResourceKey<Item> itemKey,
            @NotNull BlockItem item,
            @Nullable TagKey<Item>... tags
    );

    protected Item.Properties defaultBlockItemSettings() {
        return new Item.Properties();
    }

    public void bootstrapBlockTags(TagBootstrapContext<Block> ctx) {
        if (datagenTags != null) {
            datagenTags.forEach(ctx::add);
        }

        blocks
                .entrySet()
                .stream()
                .filter(b -> b.getValue() instanceof BlockTagProvider)
                .forEach(b -> ((BlockTagProvider) b.getValue()).registerBlockTags(b.getKey().location(), ctx));
    }

    public void bootstrapBlockLoot(
            @NotNull HolderLookup.Provider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer
    ) {
        LootLookupProvider provider = new LootLookupProvider(lookup);
        blocks
                .entrySet()
                .stream()
                .filter(b -> b.getValue() instanceof BlockLootProvider)
                .forEach(b -> {
                    var key = LootTableManager.getBlockLootTableKey(C, b.getKey().location());
                    var builder = ((BlockLootProvider) b.getValue()).registerBlockLoot(
                            b.getKey().location(),
                            provider,
                            key
                    );

                    if (builder != null)
                        biConsumer.accept(key, builder);
                });
    }

    public static void registerAsFlammable(Block block) {
        registerAsFlammable(block, 5, 5);
    }

    public static void registerAsFlammable(
            Block block,
            int burn,
            int spread
    ) {
        if (block.defaultBlockState().ignitedByLava()
                && FlammableBlockRegistry.getDefaultInstance()
                                         .get(block)
                                         .getBurnChance() == 0) {
            FlammableBlockRegistry.getDefaultInstance().add(block, burn, spread);
        }
    }
}