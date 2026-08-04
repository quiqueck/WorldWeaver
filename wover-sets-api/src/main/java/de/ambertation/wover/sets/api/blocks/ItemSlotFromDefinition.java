package de.ambertation.wover.sets.api.blocks;

import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.item.api.trait.ItemRecipeTrait;
import de.ambertation.wover.item.api.trait.ItemTrait;
import de.ambertation.wover.item.api.trait.ItemTraitLookup;

import net.minecraft.world.item.Item;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link SlotFromDefinition} variant for slots that register an {@link ItemDefinition} instead of (or in
 * addition to) a block, e.g. {@code de.ambertation.wover.sets.api.blocks.types.Boat}.
 * <p>
 * {@link SlotFromDefinition#startBlockDefinition} returns {@code null} by default for these slots (no block is
 * built); {@link BlockSet#buildAndRegister()} detects the {@link ItemSlotFromDefinition} type and additionally
 * calls {@link #createItemDefinition} to build the item side.
 */
public class ItemSlotFromDefinition extends SlotFromDefinition {
    /**
     * @param slot the slot this factory fills
     */
    protected ItemSlotFromDefinition(SlotType slot) {
        super(slot);
    }

    /**
     * Builds and registers this slot's item, then reports it back through {@code itemDefinitionConsumer}.
     * Called automatically by {@link BlockSet#buildAndRegister()} for every {@link ItemSlotFromDefinition} in
     * the set.
     *
     * @param set                   the block set this factory belongs to
     * @param itemDefinitionConsumer receives the built item definition
     */
    public void createItemDefinition(BlockSet<?> set, Consumer<ItemDefinition<?, ?>> itemDefinitionConsumer) {
        ItemDefinition<?, ? extends ItemDefinition<?, ?>> definition = startItemDefinition(
                ItemRegistry.forMod(set.C),
                this.getName(set)
        );

        if (definition == null) return;

        this.addSlotSpecificDefinitions(set, definition);
        set.addCommonItemDefinitions(this.slot, definition);

        definition.addTrait(this.buildRecipe(set, definition));
        definition.addTrait(this.buildModel(set, definition));

        itemDefinitionConsumer.accept(definition);
    }

    /**
     * Creates the initial (not yet configured) item definition for this slot. The default implementation builds
     * a plain item via {@link ItemRegistry#defineDefaultItem(String)}; override to use a specialized definition
     * (e.g. {@link ItemRegistry#defineBoatItem}).
     *
     * @param registry the registry to build the definition with
     * @param name     this slot's registration name
     * @return the new definition, or {@code null} to skip building an item for this slot
     */
    protected @Nullable ItemDefinition<?, ?> startItemDefinition(
            @NotNull ItemRegistry registry,
            @NotNull String name
    ) {
        return registry.defineDefaultItem(name);
    }

    /**
     * Hook for adding slot-specific traits/tags/properties to the item definition before it is built. The
     * default implementation does nothing.
     *
     * @param set the block set this factory belongs to
     * @param def the definition to configure
     */
    protected void addSlotSpecificDefinitions(BlockSet<?> set, ItemDefinition<?, ?> def) {
    }

    /**
     * Builds the (common, data-only) item model binding for this slot's item. The default implementation returns
     * {@code null} (no model generated). The client source set resolves the binding to a generator at datagen.
     *
     * @param set         the block set this factory belongs to
     * @param traitLookup the item's trait lookup, to query traits added earlier during configuration
     * @return the model binding, or {@code null} for none
     */
    protected ItemTrait<Item, ?> buildModel(BlockSet<?> set, ItemTraitLookup traitLookup) {
        return null;
    }

    /**
     * Builds the auto-generated recipe trait for this slot's item. The default implementation returns
     * {@code null} (no recipe generated).
     *
     * @param set         the block set this factory belongs to
     * @param traitLookup the item's trait lookup, to query traits added earlier during configuration
     * @return the recipe trait, or {@code null} for none
     */
    protected ItemRecipeTrait buildRecipe(BlockSet<?> set, ItemTraitLookup traitLookup) {
        return null;
    }
}
