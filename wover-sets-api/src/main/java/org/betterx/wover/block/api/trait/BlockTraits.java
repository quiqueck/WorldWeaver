package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.trait.behaviour.*;
import org.betterx.wover.block.api.trait.type.BarkBlockTrait;
import org.betterx.wover.block.api.trait.type.LogBlockTrait;
import org.betterx.wover.block.impl.trait.BlockRecipeTraitBuilder;
import org.betterx.wover.block.impl.trait.behaviour.*;
import org.betterx.wover.block.impl.trait.material.MetalMaterialBuilder;
import org.betterx.wover.block.impl.trait.material.StoneMaterialBuilder;
import org.betterx.wover.block.impl.trait.material.WoodMaterialBuilder;
import org.betterx.wover.block.impl.trait.type.*;

public class BlockTraits {
    public static final FlammableBlockTrait.Builder FLAMMABLE = FlammableBlockBuilder.BUILDER;
    public static final StripableBlockTrait.Builder STRIPABLE = StripableBlockBuilder.BUILDER;
    public static final ValidForBlockEntityTypeTrait.Builder VALID_BLOCK_ENTITY = ValidForBlockEntityTypeBuilder.BUILDER;
    public static final LootTableTrait.Builder LOOT_TABLE = LootTableTraitBuilder.BUILDER;
    public static final GenericBlockTrait.BuilderWithDefault MAGIC_SOURCE = MagicSourceTrait.BUILDER;

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
    public static final GenericBlockTrait.BuilderWithDefaults BOOK_SHELF = BookshelfBlockBuilder.BUILDER;
    public static final GenericBlockTrait.BuilderWithDefault BUTTON_BLOCK = ButtonBlockBuilder.BUILDER;
    public static final GenericBlockTrait.BuilderWithDefaults CHEST_BLOCK = ChestBlockBuilder.BUILDER;
    public static final GenericBlockTrait.BuilderWithDefault COMPOSTER_BLOCK = ComposterBlockBuilder.BUILDER;
    public static final GenericBlockTrait.BuilderWithDefault CRAFTING_TABLE_BLOCK = CraftingTableBlockBuilder.BUILDER;

    //Recipe Traits
    public static final BlockRecipeTrait.Builder RECIPE = BlockRecipeTraitBuilder.BUILDER;
}
