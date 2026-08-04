package de.ambertation.wover.sets.api.blocks;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;

import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The standard {@link SlotFactory} base class: builds a single block through
 * {@link de.ambertation.wover.block.api.BlockDefinition}, deriving its registration name from the owning
 * {@link BlockSet}'s base name and this slot's suffix.
 * <p>
 * This is the base class extended by every block-type factory in
 * {@code de.ambertation.wover.sets.api.blocks.types} (e.g. {@code Slab}, {@code Stairs}, {@code Door}). Subclasses
 * typically override {@link #startBlockDefinition} to pick the concrete {@code Block} subclass and constructor,
 * {@link #addSlotSpecificDefinitions} to add traits/tags, {@link #buildModel} for the client-side model, and
 * {@link #buildRecipe} for the auto-generated recipe. {@link #finalizeBlockDefinitions} may be overridden for
 * factories that build more than one block per slot (see
 * {@code de.ambertation.wover.sets.api.blocks.types.Sign}/{@code HangingSign}, which report both the primary and
 * wall sign block).
 */
public class SlotFromDefinition implements SlotFactory {
    /** The slot this factory fills. */
    public final SlotType slot;

    /**
     * @param slot the slot this factory fills
     */
    protected SlotFromDefinition(SlotType slot) {
        this.slot = slot;
    }

    /**
     * @param set the block set this factory belongs to
     * @return the registration name for this slot's block, {@code <set.baseName>_<slot.suffix()>} by default
     */
    public String getName(BlockSet<?> set) {
        return set.baseName + "_" + slot.suffix();
    }

    @Override
    public SlotType slot() {
        return this.slot;
    }

    @Override
    public void createBlockDefinition(BlockSet<?> set, BiConsumer<SlotType, Block> blockDefinitionConsumer) {
        BlockDefinition<?, ? extends BlockDefinition<?, ?>> definition = startBlockDefinition(
                BlockRegistry.forMod(set.C),
                set, this.getName(set)
        );

        if (definition == null) return;

        this.addSlotSpecificDefinitions(set, definition);
        set.addCommonBlockDefinitions(this.slot, definition);

        definition.addTrait(this.buildRecipe(set, definition));
        definition.addTrait(this.buildModel(set, definition));

        this.finalizeBlockDefinitions(set, definition, blockDefinitionConsumer);
    }

    /**
     * Builds and registers {@code definition}, then reports the resulting block back to
     * {@code blockDefinitionConsumer}. Overridden by factories that need to build more than one block per slot.
     *
     * @param set                     the block set this factory belongs to
     * @param definition              the fully configured block definition
     * @param blockDefinitionConsumer receives the registered block together with its slot
     */
    protected void finalizeBlockDefinitions(
            BlockSet<?> set,
            BlockDefinition<?, ? extends BlockDefinition<?, ?>> definition,
            BiConsumer<SlotType, Block> blockDefinitionConsumer
    ) {
        var block = definition.buildAndRegister();
        blockDefinitionConsumer.accept(slot, block);
    }

    /**
     * Creates the initial (not yet configured) block definition for this slot. The default implementation
     * builds a plain block via {@link BlockRegistry#defineDefaultBlock(String)}; override to use a custom
     * {@code Block} subclass/constructor.
     *
     * @param registry the registry to build the definition with
     * @param set      the block set this factory belongs to
     * @param name     this slot's registration name
     * @return the new definition, or {@code null} to skip building a block for this slot
     */
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set, @NotNull String name
    ) {
        return registry.defineDefaultBlock(name);
    }

    /**
     * Hook for adding slot-specific traits/tags/properties to the definition before it is built. The default
     * implementation does nothing.
     *
     * @param set the block set this factory belongs to
     * @param def the definition to configure
     */
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
    }

    /**
     * Builds the (common, data-only) model binding for this slot's block. The default implementation returns
     * {@code null} (no model generated). The client source set resolves the binding to a generator at datagen.
     *
     * @param set         the block set this factory belongs to
     * @param traitLookup the block's trait lookup, to query traits added earlier during configuration
     * @return the model binding, or {@code null} for none
     */
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return null;
    }

    /**
     * Builds the auto-generated recipe trait for this slot's block. The default implementation returns
     * {@code null} (no recipe generated).
     *
     * @param set         the block set this factory belongs to
     * @param traitLookup the block's trait lookup, to query traits added earlier during configuration
     * @return the recipe trait, or {@code null} for none
     */
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return null;
    }
}
