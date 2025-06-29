package org.betterx.wover.block.api;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockWithTraits;
import org.betterx.wover.block.api.trait.RuntimeBlockTrait;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.util.GrowableArray;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlag;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class BlockDefinition<B extends Block, D extends BlockDefinition<B, D>> {
    public interface BlockFactory<B extends Block, D extends BlockDefinition<B, D>> {
        B createItem(D definition);
    }

    public final BlockRegistry registry;

    public final ResourceKey<Block> blockKey;
    protected final @NotNull ResourceKey<Item> itemKey;

    protected final BlockBehaviour.Properties properties;

    protected GrowableArray<TagKey<Block>> tags;
    protected GrowableArray<TagKey<Item>> itemTags;

    protected List<BlockTrait<? super B, ?>> traits;

    protected final BlockDefinition.BlockFactory<B, D> blockFactory;

    protected BlockDefinition(
            BlockRegistry registry,
            String blockName,
            BlockDefinition.BlockFactory<B, D> blockFactory
    ) {
        this.blockKey = registry.key(blockName);
        this.itemKey = registry.blockItemKey(blockKey);

        this.properties = BlockBehaviour.Properties.of().setId(this.blockKey);
        this.blockFactory = blockFactory;
        this.registry = registry;
    }

    abstract protected void beforeBuild();

    abstract protected B beforeRegister(B block);

    @SuppressWarnings("unchecked")
    public final B build() {
        this.beforeBuild();
        final List<RuntimeBlockTrait<B, ?>> runtimeTraits;

        // If traits are defined, configure them and collect RuntimeTraits
        if (this.traits != null && !this.traits.isEmpty()) {
            runtimeTraits = new LinkedList<>();
            for (var configuredTrait : this.traits) {
                this.configurePropertiesUnchecked(configuredTrait);

                final RuntimeBlockTrait<B, ?> runtimeTrait = this.forRuntimeUnchecked(configuredTrait);
                if (runtimeTrait != null) runtimeTraits.add(runtimeTrait);
            }
        } else runtimeTraits = null;

        B block = blockFactory.createItem((D) this);

        // If runtime traits were collected, set them on the block
        if (runtimeTraits != null && !runtimeTraits.isEmpty() && block instanceof BlockWithTraits<?>) {
            ((BlockWithTraits<B>) block).wover_setTraits(runtimeTraits);
        }

        return block;
    }


    public final B buildAndRegister() {
        B block = this.beforeRegister(this.build());
        this.registry.register(
                this.blockKey,
                block,
                tags == null ? null : tags.elements(),
                this.itemKey,
                itemTags == null ? null : itemTags.elements()
        );

        // If traits are defined, call afterBlockRegistration for each trait
        if (this.traits != null) {
            for (var configuredTrait : this.traits) {
                this.afterBlockRegistrationUnchecked(block, configuredTrait);
            }
        }

        return block;
    }

    public final ResourceKey<Item> itemKey() {
        return this.itemKey;
    }

    /**
     * Adds a trait to this block definition.
     * Traits are used to add additional behaviors or properties to the block.
     *
     * @param trait The trait to add
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends BlockTrait<? super B, ?>> D addTrait(
            @Nullable T trait
    ) {
        if (trait == null) {
            // Skip null traits
            return (D) this;
        }
        if (trait.clientOnly() && !ModCore.isClient()) {
            // Skip traits that are only for the client side
            return (D) this;
        }
        if (trait.datagenOnly() && !ModCore.isDatagen()) {
            // Skip traits that are only for data generation
            return (D) this;
        }


        if (this.traits == null) this.traits = new LinkedList<>();

        this.traits.add(trait);
        return (D) this;
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
            this.tags = new GrowableArray<>(blockTags);
        } else {
            this.tags.add(blockTags);
        }
        return (D) this;
    }

    /**
     * Gets the currently configured tags for this block.
     *
     * @return Array of tags applied to this block, may be null
     */
    public TagKey<Block>[] tags() {
        return this.tags.elements();
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
            this.itemTags = new GrowableArray<>(itemTags);
        } else {
            this.itemTags.add(itemTags);
        }
        return (D) this;
    }

    /**
     * Gets the currently configured tags for the blockItem.
     *
     * @return Array of tags applied to this blockItem, may be null
     */
    public TagKey<Item>[] itemTags() {
        return this.itemTags.elements();
    }

    // **********************************************************************
    // Redirect all BlockTrait.Properties methods (except setId) to this.properties

    /**
     * Sets the map color for this block using a dye color.
     *
     * @param dyeColor The dye color to use for the map color
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D mapColor(DyeColor dyeColor) {
        this.properties.mapColor(dyeColor);
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
        this.properties.mapColor(mapColor);
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
        this.properties.mapColor(function);
        return (D) this;
    }

    /**
     * Makes this block non-collidable (entities can pass through it).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D noCollission() {
        this.properties.noCollission();
        return (D) this;
    }

    /**
     * Prevents this block from occluding adjacent blocks (doesn't block light or rendering).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D noOcclusion() {
        this.properties.noOcclusion();
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
        this.properties.friction(friction);
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
        this.properties.speedFactor(speedFactor);
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
        this.properties.jumpFactor(jumpFactor);
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
        this.properties.sound(soundType);
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
        this.properties.lightLevel(lightLevel);
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
        this.properties.strength(destroyTime, explosionResistance);
        return (D) this;
    }

    /**
     * Makes this block break instantly (0 break time).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D instabreak() {
        this.properties.instabreak();
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
        this.properties.strength(strength);
        return (D) this;
    }

    /**
     * Makes this block receive random ticks (for growth, decay, etc.).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D randomTicks() {
        this.properties.randomTicks();
        return (D) this;
    }

    /**
     * Marks this block as having a dynamic shape (shape can change based on state).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D dynamicShape() {
        this.properties.dynamicShape();
        return (D) this;
    }

    /**
     * Prevents this block from dropping any loot when broken.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D noLootTable() {
        this.properties.noLootTable();
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
        this.properties.overrideLootTable(lootTable);
        return (D) this;
    }

    /**
     * Makes this block ignitable by lava. You should add the
     * {@link org.betterx.wover.block.api.trait.FlammableBlockTrait} to register it properly.
     *
     * @return This configuration instance for method chaining
     * @deprecated Use {@link org.betterx.wover.block.api.trait.FlammableBlockTrait} instead, as this method is
     * deprecated and will be removed in future versions.
     */
    @Deprecated(forRemoval = true)
    @SuppressWarnings("unchecked")
    protected D ignitedByLava() {
        this.properties.ignitedByLava();
        return (D) this;
    }

    /**
     * Marks this block as a liquid.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D liquid() {
        this.properties.liquid();
        return (D) this;
    }

    /**
     * Forces this block to be considered solid for rendering and collision purposes.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D forceSolidOn() {
        this.properties.forceSolidOn();
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
        this.properties.forceSolidOff();
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
        this.properties.pushReaction(pushReaction);
        return (D) this;
    }

    /**
     * Marks this block as air (invisible, non-solid, etc.).
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D air() {
        this.properties.air();
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
        this.properties.isValidSpawn(predicate);
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
        this.properties.isRedstoneConductor(predicate);
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
        this.properties.isSuffocating(predicate);
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
        this.properties.isViewBlocking(predicate);
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
        this.properties.hasPostProcess(predicate);
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
        this.properties.emissiveRendering(predicate);
        return (D) this;
    }

    /**
     * Makes this block require the correct tool to drop items when broken.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D requiresCorrectToolForDrops() {
        this.properties.requiresCorrectToolForDrops();
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
        this.properties.destroyTime(destroyTime);
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
        this.properties.explosionResistance(explosionResistance);
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
        this.properties.offsetType(offsetType);
        return (D) this;
    }

    /**
     * Prevents this block from spawning terrain particles when walked on.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D noTerrainParticles() {
        this.properties.noTerrainParticles();
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
        this.properties.requiredFeatures(featureFlags);
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
        this.properties.instrument(instrument);
        return (D) this;
    }

    /**
     * Makes this block replaceable by other blocks during world generation and placement.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D replaceable() {
        this.properties.replaceable();
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
        this.properties.overrideDescription(descriptionKey);
        return (D) this;
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
}
