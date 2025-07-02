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
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;

import org.jetbrains.annotations.NotNull;

public class WoodenBlockSet<S extends WoodenBlockSet<S>> extends BlockSet<S> {
    public final TagKey<Item> logsItemTag;
    public final TagKey<Block> logsBlocksTag;

    public final MapColor woodColor;
    private MapColor planksColor;

    public WoodenBlockSet(
            @NotNull ModCore modCore,
            @NotNull String baseName,
            MapColor woodColor
    ) {
        this(modCore, baseName, woodColor, SlotType.PLANKS);
    }

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
            return builder.register(this.C.id(this.baseName));
        }
        return setType;
    }

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
            blockDefinition.getProperties().mapColor(planksColor);
        } else {
            blockDefinition.getProperties().mapColor(woodColor);
        }

    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return SlotMap.of(
                WoodSlots.BARK,
                WoodSlots.BARREL,
                WoodSlots.LOG,
                WoodSlots.PLANKS,
                WoodSlots.SLAB,
                WoodSlots.STRIPPED_BARK,
                WoodSlots.STRIPPED_LOG,
                WoodSlots.BOAT,
                WoodSlots.BOOKSHELF,
                WoodSlots.BUTTON,
                WoodSlots.CHEST,
                WoodSlots.CHEST_BOAT,
                WoodSlots.COMPOSTER,
                WoodSlots.CRAFTING_TABLE,
                WoodSlots.DOOR,
                WoodSlots.FENCE
        );
    }
}
