package org.betterx.wover.sets.api.blocks;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.sets.api.blocks.slots.WoodSlots;
import org.betterx.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link BlockSet} specialized for wood-like materials: derives a {@link WoodType} alongside the
 * {@link net.minecraft.world.level.block.state.properties.BlockSetType}, maintains a shared "logs" block/item
 * tag every log-family block is added to, and applies the wood/flammable traits plus map colors common to every
 * block in the set.
 * <p>
 * {@link #createDefaultDefinitions()} returns every slot in
 * {@code org.betterx.wover.sets.api.blocks.slots.WoodSlots} by default; override to customize. See this module's
 * test mod ({@code TestWoodSet}) for a worked example.
 *
 * @param <S> the concrete subclass, for fluent chaining (see {@link #setPlanksColor})
 */
public class WoodenBlockSet<S extends WoodenBlockSet<S>> extends BlockSet<S> {
    /** The item tag every log-family block's {@code BlockItem} is added to. */
    public final TagKey<Item> logsItemTag;
    /** The block tag every log-family block is added to. */
    public final TagKey<Block> logsBlocksTag;

    /** The default map color applied to every block in this set except planks (see {@link #setPlanksColor}). */
    public final MapColor woodColor;
    private MapColor planksColor;
    private WoodType woodType;

    /**
     * Creates a new wooden block set using {@link SlotType#PLANKS} as the base slot.
     *
     * @param modCore   the mod this set's blocks/items are registered under
     * @param baseName  the naming prefix shared by every block/item this set registers
     * @param woodColor the default map color for this set's blocks
     */
    public WoodenBlockSet(
            @NotNull ModCore modCore,
            @NotNull String baseName,
            MapColor woodColor
    ) {
        this(modCore, baseName, woodColor, SlotType.PLANKS);
    }

    /**
     * Creates a new wooden block set.
     *
     * @param modCore   the mod this set's blocks/items are registered under
     * @param baseName  the naming prefix shared by every block/item this set registers
     * @param woodColor the default map color for this set's blocks
     * @param baseSlot  the slot considered this set's primary/base block
     */
    public WoodenBlockSet(
            @NotNull ModCore modCore,
            @NotNull String baseName,
            MapColor woodColor,
            @NotNull SlotType baseSlot
    ) {
        super(modCore, baseName, baseSlot);

        this.woodColor = woodColor;
        this.planksColor = woodColor;
        this.logsBlocksTag = TagManager.BLOCKS.makeTag(modCore, baseName + "_logs");
        this.logsItemTag = TagManager.ITEMS.makeTag(modCore, baseName + "_logs");
    }

    @Override
    protected BlockSetType createSetType(BlockSetType setType) {
        if (setType == null) {
            var builder = BlockSetTypeBuilder.copyOf(BlockSetType.OAK);
            setType = builder.register(this.C.id(this.baseName));
        }

        this.woodType = WoodTypeBuilder
                .copyOf(WoodType.OAK)
                .register(this.C.id(this.baseName), setType);

        return setType;
    }

    /**
     * @return the {@link WoodType} derived for this set, used for signs/fence gates/doors that need one
     */
    public WoodType woodType() {
        return this.woodType;
    }

    /**
     * Overrides the map color used for the planks block, independent of {@link #woodColor} (which every other
     * block in the set keeps using).
     *
     * @param planksColor the map color to use for the planks slot
     * @return this set, cast to its concrete subclass, for chaining
     */
    public S setPlanksColor(MapColor planksColor) {
        this.planksColor = planksColor;
        return (S) this;
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        super.addCommonBlockDefinitions(slot, blockDefinition);

        blockDefinition.addTrait(BlockTraits.WOOD_BLOCK.withDefault());
        blockDefinition.addTrait(BlockTraits.FLAMMABLE);

        if (slot == SlotType.PLANKS) {
            blockDefinition.mapColor(planksColor);
        } else {
            blockDefinition.mapColor(woodColor);
        }

    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return SlotMap.of(
                WoodSlots.BARK,
                WoodSlots.BARREL,
                WoodSlots.BOAT,
                WoodSlots.BOOKSHELF,
                WoodSlots.BUTTON,
                WoodSlots.CHEST,
                WoodSlots.CHEST_BOAT,
                WoodSlots.COMPOSTER,
                WoodSlots.CRAFTING_TABLE,
                WoodSlots.DOOR,
                WoodSlots.FENCE,
                WoodSlots.FENCE_GATE,
                WoodSlots.HANGING_SIGN,
                WoodSlots.LADDER,
                WoodSlots.LOG,
                WoodSlots.PLANKS,
                WoodSlots.PRESSURE_PLATE,
                WoodSlots.SIGN,
                WoodSlots.SLAB,
                WoodSlots.STAIRS,
                WoodSlots.STRIPPED_BARK,
                WoodSlots.STRIPPED_LOG,
                WoodSlots.TRAPDOOR
        );
    }
}
