package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.trait.behaviour.FlammableBlockTrait;
import org.betterx.wover.block.api.trait.behaviour.MineableWithTagTrait;
import org.betterx.wover.block.api.trait.behaviour.StripableBlockTrait;
import org.betterx.wover.block.api.trait.behaviour.ValidForBlockEntityTypeTrait;
import org.betterx.wover.block.api.trait.type.BarkBlockTrait;
import org.betterx.wover.block.api.trait.type.LogBlockTrait;
import org.betterx.wover.block.impl.trait.BlockRecipeTraitBuilder;
import org.betterx.wover.block.impl.trait.behaviour.FlammableBlockBuilder;
import org.betterx.wover.block.impl.trait.behaviour.MineableWithTagBuilder;
import org.betterx.wover.block.impl.trait.behaviour.StripableBlockBuilder;
import org.betterx.wover.block.impl.trait.behaviour.ValidForBlockEntityTypeBuilder;
import org.betterx.wover.block.impl.trait.material.MetalMaterialBuilder;
import org.betterx.wover.block.impl.trait.material.StoneMaterialBuilder;
import org.betterx.wover.block.impl.trait.material.WoodMaterialBuilder;
import org.betterx.wover.block.impl.trait.type.*;

public class BlockTraits {
    public static final FlammableBlockTrait.Builder FLAMMABLE = FlammableBlockBuilder.BUILDER;
    public static final StripableBlockTrait.Builder STRIPABLE = StripableBlockBuilder.BUILDER;
    public static final ValidForBlockEntityTypeTrait.Builder VALID_BLOCK_ENTITY = ValidForBlockEntityTypeBuilder.BUILDER;

    // Material Traits
    public static final GenericBlockTrait.BuilderWithDefaults STONE_BLOCK = StoneMaterialBuilder.BUILDER;
    public static final GenericBlockTrait.BuilderWithDefaults METAL_BLOCK = MetalMaterialBuilder.BUILDER;
    public static final GenericBlockTrait.BuilderWithDefaults WOOD_BLOCK = WoodMaterialBuilder.BUILDER;

    // Mineable traits
    public static final MineableWithTagTrait.Builder MINEABLE_WITH = MineableWithTagBuilder.BUILDER;

    // Type traits
    public static final BarkBlockTrait.Builder BARK_BLOCK = BarkBlockBuilder.BUILDER;
    public static final GenericBlockTrait.BuilderWithDefaults BARREL_BLOCK = BarrelBlockBuilder.BUILDER;
    public static final LogBlockTrait.Builder LOG_BLOCK = LogBlockBuilder.BUILDER;
    public static final GenericBlockTrait.BuilderWithDefault PLANK_BLOCK = PlankBlockBuilder.BUILDER;
    public static final GenericBlockTrait.BuilderWithDefault SLAB_BLOCK = SlabBlockBuilder.BUILDER;

    //Recipe Traits
    public static final BlockRecipeTrait.Builder RECIPE = BlockRecipeTraitBuilder.BUILDER;
}
