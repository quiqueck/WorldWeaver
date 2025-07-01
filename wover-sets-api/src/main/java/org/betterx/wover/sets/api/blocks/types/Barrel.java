package org.betterx.wover.sets.api.blocks.types;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.model.ModelTraitLibrary;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.block.api.trait.TraitLookup;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.BarrelBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Barrel extends SlotDefinition {
    public Barrel() {
        super(SlotType.BARREL);
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(BlockRegistry registry, String name) {
        return registry.defineDefaultBlockWithProps(name, BarrelBlock::new);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.BARREL_BLOCK);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, TraitLookup traitLookup) {
        return ModelTraitLibrary.barrel();
    }
}
