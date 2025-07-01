package org.betterx.wover.block.api.client.trait;

import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;
import org.betterx.wover.block.impl.client.trait.BlockModelTraitBuilder;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BiPredicate;

public interface BlockModelTrait extends BlockTrait<Block, BlockModelTrait> {
    interface ModelFactory {
        void provideBlockModels(ResourceKey<Block> key, Block block, WoverBlockModelGenerators generator);
    }

    interface Builder extends BlockTraitBuilder<Block, BlockModelTrait> {
        BlockModelTrait with(ModelFactory factory);
    }

    ModelFactory modelFactory();

    static void bootstrapModels(
            ModCore modCore,
            WoverBlockModelGenerators generator
    ) {
        BlockModelTraitBuilder.bootstrapModels(modCore, generator, (k, b) -> true);
    }
    
    static void bootstrapModels(
            ModCore modCore,
            WoverBlockModelGenerators generator,
            BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        BlockModelTraitBuilder.bootstrapModels(modCore, generator, filter);
    }
}
