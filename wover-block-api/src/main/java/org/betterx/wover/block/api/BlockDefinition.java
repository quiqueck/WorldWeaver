package org.betterx.wover.block.api;

import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.item.api.BlockItemDefinition;
import org.betterx.wover.item.api.VanillaBlockItemDefinition;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Fluent builder base class for defining, configuring and registering a {@link Block}.
 * <p>
 * A {@link BlockDefinition} wraps a {@link BlockBehaviour.Properties} instance together with an optional
 * set of {@link BlockTrait}s, block/item tags and a {@link BlockItemDefinitionFactory}. Calling
 * {@link #buildAndRegister()} creates the block, registers it with the owning {@link #registry}, applies
 * all configured traits and (unless {@link #noBlockItem()} was called) creates and registers the matching
 * {@link BlockItem}.
 * <p>
 * Instances are usually not created directly, but obtained from one of the {@code defineDefaultBlock(...)}
 * factory methods on {@link BlockRegistry}.
 *
 * @param <B> The type of {@link Block} this definition creates
 * @param <D> The concrete subclass type, used for fluent method chaining
 */
public abstract class BlockDefinition<B extends Block, D extends BlockDefinition<B, D>> implements BlockTraitLookup {
    /**
     * Factory used to create the {@link Block} instance from a fully configured definition.
     *
     * @param <B> The type of {@link Block} to create
     * @param <D> The configuration type used to create the block
     */
    public interface BlockFactory<B extends Block, D extends BlockDefinition<B, D>> {
        /**
         * Creates a block instance from the given configuration.
         *
         * @param definition The configuration object containing all block settings
         * @return The created block instance
         */
        B createItem(D definition);
    }

    /**
     * Factory used to create the {@link BlockItemDefinition} for a block created by this definition.
     *
     * @param <B> The type of {@link Block} the item is created for
     * @param <D> The configuration type used to create the block
     */
    public interface BlockItemDefinitionFactory<B extends Block, D extends BlockDefinition<B, D>> {
        /**
         * Creates the {@link BlockItemDefinition} that will be built and registered for {@code sourceBlock}.
         *
         * @param definition  The block definition the item is created for
         * @param sourceBlock The already built block instance
         * @return The item definition to build and register, or {@code null} to skip item creation
         */
        BlockItemDefinition<?, ?> get(D definition, B sourceBlock);
    }

    /**
     * The registry this definition will register the block (and its item) with.
     */
    public final BlockRegistry registry;

    /**
     * The resource key identifying the block.
     */
    public final ResourceKey<Block> blockKey;

    /**
     * The resource key identifying the block's item.
     */
    protected final @NotNull ResourceKey<Item> itemKey;

    /**
     * The properties configuration for the block.
     */
    protected BlockBehaviour.Properties properties;

    /**
     * Optional tags to be applied to the block.
     */
    protected List<TagKey<Block>> tags;

    /**
     * Optional tags to be applied to the block's item.
     */
    protected List<TagKey<Item>> itemTags;

    /**
     * List of traits applied to this block.
     * Each trait is configured with its own configuration object.
     */
    protected List<BlockTrait<? super B, ?>> traits;

    /**
     * Factory instance used to create the block.
     */
    protected final BlockDefinition.BlockFactory<B, D> blockFactory;

    /**
     * Optional factory used to create the {@link BlockItemDefinition} for the built block. If {@code null},
     * a {@link VanillaBlockItemDefinition} is used instead.
     */
    protected @Nullable BlockItemDefinitionFactory<B, D> blockItemDefinitionSupplier;

    /**
     * The {@link BlockItem} that was built for this definition, populated after {@link #buildAndRegister()}
     * was called. May be {@code null} if {@link #noBlockItem()} was configured.
     */
    protected BlockItem blockItem;

    /**
     * Creates a new block configuration with explicit properties.
     *
     * @param registry     The block registry to use for registration
     * @param blockName    The name identifier for the block
     * @param blockFactory The factory used to create the block instance
     * @param properties   The properties to use for the block
     */
    protected BlockDefinition(
            BlockRegistry registry,
            String blockName,
            BlockDefinition.BlockFactory<B, D> blockFactory,
            BlockBehaviour.Properties properties
    ) {
        this.blockKey = registry.key(blockName);
        this.itemKey = registry.blockItemKey(blockKey);

        this.properties = properties.setId(registry.key(blockName));
        this.blockFactory = blockFactory;
        this.registry = registry;
    }

    /**
     * Creates a new block configuration with default properties ({@link BlockBehaviour.Properties#of()}).
     *
     * @param registry     The block registry to use for registration
     * @param blockName    The name identifier for the block
     * @param blockFactory The factory used to create the block instance
     */
    protected BlockDefinition(
            BlockRegistry registry,
            String blockName,
            BlockDefinition.BlockFactory<B, D> blockFactory
    ) {
        this(
                registry,
                blockName,
                blockFactory,
                BlockBehaviour.Properties.of()
        );
    }

    /**
     * Creates a new block configuration, copying its initial properties from an existing block.
     *
     * @param registry     The block registry to use for registration
     * @param blockName    The name identifier for the block
     * @param blockFactory The factory used to create the block instance
     * @param templateBlock The block whose properties should be copied as a starting point
     */
    protected BlockDefinition(
            BlockRegistry registry,
            String blockName,
            BlockDefinition.BlockFactory<B, D> blockFactory,
            BlockBehaviour templateBlock
    ) {
        this(
                registry,
                blockName,
                blockFactory,
                BlockBehaviour.Properties.ofFullCopy(templateBlock)
        );
    }

    /**
     * Called before the block is built to allow subclasses to perform any final configuration.
     * This method is called automatically by {@link #build()}.
     */
    abstract protected void beforeBuild();

    /**
     * Called before the block is registered to allow subclasses to perform any final modifications.
     * This method is called automatically by {@link #buildAndRegister()} after the block is built
     * but before it is registered with the registry.
     *
     * @param block The built block instance that will be registered
     * @return The block instance (potentially modified) that should be registered
     */
    abstract protected @NotNull B beforeRegister(@NotNull B block);

    /**
     * Called after the block was registered, and after all traits' {@code afterBlockRegistration} hooks
     * ran, but before the block's item is built and registered. The default implementation does nothing
     * and simply returns {@code block}; subclasses may override it to perform additional setup.
     *
     * @param block The already registered block instance
     * @return The block instance (potentially modified) to continue with
     */
    protected B afterRegister(B block) {
        // Default implementation does nothing, can be overridden if needed
        return block;
    }

    /**
     * Overrides how the {@link BlockItem} for this block is created.
     *
     * @param blockItemDefinitionSupplier The factory that creates the {@link BlockItemDefinition} for the
     *                                    built block, or {@code null} to fall back to the default
     *                                    {@link VanillaBlockItemDefinition}
     * @return This configuration instance for method chaining
     */
    public D withBlockItem(
            @Nullable BlockItemDefinitionFactory<B, D> blockItemDefinitionSupplier
    ) {
        this.blockItemDefinitionSupplier = blockItemDefinitionSupplier;
        return (D) this;
    }

    /**
     * Prevents any {@link BlockItem} from being created and registered for this block.
     *
     * @return This configuration instance for method chaining
     */
    public D noBlockItem() {
        this.blockItemDefinitionSupplier = (d, b) -> null;
        return (D) this;
    }

    /**
     * Used in {@link #buildAndRegister()} to generate the BlockItem.
     *
     * @param sourceBlock The block for which the BlockItemDefinition is created.
     * @return
     */
    @SuppressWarnings("unchecked")
    protected @Nullable BlockItemDefinition<?, ?> getBlockItemDefinition(B sourceBlock) {
        if (blockItemDefinitionSupplier != null) {
            // If a custom BlockItemDefinitionFactory is provided, use it
            return blockItemDefinitionSupplier.get((D) this, sourceBlock);
        }
        return new VanillaBlockItemDefinition(this, sourceBlock);
    }

    /**
     * Builds the block instance using the configured properties and traits.
     * This method calls {@link #beforeBuild()} before creating the block, then configures every added
     * {@link BlockTrait} and collects their {@link RuntimeBlockTrait}s onto the block (if it implements
     * {@link BlockWithTraits}).
     *
     * @return The created block instance
     */
    @SuppressWarnings("unchecked")
    public final B build() {
        this.beforeBuild();
        final Map<BlockTraitKey, List<RuntimeBlockTrait<B, ?>>> runtimeTraits;

        // If traits are defined, configure them and collect RuntimeTraits
        if (this.traits != null && !this.traits.isEmpty()) {
            runtimeTraits = new HashMap<>(8);
            for (var configuredTrait : this.traits) {
                this.configurePropertiesUnchecked(configuredTrait);

                final RuntimeBlockTrait<B, ?> runtimeTrait = this.forRuntimeUnchecked(configuredTrait);
                if (runtimeTrait != null) {
                    // Collect runtime traits by their key
                    runtimeTraits.computeIfAbsent(
                            runtimeTrait.key(),
                            k -> new ArrayList<>()
                    ).add(runtimeTrait);
                }
            }
        } else runtimeTraits = null;

        // Apply all property setters to the properties
        for (Consumer<BlockBehaviour.Properties> propertySetter : this.propertySetters) {
            propertySetter.accept(this.properties);
        }

        B block = blockFactory.createItem((D) this);

        // If runtime traits were collected, set them on the block
        if (runtimeTraits != null && !runtimeTraits.isEmpty() && block instanceof BlockWithTraits<?>) {
            ((BlockWithTraits<B>) block).wover_setTraits(runtimeTraits);
        }

        return block;
    }


    /**
     * Builds the block and automatically registers it (and, unless {@link #noBlockItem()} was called, its
     * {@link BlockItem}) with {@link #registry}.
     * <p>
     * The process is: {@link #build()} → {@link #beforeRegister(Block)} → register block → run each
     * trait's {@code afterBlockRegistration} hook → {@link #afterRegister(Block)} → build and register
     * the block item.
     *
     * @return The created and registered block instance
     */
    @SuppressWarnings("unchecked")
    public final B buildAndRegister() {
        B block = this.beforeRegister(this.build());

        final TagKey<Block>[] tags = this.tags == null ? null : this.tags.toArray(TagKey[]::new);
        this.registry.register(this.blockKey, block, tags);

        // If traits are defined, call afterBlockRegistration for each trait
        if (this.traits != null) {
            for (var configuredTrait : this.traits) {
                this.afterBlockRegistrationUnchecked(block, configuredTrait);
            }
        }

        block = this.afterRegister(block);

        // Register the block item for this block. At this point the Block is fully configured
        var blockitemDefinition = this.getBlockItemDefinition(block);
        if (blockitemDefinition != null) {
            blockitemDefinition.addTags(itemTags);
            this.blockItem = blockitemDefinition.buildAndRegister();
        } else {
            this.blockItem = null;
        }

        return block;
    }

    /**
     * Gets the resource key that identifies this block's item.
     *
     * @return The item resource key
     */
    public final ResourceKey<Item> itemKey() {
        return this.itemKey;
    }

    /**
     * Adds every trait in the given list to this block definition. {@code null} or empty lists are ignored.
     *
     * @param traits The traits to add
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D addTrait(
            @Nullable List<BlockTrait<?, ?>> traits
    ) {
        if (traits == null || traits.isEmpty()) {
            // Skip null or empty trait lists
            return (D) this;
        }

        traits.forEach(this::addTrait);

        return (D) this;
    }

    /**
     * Adds a trait to this block definition.
     * Traits are used to add additional behaviors or properties to the block.
     *
     * @param trait The trait to add
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D addTrait(
            @Nullable BlockTrait<?, ?> trait
    ) {
        if (trait == null) {
            // Skip null traits
            return (D) this;
        }

        if (this.traits == null) this.traits = new LinkedList<>();

        if (trait.keepLatestOnly()) {
            this.traits = this.traits
                    .stream()
                    .filter(t -> !t.is(trait.key()))
                    .collect(Collectors.toCollection(LinkedList::new));
        }
        this.traits.add((BlockTrait<? super B, ?>) trait);
        return (D) this;
    }

    /**
     * Adds the default trait produced by the given builder to this block definition.
     *
     * @param traitBuilder The trait builder whose {@link BlockTraitBuilder.WithDefault#withDefault()} trait
     *                     should be added
     * @return This configuration instance for method chaining
     */
    public D addTrait(@NotNull BlockTraitBuilder.WithDefault<?, ?> traitBuilder) {
        return this.addTrait(traitBuilder.withDefault());
    }

    /**
     * Adds the default traits produced by the given builder to this block definition.
     *
     * @param traitBuilder The trait builder whose {@link BlockTraitBuilder.WithDefaults#withDefault()} traits
     *                     should be added
     * @return This configuration instance for method chaining
     */
    public D addTrait(@NotNull BlockTraitBuilder.WithDefaults<?, ?> traitBuilder) {
        return this.addTrait(traitBuilder.withDefault());
    }

    /**
     * Checks whether this definition already has a trait matching the given trait instance.
     *
     * @param trait The trait to check for
     * @return {@code true} if a matching trait was already added
     */
    public boolean hasTrait(BlockTraitImpl<?, ?> trait) {
        if (this.traits == null || this.traits.isEmpty()) {
            return false;
        }
        return this.traits.stream().anyMatch(t -> t.is(trait));
    }

    /**
     * Checks whether this definition already has a trait with the given key.
     *
     * @param traitKey The trait key to check for
     * @return {@code true} if a matching trait was already added
     */
    public boolean hasTrait(BlockTraitKey traitKey) {
        if (this.traits == null || this.traits.isEmpty()) {
            return false;
        }
        return this.traits.stream().anyMatch(trait -> trait.is(traitKey));
    }

    /**
     * Checks whether this definition already has a trait matching the given builder's key.
     *
     * @param traitBuilder The trait builder whose key should be checked for
     * @return {@code true} if a matching trait was already added
     */
    public boolean hasTrait(BlockTraitBuilder<?, ?> traitBuilder) {
        return this.hasTrait(traitBuilder.key());
    }

    /**
     * Sets the tags that should be applied to this block.
     *
     * @param blockTags The tags to apply to the block
     * @return This configuration instance for method chaining
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final D addTags(TagKey<Block>... blockTags) {
        if (this.tags == null) {
            this.tags = new ArrayList<>(blockTags.length);
        }

        for (TagKey<Block> tag : blockTags) {
            if (tag != null) {
                this.tags.add(tag);
            }
        }

        return (D) this;
    }

    /**
     * Gets the currently configured tags for this block.
     *
     * @return Array of tags applied to this block, may be null
     */
    @SuppressWarnings("unchecked")
    public TagKey<Block>[] tags() {
        return this.tags.toArray(TagKey[]::new);
    }

    /**
     * Sets the tags that should be applied to this item.
     *
     * @param itemTags The tags to apply to the item
     * @return This configuration instance for method chaining
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final D addItemTags(TagKey<Item>... itemTags) {
        if (this.itemTags == null) {
            this.itemTags = new ArrayList<>(itemTags.length);
        }

        for (TagKey<Item> tag : itemTags) {
            if (tag != null) {
                this.itemTags.add(tag);
            }
        }

        return (D) this;
    }

    /**
     * Gets the currently configured tags for the blockItem.
     *
     * @return Array of tags applied to this blockItem, may be null
     */
    public TagKey<Item>[] itemTags() {
        return this.itemTags.toArray(new TagKey[0]);
    }


    /**
     * Gets the underlying BlockTrait.Properties object used by this configuration.
     * This provides direct access to the properties for advanced configuration scenarios.
     *
     * @return The BlockTrait.Properties instance containing all configured properties
     */
    public BlockBehaviour.Properties getProperties() {
        return this.properties;
    }

    // Helper methods to handle generic type casting
    @SuppressWarnings("unchecked")
    private void configurePropertiesUnchecked(
            BlockTrait<? super B, ?> trait
    ) {
        ((BlockTrait<B, ?>) trait).configure((D) this);
    }

    @SuppressWarnings("unchecked")
    private RuntimeBlockTrait<B, ?> forRuntimeUnchecked(
            BlockTrait<? super B, ?> trait
    ) {
        // Cast is safe because the trait can work with B (since B extends the super type)
        return (RuntimeBlockTrait<B, ?>) trait.forRuntime();
    }

    @SuppressWarnings("unchecked")
    private void afterBlockRegistrationUnchecked(
            B block,
            BlockTrait<? super B, ?> trait
    ) {
        // Cast is safe because the trait can work with B (since B extends the super type)
        ((BlockTrait<B, ?>) trait).afterBlockRegistration(block, (D) this);
    }

    // **********************************************************************
    // Redirect all BlockTrait.Properties methods (except setId) to this.properties
    protected List<Consumer<BlockBehaviour.Properties>> propertySetters = new LinkedList<>();

    /**
     * Sets the map color for this block using a dye color.
     *
     * @param dyeColor The dye color to use for the map color
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D mapColor(DyeColor dyeColor) {
        propertySetters.add((properties) -> properties.mapColor(dyeColor));
        return (D) this;
    }

    /**
     * Sets the map color for this block.
     *
     * @param mapColor The map color to use
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D mapColor(MapColor mapColor) {
        propertySetters.add((properties) -> properties.mapColor(mapColor));
        return (D) this;
    }

    /**
     * Sets the map color for this block using a function that determines the color based on block state.
     *
     * @param function Function that returns the map color for a given block state
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D mapColor(Function<BlockState, MapColor> function) {
        propertySetters.add((properties) -> properties.mapColor(function));
        return (D) this;
    }

    /**
     * Makes this block non-collidable (entities can pass through it).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D noCollission() {
        propertySetters.add((properties) -> properties.noCollission());
        return (D) this;
    }

    /**
     * Prevents this block from occluding adjacent blocks (doesn't block light or rendering).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D noOcclusion() {
        propertySetters.add((properties) -> properties.noOcclusion());
        return (D) this;
    }

    /**
     * Sets the friction coefficient for this block surface.
     *
     * @param friction The friction value (0.0 = no friction, 1.0 = normal friction)
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D friction(float friction) {
        propertySetters.add((properties) -> properties.friction(friction));
        return (D) this;
    }

    /**
     * Sets the speed factor for entities walking on this block.
     *
     * @param speedFactor The speed multiplier (1.0 = normal speed, higher = faster, lower = slower)
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D speedFactor(float speedFactor) {
        propertySetters.add((properties) -> properties.speedFactor(speedFactor));
        return (D) this;
    }

    /**
     * Sets the jump factor for entities jumping on this block.
     *
     * @param jumpFactor The jump multiplier (1.0 = normal jump, higher = higher jumps)
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D jumpFactor(float jumpFactor) {
        propertySetters.add((properties) -> properties.jumpFactor(jumpFactor));
        return (D) this;
    }

    /**
     * Sets the sound type for this block (affects break, place, step, etc. sounds).
     *
     * @param soundType The sound type to use
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D sound(SoundType soundType) {
        propertySetters.add((properties) -> properties.sound(soundType));
        return (D) this;
    }

    /**
     * Sets the light level emitted by this block.
     *
     * @param lightLevel Function that returns the light level (0-15) for a given block state
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D lightLevel(ToIntFunction<BlockState> lightLevel) {
        propertySetters.add((properties) -> properties.lightLevel(lightLevel));
        return (D) this;
    }

    /**
     * Sets both the destroy time and explosion resistance of this block.
     *
     * @param destroyTime         The time it takes to break the block
     * @param explosionResistance The resistance to explosions
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D strength(float destroyTime, float explosionResistance) {
        propertySetters.add((properties) -> properties.strength(destroyTime, explosionResistance));
        return (D) this;
    }

    /**
     * Makes this block break instantly (0 break time).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D instabreak() {
        propertySetters.add((properties) -> properties.instabreak());
        return (D) this;
    }

    /**
     * Sets both the destroy time and explosion resistance to the same value.
     *
     * @param strength The strength value for both destroy time and explosion resistance
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D strength(float strength) {
        propertySetters.add((properties) -> properties.strength(strength));
        return (D) this;
    }

    /**
     * Makes this block receive random ticks (for growth, decay, etc.).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D randomTicks() {
        propertySetters.add((properties) -> properties.randomTicks());
        return (D) this;
    }

    /**
     * Marks this block as having a dynamic shape (shape can change based on state).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D dynamicShape() {
        propertySetters.add((properties) -> properties.dynamicShape());
        return (D) this;
    }

    /**
     * Prevents this block from dropping any loot when broken.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D noLootTable() {
        propertySetters.add((properties) -> properties.noLootTable());
        return (D) this;
    }

    /**
     * Overrides the loot table for this block.
     *
     * @param lootTable Optional loot table resource key
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D overrideLootTable(Optional<ResourceKey<LootTable>> lootTable) {
        propertySetters.add((properties) -> properties.overrideLootTable(lootTable));
        return (D) this;
    }

    /**
     * Makes this block ignitable by lava. You should add the
     * FlammableBlockTrait to register it properly.
     *
     * @return This configuration instance for method chaining
     * @deprecated Use FlammableBlockTrait instead, as this method is
     * deprecated and will be removed in future versions.
     */
    @Deprecated(forRemoval = true)
    @SuppressWarnings("unchecked")
    public D ignitedByLava() {
        propertySetters.add((properties) -> properties.ignitedByLava());
        return (D) this;
    }

    /**
     * Marks this block as a liquid.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D liquid() {
        propertySetters.add((properties) -> properties.liquid());
        return (D) this;
    }

    /**
     * Forces this block to be considered solid for rendering and collision purposes.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D forceSolidOn() {
        propertySetters.add((properties) -> properties.forceSolidOn());
        return (D) this;
    }

    /**
     * Forces this block to not be considered solid for rendering and collision purposes.
     *
     * @return This configuration instance for method chaining
     * @deprecated Use other methods to control solidity
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public D forceSolidOff() {
        propertySetters.add((properties) -> properties.forceSolidOff());
        return (D) this;
    }

    /**
     * Sets how this block reacts to pistons.
     *
     * @param pushReaction The piston push reaction (NORMAL, DESTROY, BLOCK, IGNORE, PUSH_ONLY)
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D pushReaction(PushReaction pushReaction) {
        propertySetters.add((properties) -> properties.pushReaction(pushReaction));
        return (D) this;
    }

    /**
     * Marks this block as air (invisible, non-solid, etc.).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D air() {
        propertySetters.add((properties) -> properties.air());
        return (D) this;
    }

    /**
     * Sets a predicate to determine if entities can spawn on this block.
     *
     * @param predicate Predicate that tests if spawning is valid for a given entity type
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D isValidSpawn(BlockBehaviour.StateArgumentPredicate<EntityType<?>> predicate) {
        propertySetters.add((properties) -> properties.isValidSpawn(predicate));
        return (D) this;
    }

    /**
     * Sets a predicate to determine if this block conducts redstone signals.
     *
     * @param predicate Predicate that tests if the block conducts redstone
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D isRedstoneConductor(BlockBehaviour.StatePredicate predicate) {
        propertySetters.add((properties) -> properties.isRedstoneConductor(predicate));
        return (D) this;
    }

    /**
     * Sets a predicate to determine if this block suffocates entities inside it.
     *
     * @param predicate Predicate that tests if the block is suffocating
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D isSuffocating(BlockBehaviour.StatePredicate predicate) {
        propertySetters.add((properties) -> properties.isSuffocating(predicate));
        return (D) this;
    }

    /**
     * Sets a predicate to determine if this block blocks view (for rendering optimizations).
     *
     * @param predicate Predicate that tests if the block blocks view
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D isViewBlocking(BlockBehaviour.StatePredicate predicate) {
        propertySetters.add((properties) -> properties.isViewBlocking(predicate));
        return (D) this;
    }

    /**
     * Sets a predicate to determine if this block requires post-processing during rendering.
     *
     * @param predicate Predicate that tests if post-processing is needed
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D hasPostProcess(BlockBehaviour.StatePredicate predicate) {
        propertySetters.add((properties) -> properties.hasPostProcess(predicate));
        return (D) this;
    }

    /**
     * Sets a predicate to determine if this block should use emissive rendering.
     *
     * @param predicate Predicate that tests if emissive rendering should be used
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D emissiveRendering(BlockBehaviour.StatePredicate predicate) {
        propertySetters.add((properties) -> properties.emissiveRendering(predicate));
        return (D) this;
    }

    /**
     * Makes this block require the correct tool to drop items when broken.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D requiresCorrectToolForDrops() {
        propertySetters.add((properties) -> properties.requiresCorrectToolForDrops());
        return (D) this;
    }

    /**
     * Sets the time it takes to destroy this block.
     *
     * @param destroyTime The destroy time in seconds
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D destroyTime(float destroyTime) {
        propertySetters.add((properties) -> properties.destroyTime(destroyTime));
        return (D) this;
    }

    /**
     * Sets the explosion resistance of this block.
     *
     * @param explosionResistance The resistance to explosions
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D explosionResistance(float explosionResistance) {
        propertySetters.add((properties) -> properties.explosionResistance(explosionResistance));
        return (D) this;
    }

    /**
     * Sets the offset type for this block (how it's positioned within its block space).
     *
     * @param offsetType The offset type (NONE, XZ, XYZ)
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D offsetType(BlockBehaviour.OffsetType offsetType) {
        propertySetters.add((properties) -> properties.offsetType(offsetType));
        return (D) this;
    }

    /**
     * Prevents this block from spawning terrain particles when walked on.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D noTerrainParticles() {
        propertySetters.add((properties) -> properties.noTerrainParticles());
        return (D) this;
    }

    /**
     * Sets the required feature flags for this block to be available.
     *
     * @param featureFlags The feature flags required for this block
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D requiredFeatures(FeatureFlag... featureFlags) {
        propertySetters.add((properties) -> properties.requiredFeatures(featureFlags));
        return (D) this;
    }

    /**
     * Sets the noteblock instrument this block produces when used as a noteblock base.
     *
     * @param instrument The noteblock instrument to use
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D instrument(NoteBlockInstrument instrument) {
        propertySetters.add((properties) -> properties.instrument(instrument));
        return (D) this;
    }

    /**
     * Makes this block replaceable by other blocks during world generation and placement.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D replaceable() {
        propertySetters.add((properties) -> properties.replaceable());
        return (D) this;
    }

    /**
     * Overrides the description key for this block.
     *
     * @param descriptionKey The custom description key
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D overrideDescription(String descriptionKey) {
        propertySetters.add((properties) -> properties.overrideDescription(descriptionKey));
        return (D) this;
    }

    /**
     * Replaces the current properties with a copy of the provided block's properties.
     * This is useful for copying properties from an existing block. This will
     * overwrite any existing properties set on this configuration.
     *
     * @param block The block whose properties should be copied
     * @return This configuration instance for method chaining
     */
    public D replacePropertiesWithCopy(BlockBehaviour block) {
        // ofFullCopy() builds a fresh Properties with no id - the constructor already set one on
        // the properties object we're replacing here, so re-apply it explicitly.
        this.properties = BlockBehaviour.Properties.ofFullCopy(block).setId(this.blockKey);
        return (D) this;
    }
}
