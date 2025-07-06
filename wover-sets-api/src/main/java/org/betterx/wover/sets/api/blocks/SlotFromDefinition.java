package org.betterx.wover.sets.api.blocks;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlotFromDefinition implements SlotFactory {
    public final SlotType slot;

    protected SlotFromDefinition(SlotType slot) {
        this.slot = slot;
    }

    public String getName(BlockSet<?> set) {
        return set.baseName + "_" + slot.suffix();
    }

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
        if (ModCore.isClient()) definition.addTrait(this.buildModel(set, definition));

        this.finalizeBlockDefinitions(set, definition, blockDefinitionConsumer);
    }

    protected void finalizeBlockDefinitions(
            BlockSet<?> set,
            BlockDefinition<?, ? extends BlockDefinition<?, ?>> definition,
            BiConsumer<SlotType, Block> blockDefinitionConsumer
    ) {
        var block = definition.buildAndRegister();
        blockDefinitionConsumer.accept(slot, block);
    }

    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set, @NotNull String name
    ) {
        return registry.defineDefaultBlock(name);
    }

    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
    }

    @Environment(EnvType.CLIENT)
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return null;
    }

    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return null;
    }
}
