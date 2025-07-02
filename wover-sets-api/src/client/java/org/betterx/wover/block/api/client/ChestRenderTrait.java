package org.betterx.wover.block.api.client;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;

import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;

public interface ChestRenderTrait extends BlockTrait<Block, ChestRenderTrait> {
    record ChestMaterialSet(Material single, Material left, Material right) {
    }

    interface Builder extends BlockTraitBuilder.WithDefault<Block, ChestRenderTrait> {

    }

    ChestMaterialSet getMaterial();
}
