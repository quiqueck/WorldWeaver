package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.trait.material.MetalMaterialBlockTrait;
import org.betterx.wover.block.api.trait.material.StoneMaterialBlockTrait;
import org.betterx.wover.block.api.trait.material.WoodMaterialBlockTrait;
import org.betterx.wover.block.api.trait.type.PlankBlockTrait;
import org.betterx.wover.block.api.trait.type.SlabBlockTrait;

public class BlockTraits {
    public static final FlammableBlockTrait.Builder FLAMMABLE = FlammableBlockTrait.BUILDER;

    // Material Traits
    public static final StoneMaterialBlockTrait.Builder STONE_BLOCK = StoneMaterialBlockTrait.BUILDER;
    public static final MetalMaterialBlockTrait.Builder METAL_BLOCK = MetalMaterialBlockTrait.BUILDER;
    public static final WoodMaterialBlockTrait.Builder WOOD_BLOCK = WoodMaterialBlockTrait.BUILDER;

    // Mineable traits
    public static final MineableWithTrait.Builder MINEABLE_WITH = MineableWithTrait.BUILDER;

    // Type traits
    public static final SlabBlockTrait.Builder SLAB_BLOCK = SlabBlockTrait.BUILDER;
    public static final PlankBlockTrait.Builder PLANK_BLOCK = PlankBlockTrait.BUILDER;
}
