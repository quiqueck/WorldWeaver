package de.ambertation.wover.sets.api.blocks;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.sets.api.blocks.slots.MetalSlots;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MetalBlockSet<S extends MetalBlockSet<S>> extends BlockSet<S> {
    public MetalBlockSet(
            @NotNull ModCore modCore,
            @NotNull String baseName
    ) {
        this(modCore, baseName, SlotType.INGOT);
    }

    public MetalBlockSet(
            @NotNull ModCore modCore,
            @NotNull String baseName,
            @NotNull SlotType baseSlot
    ) {
        super(modCore, baseName, baseSlot);
    }

    protected @Nullable SoundType setTypeSound() {
        return null;
    }

    @Override
    protected BlockSetType createSetType(BlockSetType setType) {
        if (setType == null) {
            var builder = BlockSetTypeBuilder.copyOf(BlockSetType.IRON);
            SoundType setTypeSound = setTypeSound();
            if (setTypeSound != null) builder.soundType(setTypeSound);
            setType = builder.register(this.C.id(this.baseName));
        }

        return setType;
    }


    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        super.addCommonBlockDefinitions(slot, blockDefinition);
        blockDefinition
                .addTrait(BlockTraits.METAL_BLOCK)
                .mapColor(MapColor.METAL)
                .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                .strength(5.0F, 6.0F)
                .sound(SoundType.IRON);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return SlotMap.of(
                MetalSlots.CHAIN,
                MetalSlots.BARS
        );
    }
}
