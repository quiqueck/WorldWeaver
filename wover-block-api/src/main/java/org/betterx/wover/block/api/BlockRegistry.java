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

/**
 * Per-mod registry that tracks every {@link Block} (and its {@link BlockItem}) registered through it.
 * <p>
 * A {@link BlockRegistry} is the entry point for the {@link BlockDefinition} builder API
 * ({@link #defineDefaultBlock(String)}, {@link #defineDefaultBlockWithProps(String, Function)}, ...), and also
 * exposes lower-level {@code register(...)}/{@code registerBlockOnly(...)} methods for registering blocks
 * directly. Registered blocks are used by the auto datagen providers in
 * {@link org.betterx.wover.datagen.api.provider} to generate block tags and loot tables.
 * <p>
 * Obtain an instance with {@link #forMod(ModCore)}; use {@link #streamAll()} to iterate every registry that
 * currently exists (e.g. when writing datagen providers that need to process blocks from every mod).
 */
public abstract class BlockRegistry {
    /**
     * The {@link ModCore} this registry belongs to.
     */
    public final ModCore C;
    private final Map<ResourceKey<Block>, Block> blocks = new HashMap<>();
    private Map<Block, TagKey<Block>[]> datagenTags;


    protected BlockRegistry(ModCore modeCore) {
        this.C = modeCore;

        if (ModCore.isDatagen()) {
            datagenTags = new HashMap<>();
        }
    }

    /**
     * Streams every {@link BlockRegistry} instance that currently exists (one per mod that called
     * {@link #forMod(ModCore)}).
     *
     * @return A stream of all block registries
     */
    public static Stream<BlockRegistry> streamAll() {
        return BlockRegistryImpl.streamAll();
    }

    /**
     * Gets (or lazily creates) the {@link BlockRegistry} for the given mod.
     *
     * @param modCore The mod to get the registry for
     * @return The block registry for the given mod
     */
    public static BlockRegistry forMod(ModCore modCore) {
        return BlockRegistryImpl.forMod(modCore);
    }

    /**
     * Gets the {@link ItemRegistry} that block items registered through this registry are added to.
     *
     * @return The associated item registry
     */
    public abstract ItemRegistry itemRegistry();

    /**
     * Streams every {@link Block} that was registered through this registry.
     *
     * @return A stream of all registered blocks
     */
    public Stream<Block> allBlocks() {
        return blocks.values().stream();
    }

    /**
     * Streams every registered block together with its resource key.
     *
     * @return A stream of all registered block key/value entries
     */
    public Stream<Map.Entry<ResourceKey<Block>, Block>> allEntries() {
        return blocks.entrySet().stream();
    }

    /**
     * Streams the {@link BlockItem} of every registered block that has one.
     *
     * @return A stream of all registered block items
     */
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

    /**
     * Starts a fluent {@link VanillaBlockDefinition} for a plain vanilla {@link Block}.
     *
     * @param blockName The name identifier for the block
     * @return A new block definition, not yet built or registered
     */
    public VanillaBlockDefinition defineDefaultBlock(String blockName) {
        return new VanillaBlockDefinition(this, blockName);
    }

    /**
     * Starts a fluent {@link DefaultBlockDefinition} for a custom block, using a factory that receives the
     * fully configured definition.
     *
     * @param blockName    The name identifier for the block
     * @param blockFactory The factory that creates the block from the definition
     * @param <B>          The type of block to create
     * @return A new block definition, not yet built or registered
     */
    public <B extends Block> DefaultBlockDefinition<B> defineDefaultBlock(
            String blockName,
            DefaultBlockDefinition.BlockFactory<B> blockFactory
    ) {
        return new DefaultBlockDefinition<>(this, blockName, blockFactory);
    }

    /**
     * Starts a fluent {@link DefaultBlockDefinition} for a custom block, using a factory that only needs the
     * configured {@link BlockBehaviour.Properties} (e.g. a block constructor reference).
     *
     * @param blockName    The name identifier for the block
     * @param blockFactory The factory that creates the block from its properties
     * @param <B>          The type of block to create
     * @return A new block definition, not yet built or registered
     */
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
     * @param key      The resource key identifying the block
     * @param block    The block instance to register
     * @param tags     The block tags to apply during datagen
     * @param itemKey  The resource key identifying the block's item
     * @param itemTags The item tags to apply during datagen
     * @param <T>      The type of block being registered
     *                 Will be removed in the 21.6.x series once BetterNether and BetterEnd were updated to use the new system.
     */
    @Deprecated(forRemoval = true, since = "21.6.0")
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

    /**
     * Registers a block (and a default {@link BlockItem} for it) under this registry's mod namespace.
     * <p>
     * Prefer the {@link BlockDefinition} builder API ({@link #defineDefaultBlock(String)} etc.) for new code;
     * this method exists mainly for direct/legacy block registration.
     *
     * @param path  The name identifier for the block
     * @param block The block instance to register
     * @param tags  The block tags to apply during datagen
     * @param <T>   The type of block being registered
     * @return The registered block (same instance as {@code block})
     */
    @SafeVarargs
    public final <T extends Block> T register(String path, T block, TagKey<Block>... tags) {
        return register(path, block, tags, null);
    }


    /**
     * Registers a block (and a default {@link BlockItem} for it) under this registry's mod namespace, also
     * applying item tags to the created block item.
     *
     * @param path     The name identifier for the block
     * @param block    The block instance to register
     * @param tags     The block tags to apply during datagen
     * @param itemTags The item tags to apply to the block item during datagen
     * @param <T>      The type of block being registered
     * @return The registered block (same instance as {@code block})
     */
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

    /**
     * Registers a block under this registry's mod namespace without creating or registering a
     * {@link BlockItem} for it.
     *
     * @param path  The name identifier for the block
     * @param block The block instance to register
     * @param tags  The block tags to apply during datagen
     * @param <T>   The type of block being registered
     * @return The registered block (same instance as {@code block})
     */
    @SafeVarargs
    public final <T extends Block> T registerBlockOnly(String path, T block, TagKey<Block>... tags) {
        if (block != null && block != Blocks.AIR) {
            _registerBlockOnly(key(path), block, tags);
        }

        return block;
    }

    /**
     * Registers a {@link BlockItem} with this registry's underlying {@link ItemRegistry}. Implemented by
     * platform-specific subclasses.
     *
     * @param itemKey The resource key identifying the item
     * @param item    The block item instance to register
     * @param tags    The item tags to apply during datagen
     */
    protected abstract void registerBlockItem(
            @NotNull ResourceKey<Item> itemKey,
            @NotNull BlockItem item,
            @Nullable TagKey<Item>... tags
    );

    /**
     * Creates the default {@link Item.Properties} used when a {@link BlockItem} is created without explicit
     * properties.
     *
     * @return A new, unconfigured {@link Item.Properties} instance
     */
    protected Item.Properties defaultBlockItemSettings() {
        return new Item.Properties();
    }

    /**
     * Called during tag datagen to add every tag registered against a block in this registry (either via
     * {@code tags} passed to {@code register(...)}, or via {@link BlockTagProvider}) to {@code ctx}.
     * Normally invoked automatically by {@link org.betterx.wover.datagen.api.provider.AutoBlockRegistryTagProvider}.
     *
     * @param ctx The context to add the collected tags to
     */
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

    /**
     * Called during loot table datagen to generate the loot table of every block in this registry that
     * implements {@link BlockLootProvider}. Normally invoked automatically by
     * {@link org.betterx.wover.datagen.api.provider.AutoBlockLootProvider}.
     *
     * @param lookup     The registry lookup provider, forwarded to {@link LootLookupProvider}
     * @param biConsumer Consumer that a generated loot table's key/builder pair is passed to
     */
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
                    var key = LootTableManager.getBlockLootTableKey(b.getKey());
                    var builder = ((BlockLootProvider) b.getValue()).registerBlockLoot(
                            b.getKey().location(),
                            provider,
                            key
                    );

                    if (builder != null)
                        biConsumer.accept(key, builder);
                });
    }

    /**
     * Registers the block with Fabric's {@link FlammableBlockRegistry} using a default burn/spread chance
     * of 5, but only if the block's default state is {@link BlockBehaviour.Properties#ignitedByLava()} and
     * it isn't already registered as flammable.
     *
     * @param block The block to register as flammable
     */
    public static void registerAsFlammable(Block block) {
        registerAsFlammable(block, 5, 5);
    }

    /**
     * Registers the block with Fabric's {@link FlammableBlockRegistry} using the given burn/spread chance,
     * but only if the block's default state is {@link BlockBehaviour.Properties#ignitedByLava()} and it
     * isn't already registered as flammable.
     *
     * @param block  The block to register as flammable
     * @param burn   The chance (0-300) that the block burns when adjacent to fire
     * @param spread The chance (0-100) that fire spreads to the block
     */
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