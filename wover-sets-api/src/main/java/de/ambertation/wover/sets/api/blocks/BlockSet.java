package de.ambertation.wover.sets.api.blocks;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.recipe.api.RecipeMaterial;
import de.ambertation.wover.sets.api.items.ItemSlotData;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The core "material set" builder: a themed group of blocks (and, via {@link ItemSlotFromDefinition}, items)
 * sharing a {@link BlockSetType}, such as a stone type with its slab/stairs/wall/pillar variants.
 * <p>
 * Subclass this (using a curiously-recurring-generic self-type, e.g. {@code class MySet extends BlockSet<MySet>})
 * and override {@link #createDefaultDefinitions()} to return the {@link SlotMap} of {@link SlotFactory}s the set
 * should build, typically starting from a predefined set like
 * {@code de.ambertation.wover.sets.api.blocks.slots.StoneSlots}/{@code WoodSlots} and adding/removing/replacing
 * individual slots. Call {@link #buildAndRegister()} once to build and register every configured block/item; use
 * {@link #getBlock(SlotType)}/{@link #getItem(SlotType)} afterwards to retrieve them. See
 * {@link de.ambertation.wover.sets.api.blocks.WoodenBlockSet} for the wood-specific subclass, and this module's test
 * mod ({@code TestStoneSet}/{@code TestWoodSet}) for worked examples.
 *
 * @param <S> the concrete subclass, for fluent chaining (see {@link WoodenBlockSet#setPlanksColor} on
 *            {@link de.ambertation.wover.sets.api.blocks.WoodenBlockSet})
 */
public class BlockSet<S extends BlockSet<S>> {
    protected final HashMap<SlotType, SlotData> slots;
    protected final HashMap<SlotType, ItemSlotData> itemSlots;
    /** The mod this set's blocks/items are registered under. */
    public final ModCore C;
    /** The naming prefix shared by every block/item this set registers (e.g. {@code <baseName>_slab}). */
    public final String baseName;
    /** The slot considered this set's primary/base block, used by {@link #getBaseBlock()}/{@link #recipeBaseMaterial()}. */
    public final SlotType baseSlot;

    protected final BlockSetType setType;

    /**
     * Creates a new block set and its {@link BlockSetType} (see {@link #createSetType}), but does not build or
     * register any blocks yet - call {@link #buildAndRegister()} for that.
     *
     * @param modCore  the mod this set's blocks/items are registered under
     * @param baseName the naming prefix shared by every block/item this set registers
     * @param baseSlot the slot considered this set's primary/base block
     */
    public BlockSet(@NotNull ModCore modCore, @NotNull String baseName, @NotNull SlotType baseSlot) {
        this.slots = new HashMap<>();
        itemSlots = new HashMap<>();
        this.C = modCore;
        this.baseName = baseName;
        this.baseSlot = baseSlot;

        this.setType = createSetType(null);
    }

    /**
     * Creates (or passes through) the {@link BlockSetType} this set's blocks (doors, trapdoors, buttons,
     * pressure plates, ...) share. The default implementation copies {@link BlockSetType#STONE} and registers it
     * under this set's namespace/name; {@link de.ambertation.wover.sets.api.blocks.WoodenBlockSet} overrides this to
     * derive a matching {@link net.minecraft.world.level.block.state.properties.WoodType} as well.
     *
     * @param setType an already-created set type to reuse, or {@code null} to create a new one
     * @return the set type this set should use
     */
    protected BlockSetType createSetType(BlockSetType setType) {
        if (setType == null) {
            var builder = BlockSetTypeBuilder.copyOf(BlockSetType.STONE);
            return builder.register(this.C.id(this.baseName));
        }
        return setType;
    }

    /**
     * Returns the {@link SlotMap} of {@link SlotFactory}s this set should build. Override to return a
     * customized set, typically starting from one of the predefined {@link SlotMap}s in
     * {@code de.ambertation.wover.sets.api.blocks.slots} and calling {@link SlotMap#add}/{@link SlotMap#replace}/
     * {@link SlotMap#remove} as needed. The default implementation returns an empty map.
     *
     * @return the slots to build
     */
    protected SlotMap createDefaultDefinitions() {
        return SlotMap.of();
    }

    /**
     * Clears any previously built slot data. Called automatically at the start of {@link #buildAndRegister()}.
     */
    protected void initializeSlots() {
        this.slots.clear();
        this.itemSlots.clear();
    }

    /**
     * Hook called for every block this set builds, after slot-specific configuration but before the block is
     * built. Override to apply configuration common to every block in the set (e.g. a shared material trait or
     * map color); see {@link de.ambertation.wover.sets.api.blocks.WoodenBlockSet} for an example. The default
     * implementation does nothing.
     *
     * @param slot            the slot being built
     * @param blockDefinition the definition to configure
     */
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        // This method can be overridden to add common block definitions
    }

    /**
     * Hook called for every item this set builds (see {@link ItemSlotFromDefinition}), after slot-specific
     * configuration but before the item is built. The default implementation does nothing.
     *
     * @param slot           the slot being built
     * @param itemDefinition the definition to configure
     */
    protected void addCommonItemDefinitions(SlotType slot, ItemDefinition<?, ?> itemDefinition) {
        // This method can be overridden to add common block definitions
    }

    /**
     * Builds and registers every block/item described by {@link #createDefaultDefinitions()}. Safe to call more
     * than once (each call clears and rebuilds the slot data via {@link #initializeSlots()}).
     *
     * @return this set, cast to its concrete subclass, for chaining
     */
    public S buildAndRegister() {
        final BiConsumer<SlotType, Block> acceptDefinition = (slot, block) -> {
            slots.put(slot, new SlotData(slot, block));
        };

        final BiConsumer<ItemSlotFromDefinition, ItemDefinition<?, ?>> acceptItemDefinition = (slotDefinition, itemDefinition) -> {
            Item item = itemDefinition.buildAndRegister();
            itemSlots.put(slotDefinition.slot, new ItemSlotData(slotDefinition.slot, item));
        };

        this.initializeSlots();
        final SlotMap slotDefinitions = createDefaultDefinitions();

        for (SlotFactory slotFac : slotDefinitions) {
            slotFac.createBlockDefinition(
                    this,
                    acceptDefinition::accept
            );
            if (slotFac instanceof ItemSlotFromDefinition itemSlotDefinition) {
                itemSlotDefinition.createItemDefinition(
                        this,
                        (itemDefinition) -> acceptItemDefinition.accept(itemSlotDefinition, itemDefinition)
                );
            }
        }
        return (S) this;
    }

    /**
     * Get initiated {@link Block} from this {@link BlockSet}.
     *
     * @param type         {@link SlotType} the slot you want to check
     * @param runIfPresent {@link Consumer} to run if block is present.
     * @return {@link Block} or {@code null} if nothing is stored.
     */
    @Nullable
    public Block ifBlockPresent(@NotNull SlotType type, @NotNull Consumer<Block> runIfPresent) {
        final Block block = this.getBlock(type);
        if (block != null) runIfPresent.accept(block);
        return block;
    }

    /**
     * A deferred {@link RecipeMaterial} that resolves to {@link #getBaseBlock()} once recipes are actually
     * built. Safe to call before the base block has been registered, since it defers the actual lookup - this is
     * how slot factories in {@code de.ambertation.wover.sets.api.blocks.types} build recipes for a block that
     * doesn't exist yet when the recipe trait is created.
     *
     * @return the deferred material
     */
    public RecipeMaterial recipeBaseMaterial() {
        return RecipeMaterial.ofDeferredItemLike(this::getBaseBlock);
    }

    /**
     * A deferred {@link RecipeMaterial} that resolves to the block (or, if none, the item) registered for
     * {@code type} once recipes are actually built.
     *
     * @param type the slot to resolve
     * @return the deferred material
     */
    public RecipeMaterial recipeMaterial(@NotNull SlotType type) {
        return RecipeMaterial.ofDeferredItemLike(() -> {
            var blockMat = this.getBlock(type);
            if (blockMat == null) return this.getItem(type);
            return blockMat;
        });
    }

    /**
     * A deferred {@link RecipeMaterial} that resolves to {@link #getBlockWithFallback(SlotType...)} once recipes
     * are actually built.
     *
     * @param type the slot(s) to try, in order, then {@link #baseSlot}, then vanilla stone
     * @return the deferred material
     */
    public RecipeMaterial recipeMaterialWithFallback(@NotNull SlotType... type) {
        return RecipeMaterial.ofDeferredItemLike(() -> this.getBlockWithFallback(type));
    }

    /**
     * Get the {@link Item} for the given {@link SlotType} from this set (only slots built through an
     * {@link ItemSlotFromDefinition} have an item, as opposed to a block).
     *
     * @param type {@link SlotType} the slot to check
     * @return {@link Item} or {@code null} if nothing is stored.
     */
    public @Nullable Item getItem(@NotNull SlotType type) {
        final ItemSlotData itemSlotData = itemSlots.get(type);
        if (itemSlotData != null) {
            return itemSlotData.item();
        }

        return null;
    }

    /**
     * Get the {@link Block} for the given {@link SlotType} from this set of blocks.
     * This method will return {@code null} if no block is stored for the given type.
     *
     * @param type {@link SlotType} The Block Type
     * @return {@link Block} or {@code null} if nothing is stored.
     */
    @Nullable
    public Block getBlock(@NotNull SlotType type) {
        final SlotData slotData = slots.get(type);
        if (slotData != null) {
            return slotData.block();
        }

        return null;
    }

    /**
     * Get the {@link Block} for the given {@link SlotFactory}'s slot from this set of blocks.
     * This method will return {@code null} if no block is stored for that slot.
     *
     * @param type {@link SlotFactory} whose {@link SlotFactory#slot()} to check
     * @return {@link Block} or {@code null} if nothing is stored.
     */
    @Nullable
    public Block getBlock(@NotNull SlotFactory type) {
        final SlotData slotData = slots.get(type.slot());
        if (slotData != null) {
            return slotData.block();
        }

        return null;
    }


    /**
     * Get the {@link Block} for the given {@link SlotType} from this set of blocks.
     * This method will try to find a block for the given types in the given order and return the first found block.
     * If no block is found for the given types, it will try to find a block for the {@link #baseSlot}.
     * If no block is found for the {@link #baseSlot} either, it will return a default Minecraft Stone block.
     *
     * @param type {@link SlotType} The Block Type(s) to check
     * @return {@link Block} or a default Minecraft Stone block if nothing is stored.
     */
    public @NotNull Block getBlockWithFallback(@NotNull SlotType... type) {
        // Try the given types in order, first found block will be returned
        for (SlotType slotType : type) {
            final Block block = this.getBlock(slotType);
            if (block != null) return block;
        }

        //Try the base Block slot
        final Block baseBlock = this.getBlock(this.baseSlot);
        if (baseBlock != null) return baseBlock;

        // If no block was found, return a default Minecraft Stone block
        return Blocks.STONE;
    }

    /**
     * @return the block registered for {@link #baseSlot}
     * @throws NullPointerException if no block is registered for {@link #baseSlot}
     */
    @NotNull
    public Block getBaseBlock() {
        return Objects.requireNonNull(this.getBlock(this.baseSlot));
    }

    /**
     * @return the {@link BlockSetType} shared by every door/trapdoor/button/pressure-plate this set builds
     */
    @NotNull
    public BlockSetType setType() {
        return this.setType;
    }
}
